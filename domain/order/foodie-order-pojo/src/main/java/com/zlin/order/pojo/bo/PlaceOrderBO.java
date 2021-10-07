package com.zlin.order.pojo.bo;

import com.zlin.pojo.ShopCartBO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zlin
 * @date 20211007
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderBO {

    List<ShopCartBO> shopCartList;

    private SubmitOrderBO order;

}
