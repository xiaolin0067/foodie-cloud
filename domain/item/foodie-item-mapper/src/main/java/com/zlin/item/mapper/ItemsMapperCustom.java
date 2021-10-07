package com.zlin.item.mapper;

import com.zlin.item.pojo.vo.ItemCommentVO;
import com.zlin.item.pojo.vo.ShopCartVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author zlin
 * @date 20210105
 */
public interface ItemsMapperCustom {

    List<ItemCommentVO> queryItemComments(@Param("paramsMap") Map<String, Object> paramsMap);

    List<ShopCartVO> queryItemsBySpecIds(@Param("paramsList") List<String> paramsList);

    int decreaseItemSpecStock(@Param("specId") String specId,
                              @Param("counts") int counts);
}
