package org.clever.notification.send.aliyun.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.common.utils.mapper.JacksonMapper;

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

    private final IClientProfile clientProfile;
    private final IAcsClient acsClient;

    /**
     * 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
     */
    private final String accessKeyId = "LTAIZaucWkMKPwX4";
    /**
     * 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
     */
    private final String accessKeySecret = "kvqrgsWgwNeNQuCbd4G7swyfVo1lhP";

    static {
        DefaultProfile.addEndpoint("cn-hangzhou", PRODUCT, DOMAIN);
    }

    private SmsApiUtils() {
        //初始化acsClient,暂不支持 region 化
        clientProfile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        acsClient = new DefaultAcsClient(clientProfile);
    }

    /**
     * 发送短信
     *
     * @param telephone    待发送手机号
     * @param signName     短信签名
     * @param templateCode 短信模板
     * @param outId        outId为提供给业务方扩展字段
     * @param params       模板中的变量替换JSON串
     */
    public static SendSmsResponse sendSms(String telephone, String signName, String templateCode, String outId, Map<String, Object> params) {
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(telephone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(JacksonMapper.nonEmptyMapper().toJson(params));
        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId(outId);
        try {
            return SMS_API_UTILS.acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }
}
