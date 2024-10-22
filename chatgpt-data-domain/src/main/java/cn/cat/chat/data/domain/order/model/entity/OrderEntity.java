package cn.cat.chat.data.domain.order.model.entity;

import cn.cat.chat.data.domain.order.model.valobj.OrderStatusVO;
import cn.cat.chat.data.domain.order.model.valobj.PayTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    /**
     * 订单编号
     */
    private String orderId;
    /**
     * 下单时间
     */
    private Date orderTime;
    /**
     * 订单状态；0-创建完成、1-等待发货、2-发货完成、3-系统关单
     */
    private OrderStatusVO orderStatus;
    /**
     * 订单金额
     */
    private BigDecimal totalAmount;
    /**
     * 支付类型 0-支付宝 ... 待扩展
     */
    private PayTypeVO payTypeVO;
}
