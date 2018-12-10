package org.clever.notification.test;

import com.alicom.mns.tools.DefaultAlicomMessagePuller;
import com.aliyuncs.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.clever.notification.send.aliyun.sms.SmsReport;
import org.junit.Test;

import java.text.ParseException;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-12-10 16:24 <br/>
 */
@Slf4j
public class AliSmsReportTest {

    @Test
    public void test() throws ClientException, ParseException, InterruptedException {
        DefaultAlicomMessagePuller puller = new DefaultAlicomMessagePuller();
        //设置异步线程池大小及任务队列的大小，还有无数据线程休眠时间
        puller.setConsumeMinThreadSize(6);
        puller.setConsumeMaxThreadSize(16);
        puller.setThreadQueueSize(200);
        puller.setPullMsgThreadSize(1);
        //和服务端联调问题时开启,平时无需开启，消耗性能
        puller.openDebugLog(false);

        //TODO 此处需要替换成开发者自己的AK信息
        String accessKeyId = "LTAIZaucWkMKPwX4";
        String accessKeySecret = "kvqrgsWgwNeNQuCbd4G7swyfVo1lhP";

        /*
         * TODO 将messageType和queueName替换成您需要的消息类型名称和对应的队列名称
         *云通信产品下所有的回执消息类型:
         *1:短信回执：SmsReport，
         *2:短息上行：SmsUp
         *3:语音呼叫：VoiceReport
         *4:流量直冲：FlowReport
         */
        //此处应该替换成相应产品的消息类型
        String messageType = "SmsReport";
        //在云通信页面开通相应业务消息后，就能在页面上获得对应的queueName,格式类似Alicom-Queue-xxxxxx-SmsReport
        String queueName = "Alicom-Queue-1597890179519684-SmsReport";
        puller.startReceiveMsg(accessKeyId, accessKeySecret, messageType, queueName, new SmsReport());

        Thread.sleep(1000 * 100);
        puller.stop();
    }
}
