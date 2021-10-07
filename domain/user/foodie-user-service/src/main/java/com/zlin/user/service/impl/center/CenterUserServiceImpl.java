package com.zlin.user.service.impl.center;

import com.zlin.user.mapper.UsersMapper;
import com.zlin.user.pojo.Users;
import com.zlin.user.pojo.bo.center.CenterUserBO;
import com.zlin.user.service.center.CenterUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author zlin
 * @date 20210121
 */
@RestController
public class CenterUserServiceImpl implements CenterUserService {

    @Resource
    private UsersMapper usersMapper;

    /**
     * 查询用户信息
     *
     * @param userId 用户ID
     * @return 用户
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Users users = usersMapper.selectByPrimaryKey(userId);
        users.setPassword(null);
        return users;
    }

    /**
     * 更新用户信息
     *
     * @param userId       用户ID
     * @param centerUserBO 用户BO
     * @return 用户信息
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO) {
        Users users = new Users();
        users.setId(userId);
        users.setUpdatedTime(new Date());
        BeanUtils.copyProperties(centerUserBO, users);
        usersMapper.updateByPrimaryKeySelective(users);
        return queryUserInfo(userId);
    }

    /**
     * 更新用户头像
     *
     * @param userId  用户ID
     * @param faceUrl 用户头像
     * @return 用户信息
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserFace(String userId, String faceUrl) {
        Users users = new Users();
        users.setId(userId);
        users.setFace(faceUrl);
        users.setUpdatedTime(new Date());
        usersMapper.updateByPrimaryKeySelective(users);
        return queryUserInfo(userId);
    }
}
