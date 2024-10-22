package cn.cat.chat.data.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatGPTModel {
    /** gpt-3.5-turbo */
    GPT_3_5_TURBO("gpt-3.5-turbo"),
    GPT_4O_MINI("gpt-4o-mini"),
    ;
    private final String code;
}
