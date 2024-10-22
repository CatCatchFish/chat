package cn.cat.chat.data.domain.openai.service.channel.impl;

import cn.cat.chat.data.domain.openai.model.aggregates.ChatProcessAggregate;
import cn.cat.chat.data.domain.openai.model.valobj.GenerativeModelVO;
import cn.cat.chat.data.domain.openai.service.channel.OpenAiGroupService;
import cn.cat.chat.data.domain.openai.service.channel.model.IGenerativeModelService;
import cn.cat.chat.data.domain.openai.service.channel.model.impl.ImageGenerativeModelServiceImpl;
import cn.cat.chat.data.domain.openai.service.channel.model.impl.TextGenerativeModelServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ChatGLMService implements OpenAiGroupService {
    private final Map<GenerativeModelVO, IGenerativeModelService> generativeModelGroup = new HashMap<>();

    public ChatGLMService(ImageGenerativeModelServiceImpl imageGenerativeModelService, TextGenerativeModelServiceImpl textGenerativeModelService) {
        generativeModelGroup.put(GenerativeModelVO.IMAGE, imageGenerativeModelService);
        generativeModelGroup.put(GenerativeModelVO.TEXT, textGenerativeModelService);
    }

    @Override
    public void doMessageResponse(ChatProcessAggregate chatProcess, ResponseBodyEmitter emitter) throws Exception {
        GenerativeModelVO generativeModelVO = chatProcess.getGenerativeModelVO();
        generativeModelGroup.get(generativeModelVO).doMessageResponse(chatProcess, emitter);

    }
}
