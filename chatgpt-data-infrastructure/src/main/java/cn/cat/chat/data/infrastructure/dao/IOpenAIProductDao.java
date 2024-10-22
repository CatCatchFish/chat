package cn.cat.chat.data.infrastructure.dao;

import cn.cat.chat.data.infrastructure.po.OpenAIProduct;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IOpenAIProductDao {
    OpenAIProduct queryProductByProductId(Integer productId);

    List<OpenAIProduct> queryProductList();
}
