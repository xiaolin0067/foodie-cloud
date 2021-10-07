package com.zlin.user.service.impl;

import com.zlin.enums.YesOrNo;
import com.zlin.user.mapper.UserAddressMapper;
import com.zlin.user.pojo.UserAddress;
import com.zlin.user.pojo.bo.AddressBO;
import com.zlin.user.service.AddressService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zlin
 * @date 20201225
 */
@RestController
public class AddressServiceImpl implements AddressService {

    @Resource
    UserAddressMapper userAddressMapper;
    @Resource
    Sid sid;

    /**
     * 查询用户所有收货地址
     *
     * @param userId 用户ID
     * @return 收货地址列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }

    /**
     * 用户新增地址
     *
     * @param addressBO 地址BO
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void addNewUserAddress(AddressBO addressBO) {

        int isDefault = 0;
        if (CollectionUtils.isEmpty(queryAll(addressBO.getUserId()))) {
            isDefault = 1;
        }

        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, userAddress);

        userAddress.setId(sid.nextShort());
        userAddress.setIsDefault(isDefault);
        userAddress.setCreatedTime(new Date());
        userAddress.setUpdatedTime(new Date());

        userAddressMapper.insert(userAddress);
    }

    /**
     * 用户修改地址
     *
     * @param addressBO 地址BO
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateUserAddress(AddressBO addressBO) {
        UserAddress userAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, userAddress);
        userAddress.setId(addressBO.getAddressId());
        userAddress.setUpdatedTime(new Date());

        // 为空值则不更新数据库
        userAddressMapper.updateByPrimaryKeySelective(userAddress);
    }

    /**
     * 根据用户id和地址id，删除对应的用户地址信息
     *
     * @param userId    用户ID
     * @param addressId 地址ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void deleteUserAddress(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(addressId);
        userAddress.setUserId(userId);
        userAddressMapper.delete(userAddress);
    }

    /**
     * 修改默认地址
     *
     * @param userId    用户ID
     * @param addressId 地址ID
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public void updateUserAddressToBeDefault(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setIsDefault(YesOrNo.YES.type);
        List<UserAddress> userAddressList = userAddressMapper.select(userAddress);
        for (UserAddress address : userAddressList) {
            address.setIsDefault(YesOrNo.NO.type);
            // TODO 在事务中只会提交一次到数据库？
            userAddressMapper.updateByPrimaryKeySelective(address);
        }
        UserAddress newDefaultAddress = new UserAddress();
        newDefaultAddress.setId(addressId);
        newDefaultAddress.setIsDefault(YesOrNo.YES.type);
        userAddressMapper.updateByPrimaryKeySelective(newDefaultAddress);
    }

    /**
     * 根据用户id和地址id，查询具体的用户地址对象信息
     *
     * @param userId    用户ID
     * @param addressId 地址ID
     * @return 地址信息
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setId(addressId);
        userAddress.setUserId(userId);
        return userAddressMapper.selectOne(userAddress);
    }
}
