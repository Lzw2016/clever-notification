package org.clever.notification.send.aliyun.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.DateTimeUtils;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.common.utils.mapper.JacksonMapper;

import java.io.Closeable;
import java.util.Date;
import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-12-10 17:28 <br/>
 */
public class SmsApiUtils implements Closeable {
    /**
     * 地域Region
     */
    private static final String REGION_ID = "cn-hangzhou";
    /**
     * 产品名称:云通信短信API产品,开发者无需替换
     */
    private static final String PRODUCT = "Dysmsapi";
    /**
     * 产品域名,开发者无需替换
     */
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";

    static {
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        DefaultProfile.addEndpoint(REGION_ID, PRODUCT, DOMAIN);
    }

    private final IAcsClient acsClient;

    /**
     * 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
     *
     * @param accessKeyId     AccessKeyId
     * @param accessKeySecret AccessKeySecret
     */
    public SmsApiUtils(String accessKeyId, String accessKeySecret) {
        IClientProfile clientProfile = DefaultProfile.getProfile(REGION_ID, accessKeyId, accessKeySecret);
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
    public SendSmsResponse sendSms(String telephone, String signName, String templateCode, String outId, Map<String, Object> params) {
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(telephone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        if (params != null && params.size() > 0) {
            request.setTemplateParam(JacksonMapper.nonEmptyMapper().toJson(params));
        }
        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        if (StringUtils.isNotBlank(outId)) {
            request.setOutId(outId);
        }
        try {
            return acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    /**
     * 查询短信发送状态
     *
     * @param telephone   手机号(必填)
     * @param bizId       发送流水号
     * @param date        发送日期(必填)
     * @param currentPage 当前页数(从1开始)
     * @param pageSize    每页数据量
     */
    public QuerySendDetailsResponse querySendDetails(String telephone, String bizId, Date date, long currentPage, long pageSize) {
        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //必填-号码
        request.setPhoneNumber(telephone);
        //可选-流水号
        if (StringUtils.isNotBlank(bizId)) {
            request.setBizId(bizId);
        }
        //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        request.setSendDate(DateTimeUtils.formatToString(date, "yyyyMMdd"));
        //必填-当前页码从1开始计数
        request.setCurrentPage(currentPage);
        //必填-页大小
        request.setPageSize(pageSize);
        try {
            return acsClient.getAcsResponse(request);
        } catch (ClientException e) {
            throw ExceptionUtils.unchecked(e);
        }
    }

    @Override
    public void close() {
        if (acsClient != null) {
            acsClient.shutdown();
        }
    }
}
