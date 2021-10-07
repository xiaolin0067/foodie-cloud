package com.zlin.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.zlin.enums.CommentLevel;
import com.zlin.enums.YesOrNo;
import com.zlin.item.mapper.*;
import com.zlin.item.pojo.*;
import com.zlin.item.pojo.vo.CommentLevelCountsVO;
import com.zlin.item.pojo.vo.ItemCommentVO;
import com.zlin.item.pojo.vo.ShopCartVO;
import com.zlin.item.service.ItemService;
import com.zlin.utils.DesensitizationUtil;
import com.zlin.pojo.PagedGridResult;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zlin
 * @date 20201228
 */
@RestController
public class ItemServiceImpl implements ItemService {

    @Resource
    ItemsMapper itemsMapper;
    @Resource
    ItemsImgMapper itemsImgMapper;
    @Resource
    ItemsSpecMapper itemsSpecMapper;
    @Resource
    ItemsParamMapper itemsParamMapper;
    @Resource
    ItemsCommentsMapper itemsCommentsMapper;
    @Resource
    ItemsCommentsMapperCustom itemsCommentsMapperCustom;
    @Resource
    ItemsMapperCustom itemsMapperCustom;

    /**
     * 通过商品ID查询商品详情
     *
     * @param itemId 商品ID
     * @return 商品详情
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Items queryItemById(String itemId) {
        return itemsMapper.selectByPrimaryKey(itemId);
    }

    /**
     * 通过商品ID查询商品图片列表
     *
     * @param itemId 商品ID
     * @return 商品图片列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsImg> queryItemImgList(String itemId) {
        Example example = new Example(ItemsImg.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        return itemsImgMapper.selectByExample(example);
    }

    /**
     * 通过商品ID查询商品规格列表
     *
     * @param itemId 商品ID
     * @return 商品规格列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecList(String itemId) {
        Example example = new Example(ItemsSpec.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        return itemsSpecMapper.selectByExample(example);
    }

    /**
     * 通过商品ID查询商品参数
     *
     * @param itemId 商品ID
     * @return 商品参数
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ItemsParam queryItemParam(String itemId) {
        Example example = new Example(ItemsParam.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemId", itemId);
        return itemsParamMapper.selectOneByExample(example);
    }

    /**
     * 通过商品ID查询商品评价数量
     *
     * @param itemId 商品ID
     * @return 评价数量
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public CommentLevelCountsVO queryCommentCounts(String itemId) {
        CommentLevelCountsVO resultLevelCount = new CommentLevelCountsVO();
        // 只查询一次数据库统计评价等级数量
        List<CommentsLevelCount> levelCountList = itemsCommentsMapperCustom.queryCommentsLevelCount(itemId);
        if (!CollectionUtils.isEmpty(levelCountList)) {
            // 统计commentsCount为totalCount
            resultLevelCount.setTotalCounts(levelCountList.stream().mapToInt(CommentsLevelCount::getCommentsCount).sum());
            // 填入各等级数量
            resultLevelCount.setGoodCounts(levelCountList.stream()
                    .filter(e -> CommentLevel.GOOD.type.equals(e.getCommentsLevel()))
                    .findAny().orElse(new CommentsLevelCount()).getCommentsCount());
            resultLevelCount.setNormalCounts(levelCountList.stream()
                    .filter(e -> CommentLevel.NORMAL.type.equals(e.getCommentsLevel()))
                    .findAny().orElse(new CommentsLevelCount()).getCommentsCount());
            resultLevelCount.setBadCounts(levelCountList.stream()
                    .filter(e -> CommentLevel.BAD.type.equals(e.getCommentsLevel()))
                    .findAny().orElse(new CommentsLevelCount()).getCommentsCount());
        }
        return resultLevelCount;
    }

    /**
     * 课程中的统计方式，会多次查询数据库--弃用
     * 通过商品ID和评价级别查询评价数量
     * @param itemId 商品ID
     * @param level 评价级别
     * @return 评价数量
     */
    private int queryCommentCounts(String itemId, Integer level) {
        ItemsComments itemsComments = new ItemsComments();
        itemsComments.setItemId(itemId);
        if (level != null) {
            itemsComments.setCommentLevel(level);
        }
        return itemsCommentsMapper.selectCount(itemsComments);
    }

    /**
     * 查询商品评价
     *
     * @param itemId   商品ID
     * @param level    评价等级
     * @param page     页码
     * @param pageSize 每页数量
     * @return 商品评价列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedGridResult queryPagedComments(String itemId, Integer level, Integer page, Integer pageSize) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("itemId", itemId);
        paramsMap.put("level", level);
        PageHelper.startPage(page, pageSize);

        List<ItemCommentVO> itemComments = itemsMapperCustom.queryItemComments(paramsMap);
        for (ItemCommentVO comment : itemComments) {
            comment.setNickname(DesensitizationUtil.commonDisplay(comment.getNickname()));
        }

        return new PagedGridResult(itemComments, page);
    }

    /**
     * 根据商品规格ID查询商品信息（用于刷新购物车商品信息）
     *
     * @param specIds 规格ID
     * @return 商品列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ShopCartVO> queryItemsBySpecIds(String specIds) {
        List<String> specIdList = Arrays.stream(specIds.split(",")).collect(Collectors.toList());
//        Collections.addAll(List, str[])
        return itemsMapperCustom.queryItemsBySpecIds(specIdList);
    }

    /**
     * 通过规格IDs查询商品规格列表
     *
     * @param itemSpecIds 规格IDs
     * @return 商品规格列表
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<ItemsSpec> queryItemSpecListByIds(String itemSpecIds) {
        Example example = new Example(ItemsSpec.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", Arrays.asList(itemSpecIds.split(",")));
        return itemsSpecMapper.selectByExample(example);
    }

    /**
     * 通过商品ID查询商品主图URL
     *
     * @param itemId 商品ID
     * @return 商品主图URL
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public String queryItemMainImgByItemId(String itemId) {
        ItemsImg im = new ItemsImg();
        im.setItemId(itemId);
        im.setIsMain(YesOrNo.YES.type);
        ItemsImg itemsImg = itemsImgMapper.selectOne(im);
        return itemsImg.getUrl();
    }

    /**
     * 减少商品规格库存数量
     *
     * @param specId 商品规格ID
     * @param counts 数量
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void decreaseItemSpecStock(String specId, int counts) {

        // lockUtil.getLock(); -- 加锁
        // lockUtil.unLock();  -- 解锁

        int updateRows = itemsMapperCustom.decreaseItemSpecStock(specId, counts);
        if (updateRows != 1) {
            throw new RuntimeException("规格不存在或库存不足");
        }
    }
}
