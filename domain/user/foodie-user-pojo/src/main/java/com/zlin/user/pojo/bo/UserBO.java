package com.zlin.user.pojo.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zlin
 * @date 20201221
 */
@ApiModel(value = "用户注册参数实体", description = "从客户端接收的用户注册参数实体")
public class UserBO {

    @ApiModelProperty(value = "用户名", example = "王大锤", required = true)
    private String username;
    @ApiModelProperty(value = "密码",  example = "123456", required = true)
    private String password;
    @ApiModelProperty(value = "确认密码", example = "123456")
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
