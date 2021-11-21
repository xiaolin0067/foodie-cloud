package com.zlin.order.fallback.itemservice;

import com.google.common.collect.Lists;
import com.zlin.item.pojo.vo.MyCommentVO;
import com.zlin.pojo.PagedGridResult;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @author zlin
 * @date 20211121
 */
@Component
// 该类不需要远程调用的降级类，随便取一个mapping名解决Ambiguous mapping错误
@RequestMapping("testppp")
public class ItemCommentsFallback implements ItemCommentsFeignClient {

    @Override
    // HystrixCommand - 可以实现多级降级
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
}
