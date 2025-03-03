package com.zlin.order.fallback.itemservice;

import com.google.common.collect.Lists;
import com.zlin.item.pojo.vo.MyCommentVO;
import com.zlin.pojo.PagedGridResult;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zlin
 * @date 20211121
 */
@Component
public class ItemCommentsFallbackFactory implements FallbackFactory<ItemCommentsFeignClient> {

    @Override
    public ItemCommentsFeignClient create(Throwable throwable) {
        return new ItemCommentsFeignClient() {
            @Override
            public PagedGridResult queryMyComments(String userId, Integer page, Integer pageSize) {
                MyCommentVO commentVO = new MyCommentVO();
                commentVO.setContent("正在加载中");
                PagedGridResult result = new PagedGridResult();
                result.setRows(Lists.newArrayList(commentVO));
                result.setTotal(1);
                result.setRecords(1);
                return result;
            }

            @Override
            public void saveComments(Map<String, Object> map) {
            }
        };
    }
}
