package org.clever.notification.utils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.utils.HttpUtils;
import org.clever.common.utils.mapper.JacksonMapper;
import org.clever.notification.dto.PatternConstant;
import org.clever.notification.model.BaseMessage;
import org.springframework.amqp.core.Message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 发送异步通知
 * 作者： lzw<br/>
 * 创建时间：2018-11-08 14:49 <br/>
 */
@Slf4j
public class AsyncNotice {

    /**
     * 参数正确返回true
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean checkUrl(BaseMessage baseMessage) {
        return baseMessage != null
                && StringUtils.isNotBlank(baseMessage.getAsyncCallBack())
                && baseMessage.getAsyncCallBack().matches(PatternConstant.Url_Pattern);
    }

    /**
     * 参数正确返回true
     */
    private static boolean checkUrl(String url) {
        return StringUtils.isNotBlank(url)
                && url.matches(PatternConstant.Url_Pattern);
    }

    /**
     * 成功通知
     */
    public static void successNotice(BaseMessage baseMessage) {
        if (!checkUrl(baseMessage)) {
            return;
        }
        log.info("### 发送成功异步通知 {}", baseMessage);
        HttpUtils.getInner().postStr(baseMessage.getAsyncCallBack(), JacksonMapper.nonEmptyMapper().toJson(new AsyncNoticeReq(true, baseMessage)));
    }

    /**
     * 失败通知
     */
    public static void failNotice(BaseMessage baseMessage) {
        if (!checkUrl(baseMessage)) {
            return;
        }
        log.info("### 发送失败异步通知 {}", baseMessage);
        HttpUtils.getInner().postStr(baseMessage.getAsyncCallBack(), JacksonMapper.nonEmptyMapper().toJson(new AsyncNoticeReq(true, baseMessage)));
    }

    /**
     * 失败通知
     */
    public static void failNotice(String url, Long sendId) {
        if (!checkUrl(url)) {
            return;
        }
        log.info("### 发送失败异步通知 {}", sendId);
        HttpUtils.getInner().postStr(url, JacksonMapper.nonEmptyMapper().toJson(new AsyncNoticeReq(true, sendId)));
    }

    public static void failNotice(Message message) {
        String str = new String(message.getBody());
        if (StringUtils.isBlank(str)) {
            return;
        }
        BaseMessage baseMessage = JacksonMapper.nonEmptyMapper().fromJson(str, BaseMessage.class);
        if (!checkUrl(baseMessage)) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("sendId", baseMessage.getSendId());
        map.put("message", str);
        log.info("### 发送失败异步通知 {}", baseMessage.getSendId());
        HttpUtils.getInner().postStr(baseMessage.getAsyncCallBack(), JacksonMapper.nonEmptyMapper().toJson(map));
    }

    @Data
    @NoArgsConstructor
    static class AsyncNoticeReq implements Serializable {

        private boolean success;

        /**
         * 消息发送ID (全局唯一)
         */
        private Long sendId;

        private BaseMessage message;

        AsyncNoticeReq(boolean success, BaseMessage message) {
            this.success = success;
            this.message = message;
            this.sendId = message.getSendId();
        }

        AsyncNoticeReq(boolean success, Long sendId) {
            this.success = success;
            this.sendId = sendId;
        }
    }
}
