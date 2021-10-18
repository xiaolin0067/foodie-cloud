package com.zlin.user.service.center;

import com.zlin.user.pojo.Users;
import com.zlin.user.pojo.bo.center.CenterUserBO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author zlin
 * @date 20210121
 */
@FeignClient("foodie-user-service")
@RequestMapping("center-user-api")
public interface CenterUserService {

    /**
     * 查询用户信息
     * @param userId 用户ID
     * @return 用户
     */
    @GetMapping("user")
    Users queryUserInfo(@RequestParam("userId") String userId);


    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param centerUserBO 用户BO
     * @return 用户信息
     */
    @PutMapping("user/{userId}")
    Users updateUserInfo(@PathVariable("userId") String userId,
                         @RequestBody CenterUserBO centerUserBO);

    /**
     * 更新用户头像
     * @param userId 用户ID
     * @param faceUrl 用户头像
     * @return 用户信息
     */
    @PutMapping("user/face")
    Users updateUserFace(@RequestParam("userId") String userId,
                         @RequestParam("faceUrl") String faceUrl);
}
