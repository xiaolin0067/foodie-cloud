package com.zlin.user.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.zlin.controller.BaseController;
import com.zlin.enums.CacheKey;
import com.zlin.pojo.Result;
import com.zlin.pojo.ShopCartBO;
import com.zlin.user.pojo.Users;
import com.zlin.user.pojo.bo.UserBO;
import com.zlin.user.pojo.vo.UsersVO;
import com.zlin.user.service.UserService;
import com.zlin.utils.CookieUtils;
import com.zlin.utils.JsonUtils;
import com.zlin.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author zlin
 * @date 20201219
 */
@Api(value = "注册登录", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Resource
    UserService userService;

    @Resource
    private RedisOperator redisOperator;

    /**
     * 检查用户名是否存在
     * 若参数不带@RequestParam注解，swagger中Parameter content type为application/json
     * @param username 用户名
     * @return 返回结果
     */
    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "用户名", name = "username", dataType = "string", paramType = "query", required = true)
    })
    @GetMapping("/usernameIsExist")
    public Result usernameIsExist(@RequestParam String username) {
        if (StringUtils.isBlank(username)) {
            return Result.errorMsg("用户名不能为空");
        }
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist) {
            return Result.errorMsg("用户名已存在");
        }
        return Result.ok();
    }

    /**
     * 用户注册
     * @param userBO 注册信息
     * @return 返回结果
     */
    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public Result regist(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) {
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();
        // 1.用户名和密码不能为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPassword)) {
            return Result.errorMsg("用户名和密码不能为空");
        }
        //2.用户名是否存在
        if (userService.queryUsernameIsExist(username)) {
            return Result.errorMsg("用户名已存在");
        }
        //3.密码长度不能小于6位
        if (password.length() < 6) {
            return Result.errorMsg("密码长度不能小于6位");
        }
        //4.两次密码是否一致
        if (!password.equals(confirmPassword)) {
            return Result.errorMsg("两次密码不一致");
        }
        // 注册
        Users user = userService.createUser(userBO);
        // 登录信息脱敏
//        setNullProperty(user);

        // 缓存会话token，得到VO
        UsersVO usersVO = cacheTokenAndConvertVO(user);
        //登录信息缓存
        CookieUtils.setCookie(request, response, CacheKey.USER.value, JsonUtils.objectToJson(usersVO), true);

        // 同步购物车
        syncShopCart(request, response, user.getId());

        return Result.ok();
    }

    /**
     * 缓存用户会话token并转换为VO
     * @param user 用户
     * @return 用户VO
     */
    public UsersVO cacheTokenAndConvertVO(Users user) {
        String uniqueToken = UUID.randomUUID().toString().trim();
        redisOperator.set(CacheKey.USER_TOKEN.append(user.getId()), uniqueToken);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user,usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        return usersVO;
    }

    /**
     * 用户登录
     * @param userBO 登录信息
     * @return 返回结果
     */
    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    @HystrixCommand(
            commandKey = "loginFail", // 全局唯一的标识服务，默认函数名称
            groupKey = "password", // 全局服务分组，用于组织仪表盘，统计信息。默认：类名
            fallbackMethod = "loginFail", //同一个类里，public private都可以
            // 在列表中的exception，不会触发降级
//            ignoreExceptions = {IllegalArgumentException.class}
            // 线程有关的属性
            // 线程组, 多个服务可以共用一个线程组
            threadPoolKey = "threadPoolA",
            threadPoolProperties = {
                    // 核心线程数
                    @HystrixProperty(name = "coreSize", value = "10"),
                    // size > 0, LinkedBlockingQueue -> 请求等待队列
                    // 默认-1 , SynchronousQueue -> 不存储元素的阻塞队列（建议读源码，学CAS应用）
                    @HystrixProperty(name = "maxQueueSize", value = "40"),
                    // 在maxQueueSize=-1的时候无效，队列没有达到maxQueueSize依然拒绝
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "15"),
                    // （线程池）统计窗口持续时间
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "2024"),
                    // （线程池）窗口内桶子的数量
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "18"),
            }
//            ,
//            commandProperties = {
//                  // TODO 熔断降级相关属性，也可以放在这里
//            }
    )
    public Result login(@RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) {
        // request uri为nginx代理的location中配置的proxy_pass http://api.tomcats.com;中的域名
        logger.info("Request url: {}", request.getRequestURL().toString());
        String username = userBO.getUsername();
        String password = userBO.getPassword();
        // 用户名和密码不能为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            return Result.errorMsg("用户名和密码不能为空");
        }
        // 登录
        Users user = userService.queryUserForLogin(username, password);
        if (user == null) {
            return Result.errorMsg("用户名或密码错误");
        }
        // 登录信息脱敏
//        setNullProperty(user);
        // 缓存会话token，得到VO
        UsersVO usersVO = cacheTokenAndConvertVO(user);

        // 登录信息缓存
        CookieUtils.setCookie(request, response, CacheKey.USER.value, JsonUtils.objectToJson(usersVO), true);
        // 同步购物车
        syncShopCart(request, response, user.getId());

        return Result.ok(user);
    }

    public Result loginFail(UserBO userBO, HttpServletRequest request, HttpServletResponse response, Throwable th) {
        return Result.errorMsg("验证码错误（熔断降级业务）");
    }

    /**
     * 同步缓存与cookie中的购物车列表
     * @param request request
     * @param response response
     * @param userId 用户ID
     */
    public void syncShopCart(HttpServletRequest request, HttpServletResponse response, String userId){
        String shopCartCacheKey = CacheKey.SHOP_CART.append(userId);
        // 获取当前缓存和cookie中的购物车json字符串
        String cacheJson = redisOperator.get(shopCartCacheKey);
        String cookieJson = CookieUtils.getCookieValue(request, CacheKey.SHOP_CART.value, true);
        // 转换为列表
        List<ShopCartBO> cacheList = StringUtils.isBlank(cacheJson) ?
                null : JsonUtils.jsonToList(cacheJson, ShopCartBO.class);
        List<ShopCartBO> cookieList = StringUtils.isBlank(cookieJson) ?
                null : JsonUtils.jsonToList(cookieJson, ShopCartBO.class);
        // 缓存与cookie中购物车列表是否有商品
        boolean itemInCache = !CollectionUtils.isEmpty(cacheList);
        boolean itemInCookie = !CollectionUtils.isEmpty(cookieList);

        if (itemInCache && itemInCookie){
            // 缓存与cookie购物车列表都有商品，删除缓存中规格ID在cookie中存在的商品，以cookie中的商品为准
            List<String> cookieSpecIds = cookieList.stream().map(ShopCartBO::getSpecId).collect(Collectors.toList());
            cacheList.removeIf(shopCart -> cookieSpecIds.contains(shopCart.getSpecId()));
            // 合并购物车列表
            cacheList.addAll(cookieList);
            String mergeJson = JsonUtils.objectToJson(cacheList);
            CookieUtils.setCookie(request, response, CacheKey.SHOP_CART.value, mergeJson, true);
            redisOperator.set(shopCartCacheKey, mergeJson);
        }else if (itemInCache) {
            // 缓存中有商品而cookie中没有商品
            CookieUtils.setCookie(request, response, CacheKey.SHOP_CART.value, cacheJson, true);
        }else if (itemInCookie) {
            // cookie中有商品而缓存中没有商品
            redisOperator.set(shopCartCacheKey, cookieJson);
        }
        // cookie与缓存中都没有商品，无需处理
    }

    @PostMapping("/logout")
    @ApiOperation(value = "用户退出登陆", notes = "用户退出登陆", httpMethod = "POST")
    public Result logout(String userId, HttpServletRequest request, HttpServletResponse response) {
        // 清除用户相关cookie
        CookieUtils.deleteCookie(request, response, CacheKey.USER.value);
        // 用户退出登录，需要清空购物车cookie
        CookieUtils.deleteCookie(request, response, CacheKey.SHOP_CART.value);
        // 分布式会话中需要清除用户redis缓存数据
        redisOperator.del(CacheKey.USER_TOKEN.append(userId));
        return Result.ok();
    }
}
