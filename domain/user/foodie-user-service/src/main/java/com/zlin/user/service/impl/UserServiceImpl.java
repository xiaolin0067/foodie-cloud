package com.zlin.user.service.impl;

import com.zlin.enums.Sex;
import com.zlin.user.mapper.UsersMapper;
import com.zlin.user.pojo.Users;
import com.zlin.user.pojo.bo.UserBO;
import com.zlin.user.service.UserService;
import com.zlin.utils.DateUtil;
import com.zlin.utils.MD5Utils;
import org.n3r.idworker.Sid;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * @author zlin
 * @date 20201219
 */
@RestController
public class UserServiceImpl implements UserService {

    @Resource
    UsersMapper usersMapper;

    @Resource
    Sid sid;

    private static final String USER_FACE = "http://122.152.205.72:88/group1/M00/00/05/CpoxxFw_8_qAIlFXAAAcIhVPdSg994.png";

    /**
     * 判断用户名是否已存在
     * @return true-存在，false-不存在
     */
    @Transactional(propagation = Propagation.SUPPORTS,rollbackFor = Exception.class)
    @Override
    public boolean queryUsernameIsExist(String username) {
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        int count = usersMapper.selectCountByExample(userExample);
        return count > 0;
    }

    /**
     * 用户注册
     * @param userBO 前端参数
     * @return 用户
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    @Override
    public Users createUser(UserBO userBO) {
        Users user = new Users();
        user.setId(sid.nextShort());
        user.setUsername(userBO.getUsername());
        try {
            user.setPassword(MD5Utils.getMd5Str(userBO.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // 默认用户昵称同用户名
        user.setNickname(userBO.getUsername());
        // 默认头像
        user.setFace(USER_FACE);
        // 默认生日
        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        // 默认性别为 保密
        user.setSex(Sex.secret.type);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        usersMapper.insert(user);
        return user;
    }

    /**
     * 用户登录，检查用户名密码是否匹配
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */
    @Transactional(propagation = Propagation.SUPPORTS,rollbackFor = Exception.class)
    @Override
    public Users queryUserForLogin(String username, String password) {
        try {
            password = MD5Utils.getMd5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Example userExample = new Example(Users.class);
        Example.Criteria userCriteria = userExample.createCriteria();
        userCriteria.andEqualTo("username", username);
        userCriteria.andEqualTo("password", password);
        return usersMapper.selectOneByExample(userExample);
    }
}
