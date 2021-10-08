package com.zlin.cart.service;

import com.zlin.pojo.ShopCartBO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zlin
 * @date 20211007
 */
@RequestMapping("cart-api")
public interface CartService {

    /**
     * 用户添加购物车
     */
    @PostMapping("addItem")
    boolean addItemToCart(@RequestParam("userId") String userId,
                          @RequestBody ShopCartBO shopCartBO);

    /**
     * 移除用户购物车
     */
    @PostMapping("removeItem")
    boolean removeItemFromCart(@RequestParam("userId") String userId,
                               @RequestParam("itemSpecId") String itemSpecId);

    /**
     * 清空用户购物车
     */
    @PostMapping("clearCart")
    boolean clearCart(@RequestParam("userId") String userId);

}
