package cn.cat.chat.data.domain.order.model.entity;

import cn.cat.chat.data.domain.order.model.valobj.MarketTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopCartEntity {

    /**
     * 用户微信唯一ID
     */
    private String openid;
    /**
     * 商品ID
     */
    private Integer productId;
    /**
     * 拼团组队ID
     */
    private String teamId;
    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 营销类型
     */
    private MarketTypeVO marketType;

}
