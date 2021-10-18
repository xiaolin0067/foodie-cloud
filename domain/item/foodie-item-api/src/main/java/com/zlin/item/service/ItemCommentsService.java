package com.zlin.item.service;

import com.zlin.pojo.PagedGridResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author zlin
 * @date 20211007
 */
@FeignClient("foodie-item-service")
@RequestMapping("item-comments-api")
public interface ItemCommentsService {

    /**
     * 分页查询我的评价列表
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 评价列表
     */
    @GetMapping("myComments")
    PagedGridResult queryMyComments(@RequestParam("userId") String userId,
                                    @RequestParam(value = "page", required = false) Integer page,
                                    @RequestParam(value = "pageSize", required = false) Integer pageSize);

    /**
     * 保存评论
     * @param map 评价
     */
    @PostMapping("saveComments")
    void saveComments(@RequestBody Map<String, Object> map);
}
