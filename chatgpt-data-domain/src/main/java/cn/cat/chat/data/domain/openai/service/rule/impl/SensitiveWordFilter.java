package cn.cat.chat.data.domain.openai.service.rule.impl;

import cn.cat.chat.data.domain.openai.annotation.LogicStrategy;
import cn.cat.chat.data.domain.openai.model.aggregates.ChatProcessAggregate;
import cn.cat.chat.data.domain.openai.model.entity.MessageEntity;
import cn.cat.chat.data.domain.openai.model.entity.RuleLogicEntity;
import cn.cat.chat.data.domain.openai.model.entity.UserAccountQuotaEntity;
import cn.cat.chat.data.domain.openai.model.valobj.LogicCheckTypeVO;
import cn.cat.chat.data.domain.openai.service.rule.ILogicFilter;
import cn.cat.chat.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 敏感词过滤器
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.SENSITIVE_WORD)
public class SensitiveWordFilter implements ILogicFilter<UserAccountQuotaEntity> {
    @Resource
    private SensitiveWordBs words;

    @Value("${app.config.white-list}")
    private String whiteListStr;


    @Override
    public RuleLogicEntity<ChatProcessAggregate> filter(ChatProcessAggregate chatProcess, UserAccountQuotaEntity data) throws Exception {
        // 白名单用户不做敏感词处理
        if (chatProcess.isWhiteList(whiteListStr)) {
            return RuleLogicEntity.<ChatProcessAggregate>builder()
                    .type(LogicCheckTypeVO.SUCCESS).data(chatProcess).build();
        }

        ChatProcessAggregate newChatProcessAggregate = new ChatProcessAggregate();
        newChatProcessAggregate.setOpenid(chatProcess.getOpenid());
        newChatProcessAggregate.setModel(chatProcess.getModel());

        List<MessageEntity> newMessages = chatProcess.getMessages().stream()
                .map(message -> {
                    String content = message.getContent();
                    String replace = words.replace(content);
                    return MessageEntity.builder()
                            .role(message.getRole())
                            .content(replace)
                            .build();
                })
                .collect(Collectors.toList());

        newChatProcessAggregate.setMessages(newMessages);

        return RuleLogicEntity.<ChatProcessAggregate>builder()
                .type(LogicCheckTypeVO.SUCCESS)
                .data(newChatProcessAggregate)
                .build();

    }
}
