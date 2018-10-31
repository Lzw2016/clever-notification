package org.clever.notification.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.clever.common.exception.BusinessException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 作者： lzw<br/>
 * 创建时间：2018-10-29 17:09 <br/>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SmsMessage extends BaseMessage {

    /**
     * 接收手机号
     */
    private List<String> to;

    /**
     * 设置消息内容
     *
     * @param content 消息内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 验证消息配置是否正确
     */
    @Override
    public void valid() {
        super.valid();
        // 删除重复 删除空
        if (to != null && to.size() > 0) {
            to = to.stream().filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        }
        // 校验
        if (to == null || to.size() <= 0) {
            throw new BusinessException("接收手机号，不能为空");
        }
    }
}
