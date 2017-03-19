package com.dd.ssm.service.impl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.support.HandlerMethodInvocationException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.dd.util.StatusCodeEnum;
import com.dd.util.UnifiedResult;

/**
 * spring mvc 统一异常处理器
 * 
 * @author dingpc
 *
 */
@Service("unifiedExceptionResolver")
public class UnifiedExceptionResolver implements HandlerExceptionResolver, Ordered {

	private static final Logger logger = Logger.getLogger(UnifiedExceptionResolver.class);

	/**
	 * HandlerExceptionResolver 处理顺序
	 */
	private int order = Ordered.HIGHEST_PRECEDENCE;

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		/** 业务异常 */
//		if (ex instanceof BusinessException) {// 业务异常
//			returnResult(response, UnifiedResult.getInstance(StatusCode.ILLEGAL_ARGUMENT));
//			return new ModelAndView();
//		}
		/** 参数异常 */
		if (ex instanceof IllegalArgumentException || ex instanceof TypeMismatchException
				|| ex instanceof HandlerMethodInvocationException
				|| ex instanceof HttpRequestMethodNotSupportedException || ex instanceof IllegalStateException) {
			printHttpServletRequest(request);
			logger.warn("捕获参数异常", ex);
			returnResult(response, UnifiedResult.getInstance(StatusCodeEnum.ILLEGAL_ARGUMENT));
			return new ModelAndView();
		}

		/** 系统内部异常 **/
		if (ex instanceof Throwable) {
			printHttpServletRequest(request);
			logger.warn("捕获未知异常:", ex);
			returnResult(response, UnifiedResult.getUnkownErrorResult());
			return new ModelAndView();
		}
		return null;
	}
	
	private void printHttpServletRequest(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		sb.append("【url=").append(request.getRequestURL());
		sb.append("; params=").append(JSON.toJSONString(request.getParameterMap()));
		sb.append("; cookie=").append(JSON.toJSONString(request.getCookies())).append("】");
		
		logger.warn(sb.toString());
	}

	/**
	 * 返回结果
	 * 
	 * @param response
	 * @param returnResult
	 */
	protected void returnResult(HttpServletResponse response, Object returnResult) {
		PrintWriter writer = null;
		try {
			String json = JSON.toJSONString(returnResult);
			response.setCharacterEncoding("UTF-8");
			writer = response.getWriter();
			writer.write(json);
			writer.flush();
		} catch (IOException e) {
			logger.warn("返回信息异常", e);
		}
	}


}
