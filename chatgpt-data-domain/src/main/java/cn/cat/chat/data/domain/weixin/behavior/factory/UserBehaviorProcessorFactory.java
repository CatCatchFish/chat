package cn.cat.chat.data.domain.weixin.behavior.factory;

import cn.cat.chat.data.domain.weixin.behavior.UserBehaviorProcessor;
import cn.cat.chat.data.domain.weixin.model.entity.UserBehaviorMessageEntity;
import cn.cat.chat.data.types.exception.ChatGPTException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class UserBehaviorProcessorFactory {
    @Resource
    private Map<String, UserBehaviorProcessor> processorMap;

    public UserBehaviorProcessor getProcessor(UserBehaviorMessageEntity messageEntity) {
        // 可以扩展其他类型的消息处理器
        // else if (MsgTypeVO.IMAGE.getCode().equals(messageEntity.getMsgType())) {
        //     return new ImageMessageProcessor(originalId);
        // }
        try {
            return processorMap.get(messageEntity.getMsgType() + "Processor");
        } catch (Exception e) {
            throw new ChatGPTException(messageEntity.getMsgType() + " 未被处理的行为类型 Err！");
        }
    }
}
