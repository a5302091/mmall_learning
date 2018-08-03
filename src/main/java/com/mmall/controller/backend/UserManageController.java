package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPollUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        ServerResponse<User> login = iUserService.login(username, password);
        if (login.isSuccess()) {
            User user = login.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                //说明登录的是管理员
                // session.setAttribute(Const.CURRENT_USER,user);
                CookieUtil.writeLoginToken(response, session.getId());
                RedisShardedPollUtil.setEx(session.getId(), JsonUtil.obj2String(login), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
                return login;
            } else {
                return ServerResponse.createByErrorMessage("不是管理员,无法登录");
            }
        }
        return login;
    }

}
