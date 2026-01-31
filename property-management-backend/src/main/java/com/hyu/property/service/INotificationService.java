package com.hyu.property.service;

import com.hyu.property.domain.Notification;

/**
 * 通知服务接口
 *
 * @author system
 * @date 2025-01-31
 */
public interface INotificationService {

    /**
     * 发送通知
     *
     * @param notification 通知对象
     * @return 结果
     */
    boolean send(Notification notification);

    /**
     * 发送到期提醒
     *
     * @param userId 用户ID
     * @param title 标题
     * @param content 内容
     * @return 结果
     */
    boolean sendExpiryReminder(Long userId, String title, String content);

    /**
     * 批量发送通知
     *
     * @param notification 通知对象
     * @param userIds 用户ID列表
     * @return 结果
     */
    boolean sendBatch(Notification notification, Long... userIds);
}
