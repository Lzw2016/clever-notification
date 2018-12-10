package org.clever.notification.send.aliyun.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-12-10 17:28 <br/>
 */
public class SmsApiUtils {
    /**
     * 产品名称:云通信短信API产品,开发者无需替换
     */
    private static final String PRODUCT = "Dysmsapi";
    /**
     * 产品域名,开发者无需替换
     */
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";

    private static final SmsApiUtils SMS_API_UTILS = new SmsApiUtils();

    /**
     * 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
     */
    private final String accessKeyId = "LTAIZaucWkMKPwX4";
    /**
     * 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
     */
    private final String accessKeySecret = "kvqrgsWgwNeNQuCbd4G7swyfVo1lhP";

    private SmsApiUtils() {
        //初始化acsClient,暂不支持 region 化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", PRODUCT, DOMAIN);
        IAcsClient acsClient = new DefaultAcsClient(profile);
    }

    /**
     * 发送短信
     */
    public static SendSmsResponse sendSms(Map<String, Object> params) {
        return null;
    }
}
