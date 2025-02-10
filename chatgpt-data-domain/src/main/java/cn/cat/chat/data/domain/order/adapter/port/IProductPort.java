package cn.cat.chat.data.domain.order.adapter.port;

import cn.cat.chat.data.domain.order.model.entity.MarketPayDiscountEntity;

public interface IProductPort {

    MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId);

}
