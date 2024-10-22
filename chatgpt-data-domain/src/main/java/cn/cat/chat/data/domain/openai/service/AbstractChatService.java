package cn.cat.chat.data.domain.openai.service;

import cn.cat.chat.data.domain.openai.model.aggregates.ChatProcessAggregate;
import cn.cat.chat.data.domain.openai.model.entity.RuleLogicEntity;
import cn.cat.chat.data.domain.openai.model.entity.UserAccountQuotaEntity;
import cn.cat.chat.data.domain.openai.model.valobj.LogicCheckTypeVO;
import cn.cat.chat.data.domain.openai.repository.IOpenAiRepository;
import cn.cat.chat.data.domain.openai.service.channel.OpenAiGroupService;
import cn.cat.chat.data.domain.openai.service.channel.impl.ChatGLMService;
import cn.cat.chat.data.domain.openai.service.channel.impl.ChatGPTService;
import cn.cat.chat.data.domain.openai.service.rule.factory.DefaultLogicFactory;
import cn.cat.chat.data.types.common.Constants;
import cn.cat.chat.data.types.enums.OpenAiChannel;
import cn.cat.chat.data.types.exception.ChatGPTException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class AbstractChatService implements IChatService {
    private final Map<OpenAiChannel, OpenAiGroupService> openAiGroup = new HashMap<>();

    public AbstractChatService(ChatGPTService chatGPTService, ChatGLMService chatGLMService) {
        openAiGroup.put(OpenAiChannel.ChatGPT, chatGPTService);
        openAiGroup.put(OpenAiChannel.ChatGLM, chatGLMService);
    }

    @Resource
    private IOpenAiRepository openAiRepository;

    @Override
    public ResponseBodyEmitter completions(ResponseBodyEmitter emitter, ChatProcessAggregate chatProcess) {
        // 1. 请求应答
        emitter.onCompletion(() -> {
            log.info("流式问答请求完成，使用模型：{}", chatProcess.getModel());
        });
        emitter.onError(throwable -> log.error("流式问答请求疫情，使用模型：{}", chatProcess.getModel(), throwable));

        UserAccountQuotaEntity userAccountQuotaEntity = openAiRepository.queryUserAccount(chatProcess.getOpenid());

        try {
            // 2. 规则过滤
            RuleLogicEntity<ChatProcessAggregate> ruleLogicEntity = this.doCheckLogic(chatProcess, userAccountQuotaEntity,
                    DefaultLogicFactory.LogicModel.ACCESS_LIMIT.getCode(),
                    DefaultLogicFactory.LogicModel.SENSITIVE_WORD.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.ACCOUNT_STATUS.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.MODEL_TYPE.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode(),
                    null != userAccountQuotaEntity ? DefaultLogicFactory.LogicModel.USER_QUOTA.getCode() : DefaultLogicFactory.LogicModel.NULL.getCode());
            if (!LogicCheckTypeVO.SUCCESS.equals(ruleLogicEntity.getType())) {
                emitter.send(ruleLogicEntity.getInfo());
                emitter.complete();
                return emitter;
            }

            // 3. 调用模型
            openAiGroup.get(chatProcess.getChannel()).doMessageResponse(ruleLogicEntity.getData(), emitter);
        } catch (Exception e) {
            throw new ChatGPTException(Constants.ResponseCode.UN_ERROR.getCode(), Constants.ResponseCode.UN_ERROR.getInfo());
        }

        // 3. 返回结果
        return emitter;
    }

    protected abstract RuleLogicEntity<ChatProcessAggregate> doCheckLogic(ChatProcessAggregate chatProcess, UserAccountQuotaEntity data, String... logics) throws Exception;
}
