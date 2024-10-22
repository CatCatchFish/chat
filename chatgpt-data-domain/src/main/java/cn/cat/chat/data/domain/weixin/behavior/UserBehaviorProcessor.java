package cn.cat.chat.data.domain.weixin.behavior;

import cn.cat.chat.data.domain.weixin.model.entity.UserBehaviorMessageEntity;
import cn.cat.chat.data.domain.weixin.model.valobj.MsgTypeVO;
import cn.cat.chat.data.domain.weixin.repository.IWeiXinRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Cat
 */
public abstract class UserBehaviorProcessor {
    @Value("${wx.config.originalid}")
    protected String originalId;
    @Resource
    protected IWeiXinRepository weiXinRepository;

    // 模板方法，定义处理逻辑的骨架
    public String process(UserBehaviorMessageEntity messageEntity) {
        // 处理用户行为
        if (MsgTypeVO.EVENT.getCode().equals(messageEntity.getMsgType())) {
            return handleEvent(messageEntity); // 处理事件类型
        }
        return handleSpecificBehavior(messageEntity); // 处理具体消息类型
    }

    protected String handleEvent(UserBehaviorMessageEntity messageEntity) {
        return "";
    }

    // 留给子类实现的处理方法
    protected abstract String handleSpecificBehavior(UserBehaviorMessageEntity messageEntity);
}
