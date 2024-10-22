package cn.cat.chat.data.domain.openai.service.channel.model.impl;

import cn.bugstack.chatglm.model.ChatCompletionRequest;
import cn.bugstack.chatglm.model.ChatCompletionResponse;
import cn.bugstack.chatglm.model.Model;
import cn.bugstack.chatglm.model.Role;
import cn.bugstack.chatglm.session.OpenAiSession;
import cn.cat.chat.data.domain.openai.model.aggregates.ChatProcessAggregate;
import cn.cat.chat.data.domain.openai.service.channel.model.IGenerativeModelService;
import cn.cat.chat.data.types.exception.ChatGPTException;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TextGenerativeModelServiceImpl implements IGenerativeModelService {
    @Resource
    protected OpenAiSession chatGlMOpenAiSession;


    @Override
    public void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception {
        log.info("使用 ChatGLM 文字生成模型 进行消息回复");
        // 1. 请求消息
        List<ChatCompletionRequest.Prompt> messages = chatProcess.getMessages().stream()
                .map(entity -> ChatCompletionRequest.Prompt.builder()
                        .role(entity.getRole())
                        .content(entity.getContent())
                        .build())
                .collect(Collectors.toList());

        // 2. 封装参数
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .stream(true)
                .messages(messages)
                .isCompatible(false)
                .model(Model.getByCode(chatProcess.getModel()))
                .build();

        chatGlMOpenAiSession.completions(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equals(data)) {
                    log.info("对话完成");
                    return;
                }
                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                List<ChatCompletionResponse.Choice> choices = response.getChoices();
                for (ChatCompletionResponse.Choice choice : choices) {
                    ChatCompletionResponse.Delta delta = choice.getDelta();
                    // 应答完成
                    String finishReason = choice.getFinishReason();
                    if (StringUtils.isNoneBlank(finishReason) && "stop".equals(finishReason)) {
                        log.info("会话结束：{}", finishReason);
                        break;
                    }

                    try {
                        log.info("发送消息：{}", delta.getContent());
                        emitter.send(delta.getContent());
                    } catch (Exception e) {
                        throw new ChatGPTException(e.getMessage());
                    }
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("关闭事件源");
                emitter.complete();
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.error("对话失败", t);
            }
        });
    }
}
