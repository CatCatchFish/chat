package cn.cat.chat.data.domain.openai.service.rule.factory;

import cn.cat.chat.data.domain.openai.annotation.LogicStrategy;
import cn.cat.chat.data.domain.openai.service.rule.ILogicFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultLogicFactory {
    public Map<String, ILogicFilter> logicFilterMap = new ConcurrentHashMap<>();

    public Map<String, ILogicFilter> openLogicFilter() {
        return logicFilterMap;
    }

    public DefaultLogicFactory(List<ILogicFilter> logicFilters) {
        logicFilters.forEach(logicFilter -> {
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logicFilter.getClass(), LogicStrategy.class);
            if (null != strategy) {
                logicFilterMap.put(strategy.logicMode().getCode(), logicFilter);
            }
        });
    }

    /**
     * 规则逻辑枚举
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public enum LogicModel {
        NULL("NULL", "放行不用过滤"),
        ACCESS_LIMIT("ACCESS_LIMIT", "访问次数过滤"),
        SENSITIVE_WORD("SENSITIVE_WORD", "敏感词过滤"),
        USER_QUOTA("USER_QUOTA", "用户额度过滤"),
        MODEL_TYPE("MODEL_TYPE", "模型可用范围过滤"),
        ACCOUNT_STATUS("ACCOUNT_STATUS", "账户状态过滤");

        private String code;
        private String info;
    }
}
