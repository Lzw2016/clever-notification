-- create database `clever-notification`;
-- use `clever-notification`;


/* ====================================================================================================================
    message_template -- 消息模版
==================================================================================================================== */
create table message_template
(
    id              bigint          not null        auto_increment                          comment '主键id',
    name            varchar(127)    not null        unique                                  comment '模版名称',
    content         MediumText      not null                                                comment '模版内容',
    message_demo    MediumText                                                              comment '模版消息示例',
    enabled         int(1)          not null        default 1                               comment '是否启用，0：禁用；1：启用',
    description     varchar(1023)                                                           comment '说明',
    create_at       datetime(3)     not null        default current_timestamp(3)            comment '创建时间',
    update_at       datetime(3)                     on update current_timestamp(3)          comment '更新时间',
    primary key (id)
) comment = '消息模版';
create index message_template_name on message_template (name);
create index message_template_title on message_template (title);


/* ====================================================================================================================
    message_send_log -- 消息发送日志
==================================================================================================================== */
create table message_send_log
(
    id              bigint          not null        auto_increment                          comment '主键id',
    send_id         bigint          not null        unique                                  comment '消息发送ID',
    sys_name        varchar(127)    not null                                                comment '系统名称',
    message_type    int(1)          not null                                                comment '消息类型，1：邮件；2：短信；...',
    template_name   varchar(127)                                                            comment '消息模版名称',
    params          MediumText                                                              comment '消息参数Json字符串',
    content         MediumText      not null                                                comment '发送消息内容',
    send_state      int(1)          not null        default 1                               comment '发送状态，1：发送中；2：发送失败；3：发送失败',
    fail_reason     varchar(511)                                                            comment '发送失败原因',
    send_time       datetime(3)     not null                                                comment '发送时间',
    use_time        int(1)                                                                  comment '发送消息耗时(毫秒)',
    create_at       datetime(3)     not null        default current_timestamp(3)            comment '创建时间',
    update_at       datetime(3)                     on update current_timestamp(3)          comment '更新时间',
    primary key (id)
) comment = '消息发送日志';
create index message_send_log_send_id on message_send_log (send_id);


/* ====================================================================================================================
    receiver_black_list -- 接收者黑名单
==================================================================================================================== */
create table receiver_black_list
(
    id              bigint          not null        auto_increment                          comment '主键id',
    sys_name        varchar(127)                                                            comment '系统名称(为空就是全局黑名单)',
    message_type    int(1)          not null                                                comment '消息类型，1：邮件；2：短信；...',
    account         varchar(127)    not null                                                comment '黑名单帐号',
    enabled         int(1)          not null        default 1                               comment '是否启用，0：禁用；1：启用',
    expired_time    datetime(3)                                                             comment '黑名单帐号过期时间(到期自动禁用)',
    create_at       datetime(3)     not null        default current_timestamp(3)            comment '创建时间',
    update_at       datetime(3)                     on update current_timestamp(3)          comment '更新时间',
    primary key (id)
) comment = '接收者黑名单';
create index receiver_black_list_account on receiver_black_list (account);


/* ====================================================================================================================
    sys_bind_email -- 系统邮件发送者帐号
==================================================================================================================== */
create table sys_bind_email
(
    id              bigint          not null        auto_increment                          comment '主键id',
    sys_name        varchar(127)    not null                                                comment '系统名称(全局使用“root”名称)',
    account         varchar(255)    not null        unique                                  comment '发送人的邮箱帐号',
    password        varchar(255)    not null                                                comment '发送人的邮箱密码',
    from_name       varchar(127)                                                            comment '发送人的名称',
    smtp_host       varchar(127)                                                            comment 'SMTP服务器地址',
    pop3_host       varchar(127)                                                            comment 'POP3服务器地址',
    enabled         int(1)          not null        default 1                               comment '是否启用，0：禁用；1：启用',
    create_at       datetime(3)     not null        default current_timestamp(3)            comment '创建时间',
    update_at       datetime(3)                     on update current_timestamp(3)          comment '更新时间',
    primary key (id)
) comment = '系统邮件发送者帐号';
create index sys_bind_email_sys_name on sys_bind_email (sys_name);
create index sys_bind_email_account on sys_bind_email (account);












/*------------------------------------------------------------------------------------------------------------------------



消息类型：邮件、短信、微信公众号消息、微信小程序消息、Web-PC消息、APP消息、


系统短信发送帐号密钥配置



扩展功能：
1. 限制消息发送频率(分钟，小时，天，周，月)
2.

--------------------------------------------------------------------------------------------------------------------------*/












