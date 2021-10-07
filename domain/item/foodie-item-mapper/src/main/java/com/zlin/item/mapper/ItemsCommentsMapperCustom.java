package com.zlin.item.mapper;

import com.zlin.item.pojo.CommentsLevelCount;
import com.zlin.item.pojo.vo.MyCommentVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author zlin
 * @date 20201229
 */
public interface ItemsCommentsMapperCustom {

    /**
     * 查询商品评价等级数量
     * @param itemId 商品ID
     * @return 评价等级数量
     */
    @Select("select comment_level as commentsLevel,COUNT(comment_level) as commentsCount from items_comments where item_id = #{itemId} GROUP BY comment_level;")
    List<CommentsLevelCount> queryCommentsLevelCount(String itemId);

    void saveComments(Map<String, Object> map);

    List<MyCommentVO> queryMyComments(@Param("paramsMap") Map<String, Object> map);
}
