import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dd.ssmDemo.model.UserVO;
import com.dd.ssmDemo.service.UserService;

@Controller
@RequestMapping()
public class UserController {
	private UserService userService;

	@RequestMapping
	@ResponseBody
	int addUser(UserVO userVO) {
		// TODO check arg
		return userService.addUser(userVO);
		// TODO 数据格式封装
	}

}
