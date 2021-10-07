package com.zlin.item.controller;

import com.zlin.controller.BaseController;
import com.zlin.item.pojo.*;
import com.zlin.item.pojo.vo.CommentLevelCountsVO;
import com.zlin.item.pojo.vo.ItemInfoVO;
import com.zlin.item.pojo.vo.ShopCartVO;
import com.zlin.item.service.ItemService;
import com.zlin.pojo.PagedGridResult;
import com.zlin.pojo.Result;
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
@Api(value = "商品详情", tags = {"商品详情相关接口"})
@RequestMapping("items")
@RestController
public class ItemController extends BaseController {

    @Resource
    ItemService itemService;

    /**
     * 查询商品详情
     * @return 商品详情
     */
    @ApiOperation(value = "查询商品详情", notes = "查询商品详情", httpMethod = "GET")
    @GetMapping("/info/{itemId}")
    public Result info(
            @ApiParam(name = "itemId", value = "商品唯一标识", required = true)
            @PathVariable String itemId) {
        if (StringUtils.isBlank(itemId)) {
            return Result.errorMsg("商品不存在");
        }
        Items item = itemService.queryItemById(itemId);
        List<ItemsImg> itemImgList = itemService.queryItemImgList(itemId);
        List<ItemsSpec> itemSpecList = itemService.queryItemSpecList(itemId);
        ItemsParam itemParams = itemService.queryItemParam(itemId);
        return Result.ok(new ItemInfoVO(item, itemImgList, itemSpecList, itemParams));
    }

    /**
     * 查询商品评价等级数量统计
     * @return 评价等级数量统计
     */
    @ApiOperation(value = "商品评价等级数量统计", notes = "商品评价等级数量统计", httpMethod = "GET")
    @GetMapping("/commentLevel")
    public Result commentLevel(
            @ApiParam(name = "itemId", value = "商品唯一标识", required = true)
            @RequestParam String itemId) {
        if (StringUtils.isBlank(itemId)) {
            return Result.errorMsg("商品不存在");
        }
        CommentLevelCountsVO levelCountsVo = itemService.queryCommentCounts(itemId);
        return Result.ok(levelCountsVo);
    }

    /**
     * 查询商品评价
     * @return 商品评价
     */
    @ApiOperation(value = "查询商品评价", notes = "查询商品评价", httpMethod = "GET")
    @GetMapping("/comments")
    public Result comments(
            @ApiParam(name = "itemId", value = "商品唯一标识", required = true)
            @RequestParam String itemId,
            @ApiParam(name = "level", value = "评价等级")
            @RequestParam Integer level,
            @ApiParam(name = "page", value = "页码")
            @RequestParam Integer page,
            @ApiParam(name = "pageSize", value = "每页记录数")
            @RequestParam Integer pageSize) {
        if (StringUtils.isBlank(itemId)) {
            return Result.errorMsg("商品不存在");
        }
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = COMMENT_PAGE_SIZE;
        }
        PagedGridResult pagedGridResult = itemService.queryPagedComments(itemId, level, page, pageSize);
        return Result.ok(pagedGridResult);
    }

    /**
     * 根据商品规格ID查询商品信息（用于刷新购物车商品信息，主要是价格），类似淘宝京东
     * @return 商品列表
     */
    @ApiOperation(value = "根据商品规格ID查询商品信息", notes = "根据商品规格ID查询商品信息", httpMethod = "GET")
    @GetMapping("/refresh")
    public Result refresh(
            @ApiParam(name = "itemSpecIds", value = "商品规格ID，多个逗号分隔", required = true, example = "1,2,3")
            @RequestParam String itemSpecIds) {
        if (StringUtils.isBlank(itemSpecIds)) {
            return Result.ok();
        }
        List<ShopCartVO> shopCartVOList = itemService.queryItemsBySpecIds(itemSpecIds);
        return Result.ok(shopCartVOList);
    }
}
