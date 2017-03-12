package com.dd.ssmDemo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dd.ssmDemo.dao.UserMapper;
import com.dd.ssmDemo.domain.User;
import com.dd.ssmDemo.model.UserVO;
import com.dd.ssmDemo.service.UserService;
import com.dd.util.BeanCopyUtils;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userDao;

	@Override
	public int addUser(UserVO user) {
		// TODO check args
		User userDO = BeanCopyUtils.convertClass(user, User.class);
		return userDao.insert(userDO);
	}

	@Override
	public UserVO getUserByName(String userName) {
		// TODO check args
		User user = userDao.selectByselectByName(userName);
		return BeanCopyUtils.convertClass(user, UserVO.class);
	}

}
