package cn.cat.chat.data.domain.weixin.service.message;

import cn.cat.chat.data.domain.weixin.behavior.factory.UserBehaviorProcessorFactory;
import cn.cat.chat.data.domain.weixin.model.entity.UserBehaviorMessageEntity;
import cn.cat.chat.data.domain.weixin.service.IWeiXinBehaviorService;
import cn.cat.chat.data.domain.weixin.behavior.UserBehaviorProcessor;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class WeiXinBehaviorService implements IWeiXinBehaviorService {
    @Resource
    private UserBehaviorProcessorFactory processorFactory;

    @Override
    public String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity) {
        // 根据消息类型获取处理器
        UserBehaviorProcessor processor = processorFactory.getProcessor(userBehaviorMessageEntity);
        // 使用模板方法处理消息
        return processor.process(userBehaviorMessageEntity);
    }
}
