package cn.cat.chat.data.domain.order.adapter.port;

import cn.cat.chat.data.domain.order.model.entity.MarketPayDiscountEntity;

import java.util.Date;

public interface IProductPort {

    MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId);

    void settlementMarketPayOrder(String userId, String orderId, Date orderTime);

}
