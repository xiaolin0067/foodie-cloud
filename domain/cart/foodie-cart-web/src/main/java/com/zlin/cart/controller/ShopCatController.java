package com.zlin.cart.controller;

import com.zlin.cart.service.impl.CartServiceImpl;
import com.zlin.controller.BaseController;
import com.zlin.pojo.Result;
import com.zlin.pojo.ShopCartBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zlin
 * @date 20201228
 */
@Api(value = "购物车相关接口", tags = {"购物车相关接口"})
@RequestMapping("shopcart")
@RestController
public class ShopCatController extends BaseController {

    @Autowired
    private CartServiceImpl cartService;

    /**
     * 添加商品到购物车
     * @return 添加结果
     */
    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public Result add(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @ApiParam(name = "shopCartBO", value = "商品")
            @RequestBody ShopCartBO shopCartBO) {
        if (StringUtils.isBlank(userId) || shopCartBO == null) {
            return Result.errorMsg("参数为空！");
        }
        boolean result = cartService.addItemToCart(userId, shopCartBO);
        if (!result) {
            return Result.errorMsg("添加失败！");
        }
        return Result.ok();
    }

    /**
     * 删除购物车商品
     * @return 删除结果
     */
    @ApiOperation(value = "删除购物车商品", notes = "删除购物车商品", httpMethod = "POST")
    @PostMapping("/del")
    public Result del(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam String userId,
            @ApiParam(name = "itemSpecId", value = "商品规格ID")
            @RequestParam String itemSpecId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return Result.errorMsg(null);
        }
        boolean result = cartService.removeItemFromCart(userId, itemSpecId);
        if (!result) {
            return Result.errorMsg(null);
        }
        return Result.ok();
    }
}
