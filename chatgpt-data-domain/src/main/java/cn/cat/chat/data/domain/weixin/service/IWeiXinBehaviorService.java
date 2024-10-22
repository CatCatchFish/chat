package cn.cat.chat.data.domain.weixin.service;

import cn.cat.chat.data.domain.weixin.model.entity.UserBehaviorMessageEntity;

public interface IWeiXinBehaviorService {
    String acceptUserBehavior(UserBehaviorMessageEntity userBehaviorMessageEntity);
}
