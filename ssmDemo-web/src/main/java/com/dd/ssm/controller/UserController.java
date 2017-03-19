package com.dd.ssm.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dd.ssm.model.UserVO;
import com.dd.ssm.service.UserService;

@Controller
@RequestMapping("user")
public class UserController {
	
	@Autowired
	private UserService userService;

	@RequestMapping
	@ResponseBody
	int addUser(UserVO userVO) {
		// TODO check arg
		return userService.addUser(userVO);
		// TODO 数据格式封装
	}

}
