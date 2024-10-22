package cn.cat.chat.data.config;

import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.utils.InnerWordCharUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SensitiveWordConfig {
    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                .wordReplace(((stringBuilder, chars, iWordResult, iWordContext) -> {
                    String sensitiveWord = InnerWordCharUtils.getString(chars, iWordResult);
                    stringBuilder.replace(iWordResult.startIndex(), iWordResult.endIndex(), "*".repeat(sensitiveWord.length()));
                    log.info("检测到敏感词：{}", sensitiveWord);
                }))
                .ignoreCase(true)
                .ignoreWidth(true)
                .ignoreNumStyle(true)
                .ignoreChineseStyle(true)
                .ignoreEnglishStyle(true)
                .ignoreRepeat(false)
                .enableNumCheck(true)
                .enableEmailCheck(true)
                .enableUrlCheck(true)
                .enableWordCheck(true)
                .numCheckLen(1024)
                .init();
    }
}
