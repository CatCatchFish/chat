package cn.cat.chat.data.domain.openai.service.channel;

import cn.cat.chat.data.domain.openai.model.aggregates.ChatProcessAggregate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public interface OpenAiGroupService {
    // 解耦信息处理，根据用户选择渠道，调用相应的消息处理方法
    // 下放到具体的消息处理类中，避免耦合
    void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception;
}
