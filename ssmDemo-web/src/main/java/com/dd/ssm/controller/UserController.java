package com.dd.ssm.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dd.ssm.model.ModifyDateKey;
import com.dd.ssm.model.UserVO;
import com.dd.ssm.service.UserService;
import com.dd.util.bean.ModifyDateService;
import com.dd.util.result.ResultWithMD;
import com.dd.util.result.StatusCodeEnum;
import com.dd.util.result.UnifiedResult;


//http://localhost:8080/ssmDemo-web/user?loginName=001&password=001&name=dd&birthDay=1985-01-01
//http://localhost:8080/ssmDemo-web/user?userName=dd

@Controller
@RequestMapping("user")
public class UserController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private ModifyDateService modifyDataService;

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	int addUser(UserVO userVO) {
		// TODO check arg
		return userService.addUser(userVO);
		// TODO 数据格式封装
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	UnifiedResult<UserVO> getUserByName(String userName, Long modifyDate){
//		TODO check arg
		
		String dataKey = ModifyDateKey.getSubjectMDKey(userName);
		if (!modifyDataService.isDataModified(modifyDate, dataKey)) {
			return UnifiedResult.getInstance(StatusCodeEnum.NO_MODIFY);
		}
		UserVO result = userService.getUserByName(userName);
		
		
		return ResultWithMD.getSuccessResult(result, modifyDataService.getDataModifyDate(dataKey));
	}

}
