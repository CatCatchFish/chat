package cn.cat.chat.data.domain.order.model.entity;

import cn.cat.chat.data.domain.order.model.valobj.PayStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayOrderEntity {

    /**
     * 用户ID
     */
    private String openid;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 支付地址；创建支付后，获得的URL地址
     */
    private String payUrl;
    /**
     * 支付状态；0-等待支付、1-支付完成、2-支付失败、3-放弃支付
     */
    private PayStatusVO payStatus;
    /**
     * 营销类型；0无营销、1拼团营销
     */
    private Integer marketType;
    /**
     * 营销金额；优惠金额
     */
    private BigDecimal marketDeductionAmount;
    /**
     * 支付金额；实际支付金额
     */
    private BigDecimal payAmount;

}
