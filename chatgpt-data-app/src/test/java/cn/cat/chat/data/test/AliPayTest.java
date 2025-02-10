package cn.cat.chat.data.test;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class AliPayTest {

    // 「沙箱环境」应用ID - 您的APPID，收款账号既是你的APPID对应支付宝账号。获取地址；https://open.alipay.com/develop/sandbox/app
    public static String app_id = "9021000141621304";
    // 「沙箱环境」商户私钥，你的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEugIBADANBgkqhkiG9w0BAQEFAASCBKQwggSgAgEAAoIBAQCDbBbB9WQcqXGvucOaLdiqpA0qmBLINVPl9axLuNGwnnEGUGFFK7swOCxwM9rrtJ0YDPUqylLRip/ZHVI268Y5Uo8767aNCjTlqtaPNwvyEAYwIpnbGYmZAqbBvuDpAYWTf2OgitwCKzZnrTFcRQHqP3rVofAh870KchVE4C7Lna+96VPW9zJ4nnTG9thTjdVbP3u47nXp+E7M0+kRX7YfW9wkGvgnAsLcdloeiEa89ZrJeRmt7xoBlOW+/tCWvYxe6yQeMluZrIenFT/Yw9Y2QXE+ZTEnRtINYtcEu9kP7m7PEFmnQqbGYZhxuwLhT13sPfX2eATxZiqQgevHIyWrAgMBAAECggEAKNy9wrbi1sqUj5fJyrGAkaYKdfjsthVmrbSSSsT/ZdQNvnHVnKU5QYflwUlOQ0VYJvc4b4KS95YLl303Z94tNvuFj5L9oqLOya4xojvyWeRDmEnZ3rb1An9BjDImSixv9OmB95PVTlNTSi8ejtrh+oycS7pbKKNZXtsT4+N0iVhdUgg6Y5XTBKDoml2p+/FnmvFBoNRFqPzX1NhSuQFSRs5OUnPITR6iKtUl5geh/l+LTqsiCE0cqD6Y9o7kElcWdEk5+WTV4QHl+4xKZVLYfY9idHwX/unv5oJivVZ84rYDjEpBXrFkc91BfZZi4bv9+WxRi6V3uIH7o7BvUl9BOQKBgQDadA78H3JIKhTNbg1ET3vnifkZzSEtQAaaLt6yN4nY6G9Lcxwu4ibLg+uLGuUoFqjvD12OWXirManYak3nkTANdUQCcc2ijmBlrM9YPNe0NEwU433FiNj49p/U4qjXdoKr+Jw4jScZkwHMHzF9ClWV3lBvaWJKQKZWnyuJsANzJQKBgQCaAqm6+x53rLMfMlcP2ywHRCEZsbxxamF1E4wduG3prPXfdzDUFk0H/7EBDRcw0N6O+tCldLXj2Kiw0348KXTPyi+jy4o7qmAO3Qj8jobp4f/QKga7/347lCOD/VllHBq85DmwjI+r1sy92gHvXnVrXt5mYJLHURWbg1VQriBEjwKBgCc1m5bvQgDn5LH2pdTz+0ZUwmw6IFFR4516+ie8xjYzif5d29oiZ1oyBG4EVss6PfoipOUu8/SuRlD8y5zbt2dC/AEAUityKKU3PkQVL5Y9qMY7CtEQvfj5szhvsmRx9gRN7CROWY1CkJkFpElQOIpcy80jeivfrLtxeEwPVp8tAoGAVrWdHhr4YmSnTK8o+VJqjYS8Jh09fZVpXWMuBiWqLGAm5jPTF/WeVRyOaYsXPXK7e0CXbDAzoB6Bi1znu7OTgtHQ1KVqJIGeJ4Am6RrNtqx5PJgMxpfoWF7lSBDB3f1eyG6uVEovMY50I4e0gzJpZZQ6HuXMfc+XHMFqrUv7+t0CfztwRpAfav7FJJYCdiFtI87IG7gVIsHYChuwuAulP+6d7HLbEbdKrt3OESK+Wpz0aAiVF2khu5Awi8rFRiyDGFGYG/XjO7iUQVBld10MH035jY5S3NNikw9fXkUZaiVNGecFVdFsUlhjVr6zf49k53j5m+ZxVlH8Hv0EE5dVK8w=";
    // 「沙箱环境」支付宝公钥
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlQ2OZpuo8Ow/lsD1Ym+AknJFg50KvK7j3LGXJqG9bw5Bu8s/msOd2wuQOgxp+w30AF+GBQ31a4rJyphTlyjR/YuHB32RqZYuUZi2Uy8ThB4oYxxG00cjH+j22480/zLDzOsbcqMWtEi+ul9Gv3gerJPxZ3sqTCiKR4eENMwfWr7U9BEKQWZgbl1QDz2kYMYTAzWcj1pJ/IFNgPAcKmTBTnOM+6npnXwNJTtLWdd1ZphhsB16MsbPB/N00lXM8Swc3xCtc9L5DSHVsUILuwLJpkEkidCajhxoflunFu3rDCb4qt05BW0vNtNBpgg2qUcaUVBaqQ+G4GntTRyr+d8vywIDAQAB";
    // 「沙箱环境」服务器异步通知回调地址
    public static String notify_url = "https://catapp.hk.cpolar.io/api/v1/sale/pay_notify";
    // 「沙箱环境」页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "https://catweb.ap.cpolar.io";
    // 「沙箱环境」
    public static String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    // 签名方式
    public static String sign_type = "RSA2";
    // 字符编码格式
    public static String charset = "utf-8";

    private AlipayClient alipayClient;

    @BeforeEach
    public void init() {
        this.alipayClient = new DefaultAlipayClient(gatewayUrl,
                app_id,
                merchant_private_key,
                "json",
                charset,
                alipay_public_key,
                sign_type);
    }

    @Test
    public void test_aliPay_pageExecute() throws AlipayApiException {
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();  // 发送请求的 Request类
        request.setNotifyUrl(notify_url);
        request.setReturnUrl(return_url);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", "132100584278");  // 我们自己生成的订单编号
        bizContent.put("total_amount", "0.01"); // 订单的总金额
        bizContent.put("subject", "测试商品");   // 支付的名称
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());

        String form = alipayClient.pageExecute(request).getBody();
        log.info("测试结果：{}", form);

        /**
         * 会生成一个form表单；
         * <form name="punchout_form" method="post" action="https://openapi-sandbox.dl.alipaydev.com/gateway.do?charset=utf-8&method=alipay.trade.page.pay&sign=CAAYYDIbvUNRDvY%2B%2BF5vghx2dL9wovodww8CK0%2FferNP1KtyXdytBVLdZKssaFJV%2B8QksVuKlU3qneWhWUuI7atLDgzpussJlJhxTMYQ3GpAfOP4PEBYQFE%2FORemzA2XPjEn88HU7esdJdUxCs602kiFoZO8nMac9iqN6P8deoGWYO4UAwE0RCV65PKeJTcy8mzhOTgkz7V018N9yIL0%2BEBf5iQJaP9tGXM4ODWwFRxJ4l1Egx46FNfjLAMzysy7D14LvTwBi5uDXV4Y%2Bp4VCnkxh3Jhkp%2BDP9SXx6Ay7QaoerxHA09kwYyLQrZ%2FdMZgoQ%2BxSEOgklIZtYj%2FLbfx1A%3D%3D&return_url=https%3A%2F%2Fgaga.plus&notify_url=http%3A%2F%2Fngrok.sscai.club%2Falipay%2FaliPayNotify_url&version=1.0&app_id=9021000132689924&sign_type=RSA2&timestamp=2023-12-13+11%3A36%3A29&alipay_sdk=alipay-sdk-java-4.38.157.ALL&format=json">
         * <input type="hidden" name="biz_content" value="{&quot;out_trade_no&quot;:&quot;100001001&quot;,&quot;total_amount&quot;:&quot;1.00&quot;,&quot;subject&quot;:&quot;测试&quot;,&quot;product_code&quot;:&quot;FAST_INSTANT_TRADE_PAY&quot;}">
         * <input type="submit" value="立即支付" style="display:none" >
         * </form>
         * <script>document.forms[0].submit();</script>
         */
    }

}
