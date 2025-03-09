package cn.cat.chat.data.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatGLMModel {
    GLM_4V_FLASH("glm-4v-flash"),
    GLM_4("glm-4"),
    GLM_4V("glm-4v"),
    COGVIEW_3_FLASH("cogview-3-flash");
    private final String code;

    public static String getModelTypes() {
        return GLM_4V_FLASH.getCode() + "," + COGVIEW_3_FLASH.getCode();
    }

}
