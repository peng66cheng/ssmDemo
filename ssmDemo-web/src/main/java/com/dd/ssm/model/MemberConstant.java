package com.dd.ssm.model;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dd.util.crypt.Crypt;


/**
 * 
 * 
 * @author dingpc
 * @date 2017年3月21日
 */
public final class MemberConstant {

	public static final String BREAK_SIGN = ":";

	/**
	 * 用户登录token
	 */
	private static final String MEMBER_LOGIN_SET = "MEMBER:LOGIN:SOURCE:";
	
	/**
	 * token前缀
	 */
	private static final String TOKEN_PRE = "COURSE:CENTER:MEMBER:";
	
	/**
	 * token 状态属性
	 */
	public static final String TOKEN_STATUS_ATTR = "STATUS";
	/**
	 * token 登录名属性
	 */
	public static final String TOKEN_LOGIN_NAME_ATTR = "LOGIN_NAME";
	
	public static String getTokenKey(String loginName, String source){
		return MemberConstant.MEMBER_LOGIN_SET + source + MemberConstant.BREAK_SIGN + loginName;
	}
	

	public static String getTokenName(String loginName) {
		return MemberConstant.TOKEN_PRE + replaceSpecialChar(Crypt.encryptToDES(loginName + ":" + new Date().getTime()));
	}
	
	/**
	 * 方法说明：特殊字符替换
	 * 
	 * @param data
	 *            数据
	 * @return 特殊字符
	 */
	private static String replaceSpecialChar(String data) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(data);
		return m.replaceAll("a").trim();
	}
	

}
