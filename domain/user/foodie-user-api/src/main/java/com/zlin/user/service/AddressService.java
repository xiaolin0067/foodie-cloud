package com.zlin.user.service;


import com.zlin.user.pojo.UserAddress;
import com.zlin.user.pojo.bo.AddressBO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zlin
 * @date 20201225
 */
@RestController("address-api")
public interface AddressService {

    /**
     * 查询用户所有收货地址
     * @param userId 用户ID
     * @return 收货地址列表
     */
    @GetMapping("addressList")
    List<UserAddress> queryAll(@RequestParam("userId") String userId);

    /**
     * 用户新增地址
     * @param addressBO 地址BO
     */
    @PostMapping("address")
    void addNewUserAddress(@RequestBody AddressBO addressBO);

    /**
     * 用户修改地址
     * @param addressBO 地址BO
     */
    @PutMapping("address")
    void updateUserAddress(@RequestBody AddressBO addressBO);

    /**
     * 根据用户id和地址id，删除对应的用户地址信息
     * @param userId 用户ID
     * @param addressId 地址ID
     */
    @DeleteMapping("address")
    void deleteUserAddress(@RequestParam("userId") String userId,
                           @RequestParam("addressId") String addressId);

    /**
     * 修改默认地址
     * @param userId 用户ID
     * @param addressId 地址ID
     */
    @PostMapping("setDefaultAddress")
    void updateUserAddressToBeDefault(@RequestParam("userId") String userId,
                                      @RequestParam("addressId") String addressId);

    /**
     * 根据用户id和地址id，查询具体的用户地址对象信息
     * @param userId 用户ID
     * @param addressId 地址ID
     * @return 地址信息
     */
    @GetMapping("address")
    UserAddress queryUserAddress(@RequestParam("userId") String userId,
                                 @RequestParam(value = "addressId", required = false) String addressId);
}
