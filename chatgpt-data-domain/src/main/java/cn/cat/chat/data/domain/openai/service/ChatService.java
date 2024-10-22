package cn.cat.chat.data.domain.openai.service;

import cn.cat.chat.data.domain.openai.model.aggregates.ChatProcessAggregate;
import cn.cat.chat.data.domain.openai.model.entity.RuleLogicEntity;
import cn.cat.chat.data.domain.openai.model.entity.UserAccountQuotaEntity;
import cn.cat.chat.data.domain.openai.model.valobj.LogicCheckTypeVO;
import cn.cat.chat.data.domain.openai.service.channel.impl.ChatGLMService;
import cn.cat.chat.data.domain.openai.service.channel.impl.ChatGPTService;
import cn.cat.chat.data.domain.openai.service.rule.ILogicFilter;
import cn.cat.chat.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class ChatService extends AbstractChatService {
    @Resource
    private DefaultLogicFactory logicFactory;

    public ChatService(ChatGPTService chatGPTService, ChatGLMService chatGLMService) {
        super(chatGPTService, chatGLMService);
    }

    @Override
    protected RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcess, UserAccountQuotaEntity userAccountQuotaEntity, String... logics) throws Exception {
        Map<String, ILogicFilter> logicFilterMap = logicFactory.openLogicFilter();
        RuleLogicEntity<ChatProcessAggregate> entity = null;
        for (String code : logics) {
            if (DefaultLogicFactory.LogicModel.NULL.getCode().equals(code)) continue;
            entity = logicFilterMap.get(code).filter(chatProcess, userAccountQuotaEntity);
            if (!LogicCheckTypeVO.SUCCESS.equals(entity.getType())) return entity;
        }
        return entity != null ? entity : RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
    }
}
