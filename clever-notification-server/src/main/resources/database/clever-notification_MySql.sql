-- create database `clever-notification`;
-- use `clever-notification`;


/* ====================================================================================================================
    service_sys -- 服务系统
==================================================================================================================== */
create table service_sys
(
    id                  bigint          not null        auto_increment                          comment '主键id',
    sys_name            varchar(127)    not null        unique                                  comment '系统(或服务)名称',
    redis_name_space    varchar(127)    not null        unique                                  comment '全局的Session Redis前缀',
    description         varchar(511)                                                            comment '说明',
    create_at           datetime(3)     not null        default current_timestamp(3)            comment '创建时间',
    update_at           datetime(3)                     on update current_timestamp(3)          comment '更新时间',
    primary key (id)
) comment = '服务系统';
create index service_sys_sys_name on service_sys (sys_name);
/*------------------------------------------------------------------------------------------------------------------------

消息模版
消息发送日志
接受者黑名单


3.7.8-alpine, 3.7-alpine, 3-alpine, alpine (3.7/alpine/Dockerfile)




--------------------------------------------------------------------------------------------------------------------------*/












