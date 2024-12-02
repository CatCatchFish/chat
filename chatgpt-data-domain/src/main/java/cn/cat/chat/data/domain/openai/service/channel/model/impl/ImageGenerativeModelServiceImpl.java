package cn.cat.chat.data.domain.openai.service.channel.model.impl;

import cn.bugstack.chatglm.model.ImageCompletionRequest;
import cn.bugstack.chatglm.model.ImageCompletionResponse;
import cn.bugstack.chatglm.model.Model;
import cn.bugstack.chatglm.session.OpenAiSession;
import cn.bugstack.chatgpt.common.Constants;
import cn.cat.chat.data.domain.openai.model.aggregates.ChatProcessAggregate;
import cn.cat.chat.data.domain.openai.model.entity.MessageEntity;
import cn.cat.chat.data.domain.openai.service.channel.model.IGenerativeModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.List;


@Slf4j
@Service
public class ImageGenerativeModelServiceImpl implements IGenerativeModelService {
    @Resource
    protected OpenAiSession chatGlMOpenAiSession;

    @Override
    public void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception {
        log.info("使用 ChatGLM 图片生成模型 进行消息回复");

        // 封装请求信息
        StringBuilder prompt = new StringBuilder();
        List<MessageEntity> messages = chatProcess.getMessages();
        for (MessageEntity message : messages) {
            String role = message.getRole();
            if (Constants.Role.USER.getCode().equals(role)) {
                prompt.append(message.getContent());
                prompt.append("\r\n");
            }
        }

        ImageCompletionRequest request = ImageCompletionRequest.builder()
                .prompt(prompt.toString())
                .model(Model.getByCode(chatProcess.getModel()))
                .build();

        emitter.send("您的😊图片正在生成中，请耐心等待... \r\n");

        try {
            ImageCompletionResponse response = chatGlMOpenAiSession.genImages(request);
            List<ImageCompletionResponse.Image> items = response.getData();
            for (ImageCompletionResponse.Image item : items) {
                String url = item.getUrl();
                log.info("url:{}", url);
                emitter.send("![](" + url + ")");
            }

            log.info("图片生成模型 回复完成");
            emitter.complete();
        } catch (IOException e) {
            try {
                emitter.send("您的😭图片生成失败了，请调整说明... \r\n");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
