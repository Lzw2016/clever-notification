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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.DateTimeUtils;
import org.clever.common.utils.exception.ExceptionUtils;
import org.clever.common.utils.mapper.JacksonMapper;

import java.io.Closeable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-12-10 17:28 <br/>
 */
@Slf4j
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

    /**
     * 错误信息
     */
    private static final Map<String, String> ErrorMap = new HashMap<String, String>() {{
        put("isp.RAM_PERMISSION_DENY", "RAM权限DENY");
        put("isv.OUT_OF_SERVICE", "业务停机");
        put("isv.PRODUCT_UN_SUBSCRIPT", "未开通云通信产品的阿里云客户");
        put("isv.PRODUCT_UNSUBSCRIBE", "产品未开通");
        put("isv.ACCOUNT_NOT_EXISTS", "账户不存在");
        put("isv.ACCOUNT_ABNORMAL", "账户异常");
        put("isv.SMS_TEMPLATE_ILLEGAL", "短信模版不合法");
        put("isv.SMS_SIGNATURE_ILLEGAL", "短信签名不合法");
        put("isv.INVALID_PARAMETERS", "参数异常");
        put("isp.SYSTEM_ERROR", "请重试接口调用，如仍存在此情况请创建工单反馈工程师查看");
        put("isv.MOBILE_NUMBER_ILLEGAL", "非法手机号");
        put("isv.MOBILE_COUNT_OVER_LIMIT", "手机号码数量超过限制");
        put("isv.TEMPLATE_MISSING_PARAMETERS", "模版缺少变量");
        put("isv.BUSINESS_LIMIT_CONTROL", "业务限流");
        put("isv.INVALID_JSON_PARAM", "JSON参数不合法");
        put("isv.BLACK_KEY_CONTROL_LIMIT", "黑名单管控");
        put("isv.PARAM_LENGTH_LIMIT", "参数超出长度限制");
        put("isv.PARAM_NOT_SUPPORT_URL", "不支持URL");
        put("isv.AMOUNT_NOT_ENOUGH", "账户余额不足");
        put("isv.TEMPLATE_PARAMS_ILLEGAL", "模版变量里包含非法关键字");
        put("SignatureDoesNotMatch", "Signature加密错误");
        put("InvalidTimeStamp.Expired", "时间戳错误");
        put("SignatureNonceUsed", "唯一随机数重复");
        put("InvalidVersion", "版本号错误");
        put("InvalidAction.NotFound", "接口名错误");
    }};


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
        // 发送短信
        try {
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            String error = ErrorMap.get(sendSmsResponse.getCode());
            if (error != null) {
                String res = "RequestId=" + sendSmsResponse.getRequestId() + " | "
                        + "BizId=" + sendSmsResponse.getBizId() + " | "
                        + "Code=" + sendSmsResponse.getCode() + " | "
                        + "Message=" + sendSmsResponse.getMessage() + " | ";
                log.error("### {}", res);
                throw new BusinessException("短信发送失败: " + error);
            }
            return sendSmsResponse;
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
