package cn.cat.chat.data.trigger.http;

import cn.cat.chat.data.domain.auth.service.IAuthService;
import cn.cat.chat.data.domain.openai.model.aggregates.ChatProcessAggregate;
import cn.cat.chat.data.domain.openai.model.entity.MessageEntity;
import cn.cat.chat.data.domain.openai.service.IChatService;
import cn.cat.chat.data.trigger.http.dto.ChatGLMRequestDTO;
import cn.cat.chat.data.types.common.Constants;
import cn.cat.chat.data.types.exception.ChatGPTException;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@EnableAsync
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/${app.config.api-version}/")
public class ChatGLMAIServiceController {
    @Resource
    private IChatService chatService;
    @Resource
    private IAuthService authService;

    @RequestMapping("chat/completions")
    public ResponseBodyEmitter completionsStream(@RequestBody ChatGLMRequestDTO request, @RequestHeader("Authorization") String token, HttpServletResponse response) {
        log.info("流式问答请求开始，使用模型：{} 请求信息：{}", request.getModel(), JSON.toJSONString(request.getMessages()));
        try {
            // 1. 基础配置；流式输出、编码、禁用缓存
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");

            // 2. 构建异步响应对象【对 Token 过期拦截】
            ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
            boolean success = authService.checkToken(token);
            if (!success) {
                try {
                    emitter.send(Constants.ResponseCode.TOKEN_ERROR.getCode());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                emitter.complete();
                return emitter;
            }

            // 3. 获取 OpenID
            String openid = authService.openid(token);
            log.info("流式问答请求处理，openid:{} 请求模型:{}", openid, request.getModel());

            // 3. 构建参数
            ChatProcessAggregate chatProcessAggregate = ChatProcessAggregate.builder()
                    .openid(openid)
                    .model(request.getModel())
                    .messages(request.getMessages().stream()
                            .map(entity -> MessageEntity.builder()
                                    .role(entity.getRole())
                                    .content(entity.getContent())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();

            // 3. 请求结果&返回
            return chatService.completions(emitter, chatProcessAggregate);
        } catch (Exception e) {
            log.error("流式应答，请求模型：{} 发生异常", request.getModel(), e);
            throw new ChatGPTException(e.getMessage());
        }
    }

    @RequestMapping("/stream/test")
    public ResponseBodyEmitter completionsStream(HttpServletResponse response) {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");

        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        executor.execute(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    emitter.send("data:" + i + "\n\n");
                    Thread.sleep(1000);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}
