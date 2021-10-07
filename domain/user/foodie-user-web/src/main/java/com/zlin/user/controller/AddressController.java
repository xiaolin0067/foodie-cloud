package com.zlin.user.controller;

import com.zlin.controller.BaseController;
import com.zlin.pojo.Result;
import com.zlin.user.pojo.UserAddress;
import com.zlin.user.pojo.bo.AddressBO;
import com.zlin.user.service.AddressService;
import com.zlin.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zlin
 * @date 20201228
 */
@Api(value = "地址相关接口", tags = {"地址相关接口"})
@RequestMapping("address")
@RestController
public class AddressController extends BaseController {

    @Resource
    AddressService addressService;

    /**
     * 查询收货地址列表
     * @return 地址列表
     */
    @ApiOperation(value = "查询收货地址列表", notes = "查询收货地址列表", httpMethod = "POST")
    @PostMapping("/list")
    public Result list(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId) {
        if (StringUtils.isBlank(userId)) {
            return Result.errorMsg(null);
        }
        List<UserAddress> userAddressList = addressService.queryAll(userId);
        return Result.ok(userAddressList);
    }

    /**
     * 新增收货地址
     * @return 结果
     */
    @ApiOperation(value = "新增收货地址", notes = "新增收货地址", httpMethod = "POST")
    @PostMapping("/add")
    public Result add(@RequestBody AddressBO addressBO) {
        Result result = checkAddress(addressBO);
        if (!result.isOK()) {
            return result;
        }
        addressService.addNewUserAddress(addressBO);
        return Result.ok();
    }

    /**
     * 修改收货地址
     * @return 结果
     */
    @ApiOperation(value = "修改收货地址", notes = "修改收货地址", httpMethod = "POST")
    @PostMapping("/update")
    public Result update(@RequestBody AddressBO addressBO) {
        if (StringUtils.isBlank(addressBO.getAddressId())) {
            return Result.errorMsg("addressId为空");
        }
        Result result = checkAddress(addressBO);
        if (!result.isOK()) {
            return result;
        }
        addressService.updateUserAddress(addressBO);
        return Result.ok();
    }

    /**
     * 删除收货地址
     * @return 结果
     */
    @ApiOperation(value = "删除收货地址", notes = "删除收货地址", httpMethod = "POST")
    @PostMapping("/delete")
    public Result delete(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @ApiParam(name = "addressId", value = "地址ID", required = true)
            @RequestParam String addressId) {
        if (StringUtils.isBlank(addressId) || StringUtils.isBlank(userId)) {
            return Result.errorMsg("userId或addressId为空");
        }
        addressService.deleteUserAddress(userId, addressId);
        return Result.ok();
    }

    /**
     * 设置默认收货地址
     * @return 结果
     */
    @ApiOperation(value = "设置默认收货地址", notes = "设置默认收货地址", httpMethod = "POST")
    @PostMapping("/setDefalut")
    public Result setDefalut(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @ApiParam(name = "addressId", value = "地址ID", required = true)
            @RequestParam String addressId) {
        if (StringUtils.isBlank(addressId) || StringUtils.isBlank(userId)) {
            return Result.errorMsg("userId或addressId为空");
        }
        addressService.updateUserAddressToBeDefault(userId, addressId);
        return Result.ok();
    }

    private Result checkAddress(AddressBO addressBO) {
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)) {
            return Result.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12) {
            return Result.errorMsg("收货人姓名不能太长");
        }

        String mobile = addressBO.getMobile();
        if (StringUtils.isBlank(mobile)) {
            return Result.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11) {
            return Result.errorMsg("收货人手机号长度不正确");
        }
        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk) {
            return Result.errorMsg("收货人手机号格式不正确");
        }

        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province) ||
                StringUtils.isBlank(city) ||
                StringUtils.isBlank(district) ||
                StringUtils.isBlank(detail)) {
            return Result.errorMsg("收货地址信息不能为空");
        }

        return Result.ok();
    }
}
