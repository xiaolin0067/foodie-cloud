package com.zlin.user.controller.center;

import com.zlin.pojo.Result;
import com.zlin.user.pojo.Users;
import com.zlin.user.service.center.CenterUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zlin
 * @date 20210121
 */
@Api(value = "用户中心相关接口", tags = {"用户中心相关接口"})
@RestController
@RequestMapping("center")
public class CenterController {

    @Resource
    private CenterUserService centerUserService;

    @ApiOperation(value = "查询用户信息", notes = "查询用户信息", httpMethod = "GET")
    @GetMapping("userInfo")
    public Result userInfo(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return Result.errorMsg("userId为空");
        }
        Users users = centerUserService.queryUserInfo(userId);
        return Result.ok(users);
    }

}
