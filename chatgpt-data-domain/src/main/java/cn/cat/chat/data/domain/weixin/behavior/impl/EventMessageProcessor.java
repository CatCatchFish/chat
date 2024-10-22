package cn.cat.chat.data.domain.weixin.behavior.impl;

import cn.cat.chat.data.domain.weixin.model.entity.UserBehaviorMessageEntity;
import cn.cat.chat.data.domain.weixin.behavior.UserBehaviorProcessor;
import org.springframework.stereotype.Component;

@Component("eventProcessor")
public class EventMessageProcessor extends UserBehaviorProcessor {
    @Override
    protected String handleSpecificBehavior(UserBehaviorMessageEntity messageEntity) {
        return "";
    }
}
