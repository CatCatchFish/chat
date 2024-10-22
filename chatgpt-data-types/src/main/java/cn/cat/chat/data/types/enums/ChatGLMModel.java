package cn.cat.chat.data.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatGLMModel {
    GLM_3_5_TURBO("glm-3-turbo"),
    GLM_4("glm-4"),
    GLM_4V("glm-4v"),
    COGVIEW_3("cogview-3");
    private final String code;

    public static String getModelTypes() {
        return GLM_3_5_TURBO.getCode() + "," + GLM_4.getCode() + "," + GLM_4V.getCode() + "," + COGVIEW_3.getCode();
    }
}
