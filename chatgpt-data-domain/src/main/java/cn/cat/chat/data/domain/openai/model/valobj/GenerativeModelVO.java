package cn.cat.chat.data.domain.openai.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenerativeModelVO {
    TEXT("text", "文本生成模型"),
    IMAGE("image", "图像生成模型")
    ;

    private final String code;
    private final String info;
}
