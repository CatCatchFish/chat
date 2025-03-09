package cn.cat.chat.data.domain.openai.model.aggregates;

import cn.bugstack.chatglm.model.Model;
import cn.cat.chat.data.domain.openai.model.entity.MessageEntity;
import cn.cat.chat.data.domain.openai.model.valobj.GenerativeModelVO;
import cn.cat.chat.data.types.common.Constants;
import cn.cat.chat.data.types.enums.OpenAiChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatProcessAggregate {
    /**
     * 验证信息
     */
    private String openid;
    /**
     * 默认模型
     */
    private String model = Model.GLM_4V_Flash.getCode();
    /**
     * 问题描述
     */
    private List<MessageEntity> messages;

    public boolean isWhiteList(String whiteListStr) {
        String[] whiteList = whiteListStr.split(Constants.SPLIT);
        for (String whiteOpenid : whiteList) {
            if (whiteOpenid.equals(openid)) return true;
        }
        return false;
    }

    public OpenAiChannel getChannel() {
        return OpenAiChannel.getChannel(this.model);
    }

    public GenerativeModelVO getGenerativeModelVO() {
        return switch (this.model) {
            case "cogview-3-flash", "cogview-3" -> GenerativeModelVO.IMAGE;
            default -> GenerativeModelVO.TEXT;
        };
    }

}
