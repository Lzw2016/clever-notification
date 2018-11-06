package org.clever.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.clever.common.exception.BusinessException;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.mapper.ReceiverBlackListMapper;
import org.clever.notification.model.BaseMessage;
import org.clever.notification.model.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-06 18:16 <br/>
 */
@Service
@Slf4j
public class ReceiverBlackListService {

    @Autowired
    private ReceiverBlackListMapper receiverBlackListMapper;

    @Transactional
    public synchronized void load() {
        // TODO 加载黑名单到 Redis
    }

    /**
     * 帐号是否在黑名单列表中
     *
     * @param messageType 消息类型
     * @param account     帐号
     * @return true:在黑名单中；false:不在黑名单中
     */
    public boolean inBlackList(Integer messageType, String account) {
        // TODO 帐号是否在黑名单列表中
        return false;
    }

    /**
     * 删除黑名单里的接受者
     *
     * @param baseMessage 消息
     * @return 除去黑名单接受者的消息
     */
    public BaseMessage removeBlackList(BaseMessage baseMessage) {

        return baseMessage;
    }

    /**
     * 删除黑名单里的接受者
     *
     * @param emailMessage email消息
     * @return 除去黑名单接受者的email消息
     */
    protected EmailMessage removeBlackList(EmailMessage emailMessage) {
        Set<String> accountSet = new HashSet<>();
        if (emailMessage.getTo() != null) {
            accountSet.addAll(emailMessage.getTo());
        }
        if (emailMessage.getCc() != null) {
            accountSet.addAll(emailMessage.getCc());
        }
        if (emailMessage.getBcc() != null) {
            accountSet.addAll(emailMessage.getBcc());
        }
        Set<String> removeAccount = new HashSet<>();
        for (String account : accountSet) {
            if (inBlackList(EnumConstant.MessageType_1, account)) {
                removeAccount.add(account);
            }
        }
        if (emailMessage.getTo() != null) {
            emailMessage.getTo().removeAll(removeAccount);
        }
        if (emailMessage.getCc() != null) {
            emailMessage.getCc().removeAll(removeAccount);
        }
        if (emailMessage.getBcc() != null) {
            emailMessage.getBcc().removeAll(removeAccount);
        }
        if (emailMessage.getTo().size() <= 0) {
            throw new BusinessException("过滤黑名单之后没有消息接收者");
        }
        return emailMessage;
    }
}
