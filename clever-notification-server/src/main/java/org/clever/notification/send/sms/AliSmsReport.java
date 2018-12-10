package org.clever.notification.send.sms;

import com.alicom.mns.tools.MessageListener;
import com.aliyun.mns.model.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云短信-通过订阅SmsReport短信状态报告，可以获知每条短信的发送情况，了解短信是否达到终端用户的状态与相关信息
 * <p>
 * 作者： lzw<br/>
 * 创建时间：2018-12-10 15:49 <br/>
 */
@Slf4j
public class AliSmsReport implements MessageListener {

    @Override
    public boolean dealMessage(Message message) {

        System.out.println("message handle: " + message.getReceiptHandle());
        System.out.println("message body: " + message.getMessageBodyAsString());
        System.out.println("message id: " + message.getMessageId());
        System.out.println("message dequeue count:" + message.getDequeueCount());
        System.out.println("Thread:" + Thread.currentThread().getName());
        try {
            log.info("###返回数据 {}", message.getMessageBodyAsString());
//            {
//                "send_time": "2018-12-10 17:15:25",
//                "report_time": "2018-12-10 17:15:29",
//                "success": true,
//                "err_msg": "用户接收成功",
//                "err_code": "DELIVERED",
//                "phone_number": "13260658831",
//                "sms_size": "1",
//                "biz_id": "879400644433325172^0",
//                "out_id": "yourOutId"
//            }


        } catch (Throwable e) {
            //您自己的代码部分导致的异常，应该return false,这样消息不会被delete掉，而会根据策略进行重推
            return false;
        }
        //消息处理成功，返回true, SDK将调用MNS的delete方法将消息从队列中删除掉
        return true;
    }
}
