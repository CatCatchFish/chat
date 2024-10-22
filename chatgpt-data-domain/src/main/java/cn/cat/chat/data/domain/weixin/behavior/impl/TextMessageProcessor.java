package cn.cat.chat.data.domain.weixin.behavior.impl;

import cn.cat.chat.data.domain.weixin.behavior.UserBehaviorProcessor;
import cn.cat.chat.data.domain.weixin.model.entity.MessageTextEntity;
import cn.cat.chat.data.domain.weixin.model.entity.UserBehaviorMessageEntity;
import cn.cat.chat.data.types.sdk.weixin.XmlUtil;
import org.springframework.stereotype.Component;

@Component("textProcessor")
public class TextMessageProcessor extends UserBehaviorProcessor {

    @Override
    protected String handleSpecificBehavior(UserBehaviorMessageEntity messageEntity) {
        String code = weiXinRepository.genCode(messageEntity.getOpenId());

        // 反馈信息[文本]
        MessageTextEntity res = new MessageTextEntity();
        res.setToUserName(messageEntity.getOpenId());
        // 父类给出get方法，这里直接调用，就不需要重复引入originalId
        res.setFromUserName(originalId);
        res.setCreateTime(String.valueOf(System.currentTimeMillis() / 1000L));
        res.setMsgType("text");
        res.setContent(String.format("您的验证码为：%s 有效期%d分钟！", code, 3));
        return XmlUtil.beanToXml(res);
    }
}
