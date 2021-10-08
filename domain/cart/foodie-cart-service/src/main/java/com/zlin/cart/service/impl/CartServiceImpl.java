package com.zlin.cart.service.impl;

import com.zlin.cart.service.CartService;
import com.zlin.enums.CacheKey;
import com.zlin.pojo.ShopCartBO;
import com.zlin.utils.JsonUtils;
import com.zlin.utils.RedisOperator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zlin
 * @date 20211007
 */
@RestController
@Slf4j
public class CartServiceImpl implements CartService {

    @Resource
    private RedisOperator redisOperator;

    @Override
    public boolean addItemToCart(String userId, ShopCartBO shopCartBO) {
        String reqShopCartJson = JsonUtils.objectToJson(shopCartBO);
        log.info("添加商品到购物车 userId：{}, shopCartBO：{}", userId, reqShopCartJson);

        // 在用户登录的情况下，需要同步购物车到Redis,若当前购物中存在该商品，则数量累加
        String shopCartKey = CacheKey.SHOP_CART.append(userId);
        String shopCartJson = redisOperator.get(shopCartKey);
        List<ShopCartBO> shopCartList;
        if (StringUtils.isNotBlank(shopCartJson)) {
            shopCartList = JsonUtils.jsonToList(shopCartJson, ShopCartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话counts累加
            boolean isHaving = false;
            for (ShopCartBO shopCart : shopCartList) {
                if (shopCart.getSpecId().equals(shopCartBO.getSpecId())) {
                    shopCart.setBuyCounts(shopCart.getBuyCounts() + shopCartBO.getBuyCounts());
                    isHaving = true;
                    break;
                }
            }
            if (!isHaving) {
                shopCartList.add(shopCartBO);
            }
        } else {
            shopCartList = new ArrayList<>();
            shopCartList.add(shopCartBO);
        }
        // 覆盖现有redis中的购物车
        redisOperator.set(shopCartKey, JsonUtils.objectToJson(shopCartList));
        return true;
    }

    @Override
    public boolean removeItemFromCart(String userId, String itemSpecId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return false;
        }
        log.info("删除购物车商品 userId：{}, itemSpecId：{}", userId, itemSpecId);

        // 在用户登录的情况下，需要同步购物车到Redis
        String shopCartKey = CacheKey.SHOP_CART.append(userId);
        String shopCartJson = redisOperator.get(shopCartKey);
        if (StringUtils.isBlank(shopCartJson)) {
            return true;
        }
        List<ShopCartBO> shopCartList = JsonUtils.jsonToList(shopCartJson, ShopCartBO.class);
        if (CollectionUtils.isEmpty(shopCartList)) {
            return true;
        }
        shopCartList.removeIf(shopCart -> itemSpecId.equals(shopCart.getSpecId()));
        redisOperator.set(shopCartKey, JsonUtils.objectToJson(shopCartList));
        return true;
    }

    @Override
    public boolean clearCart(String userId) {
        String shopCartKey = CacheKey.SHOP_CART.append(userId);
        redisOperator.del(shopCartKey);
        return false;
    }
}
