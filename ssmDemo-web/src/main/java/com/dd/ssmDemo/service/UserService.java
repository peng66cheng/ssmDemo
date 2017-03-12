package com.dd.ssmDemo.service;

import com.dd.ssmDemo.model.UserVO;

/**
 * 用户服务
 * 
 * @author dingpc
 * @date 2017年3月12日
 */
public interface UserService {
	
	/**
	 * 增加用户
	 * @param user
	 * @return
	 */
	int addUser(UserVO user);
	
	/**
	 * 通过用户名查询用户
	 * @param userName
	 * @return
	 */
	UserVO getUserByName(String userName);
}
