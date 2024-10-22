package cn.cat.chat.data.domain.order.model.entity;

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

    @Override
    public String toString() {
        return "ShopCartEntity{" +
                "openid='" + openid + '\'' +
                ", productId=" + productId +
                '}';
    }

}
