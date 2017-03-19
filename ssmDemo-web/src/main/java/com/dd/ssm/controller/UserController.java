package com.dd.ssm.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dd.ssm.model.UserVO;
import com.dd.ssm.service.UserService;


//http://localhost:8080/ssmDemo-web/user?loginName=001&password=001&name=dd&birthDay=1985-01-01
//http://localhost:8080/ssmDemo-web/user?userName=dd

@Controller
@RequestMapping("user")
public class UserController {
	
	@Autowired
	private UserService userService;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	int addUser(UserVO userVO) {
		// TODO check arg
		return userService.addUser(userVO);
		// TODO 数据格式封装
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	UserVO getUserByName(String userName){
//		TODO check arg
		return userService.getUserByName(userName);
	}

}
