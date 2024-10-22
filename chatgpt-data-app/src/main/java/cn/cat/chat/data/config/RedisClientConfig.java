package cn.cat.chat.data.config;

import cn.cat.chat.data.types.model.RedisTopic;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;
import org.redisson.config.Config;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(RedisClientConfigProperties.class)
public class RedisClientConfig {
    @Bean(name = "redissonClient", destroyMethod = "shutdown")
    public RedissonClient redissonClient(ConfigurableApplicationContext applicationContext, RedisClientConfigProperties properties) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + properties.getHost() + ":" + properties.getPort())
//                .setPassword(properties.getPassword())
                .setConnectionPoolSize(properties.getPoolSize())
                .setConnectionMinimumIdleSize(properties.getMinIdleSize())
                .setIdleConnectionTimeout(properties.getIdleTimeout())
                .setConnectTimeout(properties.getConnectTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setRetryInterval(properties.getRetryInterval())
                .setPingConnectionInterval(properties.getPingInterval())
                .setKeepAlive(properties.isKeepAlive())
        ;
        RedissonClient redissonClient = Redisson.create(config);
        // 获取监听bean
        String[] beanNamesForType = applicationContext.getBeanNamesForType(MessageListener.class);
        for (String beanName : beanNamesForType) {
            MessageListener bean = applicationContext.getBean(beanName, MessageListener.class);
            Class<? extends MessageListener> beanClass = bean.getClass();

            // 没有该监听类注解，则不注册监听器
            if (!beanClass.isAnnotationPresent(RedisTopic.class)) continue;
            RedisTopic redisTopic = beanClass.getAnnotation(RedisTopic.class);
            // 获取监听类上的topic
            RTopic topic = redissonClient.getTopic(redisTopic.topic());
            topic.addListener(String.class, bean);
            ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
            // 动态注册topic到spring容器中
            beanFactory.registerSingleton(redisTopic.topic(), topic);
        }
        return redissonClient;
    }

    static class RedisCodec extends BaseCodec {

        private final Encoder encoder = in -> {
            ByteBuf out = ByteBufAllocator.DEFAULT.buffer();
            try {
                ByteBufOutputStream os = new ByteBufOutputStream(out);
                JSON.writeJSONString(os, in, SerializerFeature.WriteClassName);
                return os.buffer();
            } catch (IOException e) {
                out.release();
                throw e;
            } catch (Exception e) {
                out.release();
                throw new IOException(e);
            }
        };

        private final Decoder<Object> decoder = (buf, state) -> JSON.parseObject(new ByteBufInputStream(buf), Object.class);

        @Override
        public Decoder<Object> getValueDecoder() {
            return decoder;
        }

        @Override
        public Encoder getValueEncoder() {
            return encoder;
        }

    }
}
