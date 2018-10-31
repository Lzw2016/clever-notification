package org.clever.notification.test;

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
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-31 16:29 <br/>
 */
@Slf4j
public class SmsTest {

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    // 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "";
    static final String accessKeySecret = "";

    public static SendSmsResponse sendSms() throws ClientException {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持 region 化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers("17607128210");
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("Periscope");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_149418310");
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"name\":\"Tom\", \"code\":\"1478\"}");

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");


        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        acsClient.shutdown();
        return sendSmsResponse;
    }

    @Test
    public void t01() throws ClientException {
        //发短信
        SendSmsResponse response = sendSms();
        System.out.println("短信接口返回的数据----------------");
        System.out.println("Code=" + response.getCode());
        System.out.println("Message=" + response.getMessage());
        System.out.println("RequestId=" + response.getRequestId());
        System.out.println("BizId=" + response.getBizId());

//        短信接口返回的数据----------------
//        Code=OK
//        Message=OK
//        RequestId=DCDF6FAE-4F10-4EF4-94AB-3996BE6A6E9B
//        BizId=522309440992405683^0
    }


    public static QuerySendDetailsResponse querySendDetails() throws ClientException {

        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //必填-号码
        request.setPhoneNumber("17607128210");
        //可选-流水号
//        request.setBizId(bizId);
        //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        //必填-页大小
        request.setPageSize(10L);
        //必填-当前页码从1开始计数
        request.setCurrentPage(1L);

        //hint 此处可能会抛出异常，注意catch
        QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);
        acsClient.shutdown();
        return querySendDetailsResponse;
    }

    @Test
    public void t02() throws ClientException {
        QuerySendDetailsResponse querySendDetailsResponse = querySendDetails();
        System.out.println("短信明细查询接口返回数据----------------");
        System.out.println("Code=" + querySendDetailsResponse.getCode());
        System.out.println("Message=" + querySendDetailsResponse.getMessage());
        int i = 0;
        for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs()) {
            System.out.println("SmsSendDetailDTO[" + i + "]:");
            System.out.println("Content=" + smsSendDetailDTO.getContent());
            System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
            System.out.println("OutId=" + smsSendDetailDTO.getOutId());
            System.out.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
            System.out.println("ReceiveDate=" + smsSendDetailDTO.getReceiveDate());
            System.out.println("SendDate=" + smsSendDetailDTO.getSendDate());
            System.out.println("SendStatus=" + smsSendDetailDTO.getSendStatus());
            System.out.println("Template=" + smsSendDetailDTO.getTemplateCode());
        }
        System.out.println("TotalCount=" + querySendDetailsResponse.getTotalCount());
        System.out.println("RequestId=" + querySendDetailsResponse.getRequestId());

//        短信明细查询接口返回数据----------------
//        Code=OK
//        Message=OK
//        SmsSendDetailDTO[0]:
//        Content=【Periscope】您正在申请手机注册，验证码为：123，5分钟内有效！
//        ErrCode=DELIVRD
//        OutId=yourOutId
//        PhoneNum=13260658831
//        ReceiveDate=2018-10-31 21:26:48
//        SendDate=2018-10-31 21:26:45
//        SendStatus=3
//        Template=SMS_149418310
//        TotalCount=1
//        RequestId=E1937548-5EE6-402C-8E0B-C3195D56F723
    }
}
