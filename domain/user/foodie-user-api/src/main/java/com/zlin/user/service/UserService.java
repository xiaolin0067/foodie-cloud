package com.zlin.user.service;

import com.zlin.user.pojo.Users;
import com.zlin.user.pojo.bo.UserBO;
import org.springframework.web.bind.annotation.*;

/**
 * @author zlin
 * @date 20201219
 */
@RequestMapping("user-api")
public interface UserService {

    /**
     * 判断用户名是否已存在
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    @GetMapping("username/exists")
    boolean queryUsernameIsExist(@RequestParam("username") String username);

    /**
     * 用户注册
     * @param userBO 前端参数
     * @return 用户
     */
    @PostMapping("user")
    Users createUser(@RequestBody UserBO userBO);

    /**
     * 用户登录，检查用户名密码是否匹配
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */
    @GetMapping("verify")
    Users queryUserForLogin(@RequestParam("username") String username,
                            @RequestParam("password") String password);
}
