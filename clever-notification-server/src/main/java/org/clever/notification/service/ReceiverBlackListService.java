package org.clever.notification.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.clever.common.exception.BusinessException;
import org.clever.common.utils.mapper.BeanMapper;
import org.clever.notification.dto.request.ReceiverBlackListQueryReq;
import org.clever.notification.dto.request.ReceiverBlackListUpdateReq;
import org.clever.notification.entity.EnumConstant;
import org.clever.notification.entity.ReceiverBlackList;
import org.clever.notification.mapper.ReceiverBlackListMapper;
import org.clever.notification.model.BaseMessage;
import org.clever.notification.model.EmailMessage;
import org.clever.notification.rabbit.producer.IExcludeBlackList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-11-06 18:16 <br/>
 */
@SuppressWarnings("Duplicates")
@Service
@Slf4j
public class ReceiverBlackListService implements IExcludeBlackList {

    /**
     * 黑名单Redis key前缀
     */
    private static final String KeyPrefix = "clever-notification:black-list";

    /**
     * 当前所有黑名单 Key
     */
    private static final String blackListSet = "clever-notification:black-list:set";

    /**
     * 所有黑名单 临时Key
     */
    private static final String blackListSetTmp = "clever-notification:black-list:set-tmp";


    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ReceiverBlackListMapper receiverBlackListMapper;

    @SuppressWarnings({"ConstantConditions", "Duplicates"})
    @PostConstruct
    @Transactional
    public synchronized void load() {
        // 初始化 blackListSet blackListSetTmp
        if (!redisTemplate.hasKey(blackListSet)) {
            Set<String> set = redisTemplate.keys(KeyPrefix + ":*");
            if (set.size() <= 0) {
                set.add("");
            }
            redisTemplate.opsForSet().add(blackListSet, set.toArray(new String[]{}));
        }
        if (!redisTemplate.hasKey(blackListSetTmp)) {
            redisTemplate.opsForSet().add(blackListSetTmp, "");
        }
        // 查询所有黑名单
        int enabledCount = receiverBlackListMapper.updateEnabledByExpiredTime();
        List<ReceiverBlackList> receiverBlackLists = receiverBlackListMapper.findAllEnabled();
        // 删除当前不存在的数据
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            for (ReceiverBlackList receiverBlackList : receiverBlackLists) {
                String key = getConfigKey(receiverBlackList);
                connection.sAdd(blackListSetTmp.getBytes(), key.getBytes());
            }
            return null;
        });
        // 求差集
        Set<String> keySet = redisTemplate.opsForSet().difference(blackListSet, blackListSetTmp);
        redisTemplate.delete(keySet);
        // 替换 blackListSetTmp -> blackListSet
        redisTemplate.delete(blackListSet);
        redisTemplate.rename(blackListSetTmp, blackListSet);
        // 插入所有的黑名单数据
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            for (ReceiverBlackList receiverBlackList : receiverBlackLists) {
                addConfig(connection, receiverBlackList);
            }
            return null;
        });
        log.info("### 加载黑名单数量:{} | 删除的黑名单数量：{} | 禁用黑名单数量:{}", receiverBlackLists.size(), keySet.size(), enabledCount);
    }

    private String getConfigKey(ReceiverBlackList receiverBlackList) {
        // {KeyPrefix}:{sys_name}:{message_type}:{account}
        return String.format(
                "%s:%s:%s:%s",
                KeyPrefix,
                receiverBlackList.getSysName(),
                receiverBlackList.getMessageType(),
                receiverBlackList.getAccount()
        );
    }

    private void addConfig(RedisConnection connection, ReceiverBlackList receiverBlackList) {
        String key = getConfigKey(receiverBlackList);
        if (receiverBlackList.getExpiredTime() != null) {
            long timeout = (receiverBlackList.getExpiredTime().getTime() - System.currentTimeMillis()) / 1000;
            if (timeout > 0) {
                // 设置数据过期时间
                connection.setEx(key.getBytes(), timeout, receiverBlackList.getAccount().getBytes());
            }
        } else {
            // 不设置数据过期时间
            connection.set(key.getBytes(), receiverBlackList.getAccount().getBytes());
        }
    }

    /**
     * 帐号是否在黑名单列表中
     */
    @Override
    public boolean inBlackList(String sysName, Integer messageType, String account) {
        // {KeyPrefix}:{sys_name}:{message_type}:{account}
        final String key = String.format("%s:%s:%s:%s", KeyPrefix, sysName, messageType, account);
        final String globalKey = String.format("%s:%s:%s:%s", KeyPrefix, EnumConstant.RootSysName, messageType, account);
        List<String> values = redisTemplate.opsForValue().multiGet(new ArrayList<String>() {{
            add(key);
            add(globalKey);
        }});
        assert values != null;
        String valueAccount = values.stream().filter(Objects::nonNull).findFirst().orElse(null);
        return Objects.equals(account, valueAccount);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends BaseMessage> T removeBlackList(T message) {
        if (message instanceof EmailMessage) {
            EmailMessage emailMessage = removeBlackList((EmailMessage) message);
            return (T) emailMessage;
        } else {
            throw new BusinessException("不支持的消息类型: " + message.getClass().getName());
        }
    }

    private EmailMessage removeBlackList(EmailMessage emailMessage) {
        Set<String> accountSet = new HashSet<>(emailMessage.getTo());
        if (emailMessage.getCc() != null) {
            accountSet.addAll(emailMessage.getCc());
        }
        if (emailMessage.getBcc() != null) {
            accountSet.addAll(emailMessage.getBcc());
        }
        Set<String> removeAccount = new HashSet<>();
        for (String account : accountSet) {
            if (inBlackList(emailMessage.getSysName(), EnumConstant.MessageType_1, account)) {
                removeAccount.add(account);
            }
        }
        emailMessage.getTo().removeAll(removeAccount);
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

    public IPage<ReceiverBlackList> findByPage(ReceiverBlackListQueryReq queryReq) {
        Page<ReceiverBlackList> page = new Page<>(queryReq.getPageNo(), queryReq.getPageSize());
        page.setRecords(receiverBlackListMapper.findByPage(queryReq, page));
        return page;
    }

    @Transactional
    public ReceiverBlackList addReceiverBlackList(ReceiverBlackList receiverBlackList) {
        // 校验黑名单是否存在
        int count = receiverBlackListMapper.exists(receiverBlackList.getSysName(), receiverBlackList.getMessageType(), receiverBlackList.getAccount());
        if (count > 0) {
            throw new BusinessException("黑名单配置已经存在");
        }
        // 保存配置
        receiverBlackListMapper.insert(receiverBlackList);
        receiverBlackList = receiverBlackListMapper.selectById(receiverBlackList.getId());
        // 加载配置
        final ReceiverBlackList tmp = receiverBlackList;
        redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            addConfig(connection, tmp);
            return null;
        });
        return receiverBlackList;
    }

    @Transactional
    public ReceiverBlackList updateReceiverBlackList(Long id, ReceiverBlackListUpdateReq updateReq) {
        ReceiverBlackList oldReceiverBlackList = receiverBlackListMapper.selectById(id);
        if (oldReceiverBlackList == null) {
            throw new BusinessException("更新数据不存在");
        }
        // 更新配置
        ReceiverBlackList newReceiverBlackList = BeanMapper.mapper(updateReq, ReceiverBlackList.class);
        newReceiverBlackList.setId(oldReceiverBlackList.getId());
        receiverBlackListMapper.updateById(newReceiverBlackList);
        newReceiverBlackList = receiverBlackListMapper.selectById(newReceiverBlackList.getId());
        // 加载配置
        String oldKey = getConfigKey(oldReceiverBlackList);
        String newKey = getConfigKey(newReceiverBlackList);
        final ReceiverBlackList tmp = newReceiverBlackList;
        if (Objects.equals(oldKey, newKey)) {
            redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
                addConfig(connection, tmp);
                return null;
            });
        } else {
            redisTemplate.executePipelined((RedisCallback<Void>) connection -> {
                addConfig(connection, tmp);
                connection.del(oldKey.getBytes());
                return null;
            });
        }
        return newReceiverBlackList;
    }

    @Transactional
    public ReceiverBlackList delReceiverBlackList(Long id) {
        ReceiverBlackList receiverBlackList = receiverBlackListMapper.selectById(id);
        if (receiverBlackList == null) {
            throw new BusinessException("删除数据不存在");
        }
        // 删除配置
        receiverBlackListMapper.deleteById(receiverBlackList.getId());
        // 卸载配置
        String key = getConfigKey(receiverBlackList);
        redisTemplate.delete(key);
        return receiverBlackList;
    }
}
