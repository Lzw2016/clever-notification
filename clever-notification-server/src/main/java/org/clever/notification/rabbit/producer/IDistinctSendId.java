package org.clever.notification.rabbit.producer;

/**
 * 去重 Message SendId
 * 作者： lzw<br/>
 * 创建时间：2018-11-07 14:15 <br/>
 */
public interface IDistinctSendId {

    /**
     * 判断sendId是否重复
     *
     * @param sendId Message SendId
     * @return true:重复；false:不重复
     */
    boolean existsSendId(long sendId);

    /**
     * 新增SendId
     *
     * @param sendId Message SendId
     */
    void addSendId(long sendId);
}
