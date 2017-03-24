package com.dd.ssm.service.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.dd.ssm.model.CookieConfig;
import com.dd.ssm.model.LoginStatus;
import com.dd.ssm.model.MemberConstant;
import com.dd.util.result.StatusCodeEnum;

import redis.clients.jedis.JedisCluster;

/**
 * 登陆拦截器
 * 
 * @author dingpc
 * @date 2017年3月24日
 */
@Service("loginInterceptor")
public class LoginInterceptor extends HandlerInterceptorAdapter {

	private static final Logger logger = Logger.getLogger(LoginInterceptor.class);

	/**
	 * token标识 用于记录用户的一次登录
	 */
	private static final String TOKEN = "token";
	/**
	 * 未登录处理策略
	 */
	private static final String UNLOGIN_STRATEGY = "unloginStrategy";

	/**
	 * 回退
	 */
	private static final String UNLOGIN_STRATEGY_GOBACK = "goback";

	/**
	 * 跳转至登录页
	 */
	private static final String UNLOGIN_STRATEGY_LOGIN = "redirecLogin";

	private static final String UNLOGIN_STRATEGY_RETURN_CODE = "returnCode";

	private static final String DEFAULT_LOGIN_ADDRESS = "wike.daydays.com";

	/**
	 * 默认的登录超时时间
	 */
	private final static int DEFAULT_LOGIN_TIME_OUT = 60 * 60 * 24;

	@Autowired
	private JedisCluster jedisCluster;

	private static final ThreadLocal<String> loginUserName = new ThreadLocal<String>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String sessionKey = getSessionKey(request);
		if (StringUtils.isEmpty(sessionKey) || !jedisCluster.exists(sessionKey)) {
			unLoginHandle(request, response, StatusCodeEnum.UN_LOGIN);
			return false;
		}
		String tokenStatus = jedisCluster.hget(sessionKey, MemberConstant.TOKEN_STATUS_ATTR);
		if (LoginStatus.KICKED.name().equals(tokenStatus)) {
			unLoginHandle(request, response, StatusCodeEnum.KICKED);
			return false;
		}
		String loginName =  jedisCluster.hget(sessionKey, MemberConstant.TOKEN_LOGIN_NAME_ATTR);
		if (!StringUtils.hasText(loginName)) {
			logger.warn("getLoginUser is null, sessionKey : " + sessionKey);
			return false;
		}
		this.setCurrentLoginUser(loginName);
		resetExpireTime(sessionKey);
		return true;

	}

	/**
	 * 未登录处理
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void unLoginHandle(HttpServletRequest request, HttpServletResponse response, StatusCodeEnum status) throws IOException {
		if (StringUtils.isEmpty(request.getParameter(UNLOGIN_STRATEGY))
				|| request.getParameter(UNLOGIN_STRATEGY).equals(UNLOGIN_STRATEGY_RETURN_CODE)) {// 未登录策略为空，或者未登录策略为“返回码策略”
			returnUnloginError(response, getReturnStr(status));
		} else if (UNLOGIN_STRATEGY_GOBACK.equals(request.getParameter(UNLOGIN_STRATEGY))) {// 未登录策略为“退回到来源地址”
			redirect(request, response, getGoBackUrl(request));
		} else if (UNLOGIN_STRATEGY_LOGIN.equals(request.getParameter(UNLOGIN_STRATEGY))) {// 未登录策略为“跳转到登录页面”
			redirect(request, response, DEFAULT_LOGIN_ADDRESS);
		}
	}

	/**
	 * 获取“未登录的”失败信息
	 * 
	 * @param response
	 * @throws IOException
	 */
	private void returnUnloginError(HttpServletResponse response,String returnStr) throws IOException {
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(returnStr);
	}

	// 重新设置登录超时时间
	public void resetExpireTime(String sessionKey) {
		jedisCluster.expire(sessionKey, DEFAULT_LOGIN_TIME_OUT);
	}

	/**
	 * 
	 * 重定向 到指定地址
	 * 
	 * @param request
	 * @param response
	 * @param directUrl
	 */
	private void redirect(HttpServletRequest request, HttpServletResponse response, String directUrl) {
		if (StringUtils.isEmpty(directUrl)) {
			directUrl = DEFAULT_LOGIN_ADDRESS;
		}
		response.setHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0);
		response.setHeader("Pragma", "no-cache");
		try {
			response.sendRedirect(directUrl);
		} catch (IOException e) {
			logger.warn("redirect error,directUrl : " + directUrl, e);
		}
	}

	/**
	 * 获取跳回的url
	 * 
	 * @param request
	 * @return
	 */
	private String getGoBackUrl(HttpServletRequest request) {
		String basePath = new StringBuffer().append(request.getScheme()).append("://").append(request.getServerName())
				.append(":").append(request.getServerPort()).append(request.getContextPath())
				.append(request.getHeader("Referer")).toString();
		return basePath;
	}

	/**
	 * 获取sessionKey 先从cookie中获取，如果没有，再从tocken字段获取
	 * 
	 * @param request
	 * @return
	 */
	public static String getSessionKey(HttpServletRequest request) {
		String key = request.getParameter(TOKEN);
		if (StringUtils.isEmpty(key)) {// 无法通过cookie获取sessionKey，则通过请求参数tocken获取。
			key = getSessionKeyByCookies(request);
		}
		return key;
	}

	/**
	 * 通过cookie获取sessionKey
	 * 
	 * @param request
	 * @return
	 */
	private static String getSessionKeyByCookies(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		try {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(CookieConfig.SESSION_KEY)) {
					return URLDecoder.decode(cookie.getValue(), "UTF-8");
				}
			}
		} catch (UnsupportedEncodingException e) {
			logger.warn("UnsupportedEncodingException", e);
		}
		return null;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		super.postHandle(request, response, handler, modelAndView);
		loginUserName.remove();
	}

	private String getReturnStr(StatusCodeEnum status) {
		return "{\"data\":null,\"stateCode\":\"" + status.getCode() + "\",\"retMessage\":\"" + status.getMessage()
				+ "\"}";
	}

	/**
	 * 设置当前登录用户
	 * 
	 * @param loginUser
	 */
	private void setCurrentLoginUser(String loginUser) {
		loginUserName.set(loginUser);
	}

	/**
	 * 获取当前登录用户
	 * 
	 * @return
	 */
	public String getCurrentLoginUser() {
		return loginUserName.get();
	}

}