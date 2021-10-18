package com.zlin.item.service;

import com.zlin.item.pojo.Items;
import com.zlin.item.pojo.ItemsImg;
import com.zlin.item.pojo.ItemsParam;
import com.zlin.item.pojo.ItemsSpec;
import com.zlin.item.pojo.vo.CommentLevelCountsVO;
import com.zlin.item.pojo.vo.ShopCartVO;
import com.zlin.pojo.PagedGridResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zlin
 * @date 20201228
 */
@FeignClient("foodie-item-service")
@RequestMapping("item-api")
public interface ItemService {

    /**
     * 通过商品ID查询商品详情
     * @param itemId 商品ID
     * @return 商品详情
     */
    @GetMapping("item")
    Items queryItemById(@RequestParam("itemId") String itemId);

    /**
     * 通过商品ID查询商品图片列表
     * @param itemId 商品ID
     * @return 商品图片列表
     */
    @GetMapping("itemImages")
    List<ItemsImg> queryItemImgList(@RequestParam("itemId") String itemId);

    /**
     * 通过商品ID查询商品规格列表
     * @param itemId 商品ID
     * @return 商品规格列表
     */
    @GetMapping("itemSpec")
    List<ItemsSpec> queryItemSpecList(@RequestParam("itemId") String itemId);

    /**
     * 通过商品ID查询商品参数
     * @param itemId 商品ID
     * @return 商品参数
     */
    @GetMapping("itemParam")
    ItemsParam queryItemParam(@RequestParam("itemId") String itemId);

    /**
     * 通过商品ID查询商品评价数量
     * @param itemId 商品ID
     * @return 评价数量
     */
    @GetMapping("countComments")
    CommentLevelCountsVO queryCommentCounts(@RequestParam("itemId") String itemId);

    /**
     * 分页查询商品评价
     * @param itemId 商品ID
     * @param level 评价等级
     * @param page 页码
     * @param pageSize 每页数量
     * @return 商品评价列表
     */
    @GetMapping("pageComments")
    PagedGridResult queryPagedComments(@RequestParam("itemId") String itemId,
                                       @RequestParam(value = "level", required = false) Integer level,
                                       @RequestParam(value = "page", required = false) Integer page,
                                       @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 根据商品规格ID查询商品信息（用于刷新购物车商品信息）
     * @param specIds 规格ID
     * @return 商品列表
     */
    @GetMapping("getCartBySpecIds")
    List<ShopCartVO> queryItemsBySpecIds(@RequestParam("specIds") String specIds);

    /**
     * 通过规格IDs查询商品规格列表
     * @param itemSpecIds 规格IDs
     * @return 商品规格列表
     */
    @GetMapping("itemSpecs")
    List<ItemsSpec> queryItemSpecListByIds(@RequestParam("itemSpecIds") String itemSpecIds);


    /**
     * 通过商品ID查询商品主图URL
     * @param itemId 商品ID
     * @return 商品主图URL
     */
    @GetMapping("primaryImage")
    String queryItemMainImgByItemId(@RequestParam("itemId") String itemId);

    /**
     * 减少商品规格库存数量
     * @param specId 商品规格ID
     * @param counts 数量
     */
    @PostMapping("decreaseStock")
    void decreaseItemSpecStock(@RequestParam("specId") String specId,
                               @RequestParam("counts") int counts);
}
