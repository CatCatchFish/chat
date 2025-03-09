package cn.cat.chat.data.infrastructure.dao;

import cn.cat.chat.data.infrastructure.dao.po.OpenAIOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IOpenAIOrderDao {

    OpenAIOrder queryUnpaidOrder(OpenAIOrder order);

    void insert(OpenAIOrder order);

    void updateOrderPayInfo(OpenAIOrder order);

    int changeOrderPaySuccess(OpenAIOrder order);

    OpenAIOrder queryOrder(String orderId);

    int updateOrderStatusDeliverGoods(String orderId);

    List<String> queryReplenishmentOrder();

    List<String> queryNoPayNotifyOrder();

    List<String> queryTimeoutCloseOrderList();

    boolean changeOrderClose(String orderId);

    OpenAIOrder queryOrderByOrderId(String orderId);

    void changeMarketOrderPaySuccess(String orderId);

    void changeOrderMarketSettlement(List<String> outTradeNoList);

}
