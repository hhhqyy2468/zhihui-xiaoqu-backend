-- =============================================
-- 社区物业管理系统数据库设计
-- 数据库版本: MySQL 8.0
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- 创建日期: 2025-09-29
-- 设计规范: 遵循阿里《Java开发手册》，不使用外键
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS property_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE property_management;

create table bill
(
    id              bigint auto_increment comment '账单ID'
        primary key,
    bill_no         varchar(64)                              not null comment '账单编号',
    user_id         bigint                                   not null comment '业主用户ID',
    house_id        bigint                                   not null comment '房产ID',
    fee_type_id     bigint                                   not null comment '费用类型ID',
    fee_type_name   varchar(50)                              not null comment '费用类型名称（冗余字段，便于查询）',
    billing_period  varchar(20)                              not null comment '账期（如：2025-01）',
    amount          decimal(10, 2)                           not null comment '应缴金额',
    paid_amount     decimal(10, 2) default 0.00              null comment '实缴金额',
    discount_amount decimal(10, 2) default 0.00              null comment '减免金额',
    bill_status     tinyint        default 1                 null comment '账单状态：1-待缴费 2-已缴费 3-已超期',
    due_date        date                                     null comment '缴费截止日期',
    paid_time       datetime                                 null comment '缴费时间',
    pay_method      tinyint                                  null comment '支付方式：1-钱包支付',
    remark          varchar(500)                             null comment '备注',
    deleted         tinyint        default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_by       varchar(50)                              null comment '创建人',
    create_time     datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    update_by       varchar(50)                              null comment '更新人',
    update_time     datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint bill_no
        unique (bill_no)
)
    comment '账单表';

create index idx_bill_no
    on bill (bill_no);

create index idx_bill_status
    on bill (bill_status);

create index idx_billing_period
    on bill (billing_period);

create index idx_due_date
    on bill (due_date);

create index idx_house_id
    on bill (house_id);

create index idx_user_id
    on bill (user_id);

create table building
(
    id            bigint auto_increment comment '楼栋ID'
        primary key,
    building_no   varchar(50)                        not null comment '楼栋编号',
    building_name varchar(100)                       not null comment '楼栋名称',
    floor_count   int                                not null comment '楼层数',
    unit_count    int                                not null comment '单元数',
    address       varchar(255)                       null comment '详细地址',
    build_year    int                                null comment '建筑年份',
    remark        varchar(500)                       null comment '备注',
    deleted       tinyint  default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_by     varchar(50)                        null comment '创建人',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     varchar(50)                        null comment '更新人',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint building_no
        unique (building_no)
)
    comment '楼栋表';

create table complaint
(
    id                bigint auto_increment comment '投诉ID'
        primary key,
    complaint_no      varchar(64)                        not null comment '投诉单号',
    user_id           bigint                             not null comment '投诉人ID（业主）',
    user_name         varchar(50)                        not null comment '投诉人姓名（冗余字段）',
    house_id          bigint                             not null comment '房产ID',
    house_no          varchar(50)                        not null comment '房间编号（冗余字段）',
    complaint_type    varchar(50)                        not null comment '投诉类型（数据字典）',
    complaint_content text                               not null comment '投诉内容',
    image_urls        varchar(1000)                      null comment '图片URLs（多个用逗号分隔，最多5张）',
    urgency_level     tinyint  default 1                 null comment '紧急程度：1-普通 2-紧急',
    complaint_status  tinyint  default 1                 null comment '投诉状态：1-待处理 2-处理中 3-已处理 4-已关闭',
    handler_id        bigint                             null comment '处理人ID',
    handler_name      varchar(50)                        null comment '处理人姓名（冗余字段）',
    handle_content    text                               null comment '处理内容',
    handle_image_urls varchar(1000)                      null comment '处理图片URLs',
    handle_time       datetime                           null comment '处理完成时间',
    rating            tinyint                            null comment '满意度评价：1-满意 2-一般 3-不满意',
    rating_content    varchar(500)                       null comment '评价内容',
    rating_time       datetime                           null comment '评价时间',
    auto_close_time   datetime                           null comment '自动关闭时间（处理完成后7天）',
    deleted           tinyint  default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_time       datetime default CURRENT_TIMESTAMP null comment '投诉时间',
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint complaint_no
        unique (complaint_no)
)
    comment '投诉表';

create index idx_complaint_no
    on complaint (complaint_no);

create index idx_complaint_status
    on complaint (complaint_status);

create index idx_create_time
    on complaint (create_time);

create index idx_user_id
    on complaint (user_id);

create table fee_type
(
    id            bigint auto_increment comment '费用类型ID'
        primary key,
    type_name     varchar(50)                        not null comment '费用类型名称（物业费/水费/电费/停车费等）',
    type_code     varchar(50)                        not null comment '费用类型编码',
    unit_price    decimal(10, 2)                     null comment '单价',
    billing_unit  varchar(20)                        null comment '计费单位（月/季/年/平方米/度）',
    billing_cycle tinyint                            null comment '计费周期：1-月 2-季 3-年',
    description   varchar(500)                       null comment '费用说明',
    status        tinyint  default 1                 null comment '状态：0-停用 1-启用',
    deleted       tinyint  default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_by     varchar(50)                        null comment '创建人',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     varchar(50)                        null comment '更新人',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint type_code
        unique (type_code)
)
    comment '费用类型表';

create table flyway_schema_history
(
    installed_rank int                                 not null
        primary key,
    version        varchar(50)                         null,
    description    varchar(200)                        not null,
    type           varchar(20)                         not null,
    script         varchar(1000)                       not null,
    checksum       int                                 null,
    installed_by   varchar(100)                        not null,
    installed_on   timestamp default CURRENT_TIMESTAMP not null,
    execution_time int                                 not null,
    success        tinyint(1)                          not null
);

create index flyway_schema_history_s_idx
    on flyway_schema_history (success);

create table house
(
    id            bigint auto_increment comment '房产ID'
        primary key,
    building_id   bigint                             not null comment '楼栋ID',
    unit_id       bigint                             not null comment '单元ID',
    house_no      varchar(50)                        not null comment '房间编号（楼栋-单元-房号）',
    floor         int                                not null comment '楼层',
    room_number   varchar(50)                        not null comment '门牌号',
    house_type    varchar(50)                        null comment '户型（如：三室两厅）',
    building_area decimal(10, 2)                     not null comment '建筑面积（平方米）',
    usable_area   decimal(10, 2)                     null comment '使用面积（平方米）',
    house_status  tinyint  default 1                 null comment '房产状态：1-空置 2-已售 3-已租 4-自住',
    remark        varchar(500)                       null comment '备注',
    deleted       tinyint  default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_by     varchar(50)                        null comment '创建人',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     varchar(50)                        null comment '更新人',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint house_no
        unique (house_no)
)
    comment '房产表';

create index idx_building_id
    on house (building_id);

create index idx_house_status
    on house (house_status);

create index idx_unit_id
    on house (unit_id);

create table notice
(
    id                   bigint auto_increment comment '公告ID'
        primary key,
    notice_title         varchar(200)                       not null comment '公告标题',
    notice_type          varchar(50)                        not null comment '公告类型（数据字典）',
    notice_content       text                               not null comment '公告内容',
    attachment_urls      varchar(1000)                      null comment '附件URLs（多个用逗号分隔，最多3个）',
    is_top               tinyint  default 0                 null comment '是否置顶：0-否 1-是',
    publish_scope        tinyint  default 1                 null comment '发布范围：1-全部 2-指定楼栋 3-指定单元',
    target_building_ids  varchar(500)                       null comment '目标楼栋IDs（逗号分隔）',
    target_unit_ids      varchar(500)                       null comment '目标单元IDs（逗号分隔）',
    effective_start_time datetime                           null comment '生效开始时间',
    effective_end_time   datetime                           null comment '生效结束时间',
    notice_status        tinyint  default 1                 null comment '公告状态：1-已发布 2-已过期 3-已撤回',
    publisher_id         bigint                             not null comment '发布人ID',
    publisher_name       varchar(50)                        not null comment '发布人姓名（冗余字段）',
    read_count           int      default 0                 null comment '阅读次数',
    publish_time         datetime default CURRENT_TIMESTAMP null comment '发布时间',
    deleted              tinyint  default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_by            varchar(50)                        null comment '创建人',
    create_time          datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by            varchar(50)                        null comment '更新人',
    update_time          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '公告表';

create index idx_is_top
    on notice (is_top);

create index idx_notice_status
    on notice (notice_status);

create index idx_notice_type
    on notice (notice_type);

create index idx_publish_time
    on notice (publish_time);

create table notice_read
(
    id        bigint auto_increment comment '记录ID'
        primary key,
    notice_id bigint                             not null comment '公告ID',
    user_id   bigint                             not null comment '用户ID',
    read_time datetime default CURRENT_TIMESTAMP null comment '阅读时间',
    constraint uk_notice_user
        unique (notice_id, user_id)
)
    comment '公告阅读记录表';

create index idx_notice_id
    on notice_read (notice_id);

create index idx_user_id
    on notice_read (user_id);

create table parking_rental
(
    id            bigint auto_increment comment '租赁记录ID'
        primary key,
    rental_no     varchar(64)                              not null comment '租赁合同编号',
    user_id       bigint                                   not null comment '业主用户ID',
    user_name     varchar(50)                              not null comment '业主姓名（冗余字段）',
    space_id      bigint                                   not null comment '车位ID',
    space_no      varchar(50)                              not null comment '车位编号（冗余字段）',
    vehicle_plate varchar(20)                              not null comment '车牌号',
    vehicle_brand varchar(100)                             null comment '车辆品牌型号',
    vehicle_color varchar(20)                              null comment '车辆颜色',
    owner_name    varchar(50)                              null comment '车主姓名',
    rental_period tinyint                                  not null comment '租赁期限：1-月租 2-季租 3-半年租 4-年租',
    start_date    date                                     not null comment '租赁开始日期',
    end_date      date                                     not null comment '租赁结束日期',
    rental_amount decimal(10, 2)                           not null comment '租金总额',
    rental_status tinyint        default 1                 null comment '租赁状态：1-待审核 2-生效中 3-已到期 4-已退租',
    bill_id       bigint                                   null comment '关联账单ID',
    apply_time    datetime       default CURRENT_TIMESTAMP null comment '申请时间',
    audit_time    datetime                                 null comment '审核时间',
    auditor_id    bigint                                   null comment '审核人ID',
    refund_amount decimal(10, 2) default 0.00              null comment '退款金额（提前退租）',
    refund_time   datetime                                 null comment '退款时间',
    remark        varchar(500)                             null comment '备注',
    deleted       tinyint        default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_by     varchar(50)                              null comment '创建人',
    create_time   datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     varchar(50)                              null comment '更新人',
    update_time   datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint rental_no
        unique (rental_no)
)
    comment '车位租赁记录表';

create index idx_end_date
    on parking_rental (end_date);

create index idx_rental_no
    on parking_rental (rental_no);

create index idx_rental_status
    on parking_rental (rental_status);

create index idx_space_id
    on parking_rental (space_id);

create index idx_user_id
    on parking_rental (user_id);

create index idx_vehicle_plate
    on parking_rental (vehicle_plate);

create table parking_rental_application
(
    id                 bigint auto_increment comment '申请ID'
        primary key,
    parking_space_id   bigint                                not null comment '车位ID',
    space_no           varchar(50)                           not null comment '车位编号',
    owner_id           bigint                                not null comment '业主ID',
    owner_name         varchar(100)                          not null comment '业主姓名',
    contact_phone      varchar(20)                           not null comment '联系电话',
    vehicle_number     varchar(20)                           not null comment '车辆号码',
    vehicle_brand      varchar(100)                          null comment '车辆品牌',
    vehicle_color      varchar(50)                           null comment '车辆颜色',
    rental_start_date  date                                  not null comment '期望开始租赁日期',
    rental_end_date    date                                  null comment '期望结束租赁日期',
    rental_months      int         default 1                 null comment '租赁月数',
    application_reason text                                  null comment '申请原因',
    application_status tinyint     default 1                 null comment '申请状态：1-待审核 2-已通过 3-已驳回',
    review_user_id     bigint                                null comment '审核人ID',
    review_user_name   varchar(100)                          null comment '审核人姓名',
    review_time        datetime                              null comment '审核时间',
    review_remark      varchar(500)                          null comment '审核备注',
    deleted            tinyint     default 0                 null comment '删除标记',
    create_by          varchar(64) default ''                null comment '创建人',
    create_time        datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_by          varchar(64) default ''                null comment '更新人',
    update_time        datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '车位租赁申请表';

create index idx_application_status
    on parking_rental_application (application_status);

create index idx_create_time
    on parking_rental_application (create_time);

create index idx_deleted
    on parking_rental_application (deleted);

create index idx_owner_id
    on parking_rental_application (owner_id);

create index idx_parking_space_id
    on parking_rental_application (parking_space_id);

create table parking_space
(
    id           bigint auto_increment comment '车位ID'
        primary key,
    space_no     varchar(50)                              not null comment '车位编号',
    location     varchar(200)                             not null comment '车位位置（地下/地上，区域，层，区，号位）',
    space_status tinyint        default 1                 not null comment '车位状态：1-空闲 2-已租 3-维修中',
    monthly_rent decimal(10, 2) default 0.00              null comment '月租金',
    remark       varchar(500)                             null comment '备注',
    deleted      tinyint        default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_by    varchar(64)    default ''                null comment '创建人',
    create_time  datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    update_by    varchar(64)    default ''                null comment '更新人',
    update_time  datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_space_no
        unique (space_no)
)
    comment '车位表';

create index idx_deleted
    on parking_space (deleted);

create index idx_location
    on parking_space (location);

create index idx_space_status
    on parking_space (space_status);

create table payment_record
(
    id                    bigint auto_increment comment '缴费记录ID'
        primary key,
    payment_no            varchar(64)                        not null comment '缴费流水号',
    bill_id               bigint                             not null comment '账单ID',
    bill_no               varchar(64)                        not null comment '账单编号（冗余字段）',
    user_id               bigint                             not null comment '业主用户ID',
    amount                decimal(10, 2)                     not null comment '缴费金额',
    pay_method            tinyint                            not null comment '支付方式：1-钱包支付',
    wallet_transaction_id bigint                             null comment '钱包交易ID（钱包支付时）',
    payment_time          datetime default CURRENT_TIMESTAMP null comment '缴费时间',
    remark                varchar(500)                       null comment '备注',
    create_time           datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint payment_no
        unique (payment_no)
)
    comment '缴费记录表';

create index idx_bill_id
    on payment_record (bill_id);

create index idx_payment_no
    on payment_record (payment_no);

create index idx_payment_time
    on payment_record (payment_time);

create index idx_user_id
    on payment_record (user_id);

create table receipt
(
    id                bigint auto_increment comment '收据ID'
        primary key,
    receipt_no        varchar(64)                        not null comment '收据编号',
    bill_id           bigint                             not null comment '账单ID',
    payment_record_id bigint                             not null comment '缴费记录ID',
    user_id           bigint                             not null comment '业主用户ID',
    user_name         varchar(50)                        not null comment '业主姓名（冗余字段）',
    house_id          bigint                             not null comment '房产ID',
    house_no          varchar(50)                        not null comment '房间编号（冗余字段）',
    fee_type_name     varchar(50)                        not null comment '费用类型名称',
    billing_period    varchar(20)                        not null comment '账期',
    amount            decimal(10, 2)                     not null comment '收费金额',
    pay_method        varchar(20)                        not null comment '支付方式',
    receipt_time      datetime default CURRENT_TIMESTAMP null comment '收据生成时间',
    create_time       datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint receipt_no
        unique (receipt_no)
)
    comment '电子收据表';

create index idx_bill_id
    on receipt (bill_id);

create index idx_receipt_no
    on receipt (receipt_no);

create index idx_receipt_time
    on receipt (receipt_time);

create index idx_user_id
    on receipt (user_id);

create table repair_order
(
    id                   bigint auto_increment comment '维修工单ID'
        primary key,
    order_no             varchar(64)                              not null comment '工单编号',
    user_id              bigint                                   not null comment '报修人ID（业主）',
    user_name            varchar(50)                              not null comment '报修人姓名（冗余字段）',
    house_id             bigint                                   not null comment '房产ID',
    house_no             varchar(50)                              not null comment '房间编号（冗余字段）',
    repair_type          varchar(50)                              not null comment '维修类型（数据字典）',
    fault_description    text                                     not null comment '故障描述',
    image_urls           varchar(1000)                            null comment '故障图片URLs（多个用逗号分隔，最多5张）',
    urgency_level        tinyint        default 1                 null comment '紧急程度：1-普通 2-紧急',
    appointment_time     datetime                                 null comment '预约维修时间',
    order_status         tinyint        default 1                 null comment '工单状态：1-待派工 2-待接单 3-进行中 4-待验收 5-已完成',
    worker_id            bigint                                   null comment '维修人员ID',
    worker_name          varchar(50)                              null comment '维修人员姓名（冗余字段）',
    assign_time          datetime                                 null comment '派工时间',
    required_finish_time datetime                                 null comment '要求完成时间',
    actual_fault         varchar(500)                             null comment '实际故障原因',
    repair_content       text                                     null comment '维修内容',
    repair_image_urls    varchar(1000)                            null comment '维修后图片URLs',
    repair_cost          decimal(10, 2) default 0.00              null comment '维修费用',
    parts_replaced       varchar(500)                             null comment '更换配件',
    finish_time          datetime                                 null comment '完成时间',
    acceptance_result    tinyint                                  null comment '验收结果：1-合格 2-不合格',
    acceptance_rating    tinyint                                  null comment '验收评分：1-5星',
    acceptance_comment   varchar(500)                             null comment '验收意见',
    acceptance_time      datetime                                 null comment '验收时间',
    rework_count         int            default 0                 null comment '返工次数',
    auto_accept_time     datetime                                 null comment '自动验收时间（待验收后3天）',
    deleted              tinyint        default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_by            varchar(50)                              null comment '创建人',
    create_time          datetime       default CURRENT_TIMESTAMP null comment '报修时间',
    update_by            varchar(50)                              null comment '更新人',
    update_time          datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint order_no
        unique (order_no)
)
    comment '维修工单表';

create index idx_create_time
    on repair_order (create_time);

create index idx_order_no
    on repair_order (order_no);

create index idx_order_status
    on repair_order (order_status);

create index idx_user_id
    on repair_order (user_id);

create index idx_worker_id
    on repair_order (worker_id);

create table sys_dict_data
(
    id          bigint auto_increment comment '字典编码'
        primary key,
    dict_type   varchar(100)                       not null comment '字典类型',
    dict_label  varchar(100)                       not null comment '字典标签',
    dict_value  varchar(100)                       not null comment '字典键值',
    dict_sort   int      default 0                 null comment '显示排序',
    status      tinyint  default 1                 null comment '状态：0-禁用 1-启用',
    remark      varchar(500)                       null comment '备注',
    create_by   varchar(50)                        null comment '创建人',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   varchar(50)                        null comment '更新人',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '字典数据表';

create index idx_dict_type
    on sys_dict_data (dict_type);

create table sys_dict_type
(
    id          bigint auto_increment comment '字典主键'
        primary key,
    dict_name   varchar(100)                       not null comment '字典名称',
    dict_type   varchar(100)                       not null comment '字典类型',
    status      tinyint  default 1                 null comment '状态：0-禁用 1-启用',
    remark      varchar(500)                       null comment '备注',
    create_by   varchar(50)                        null comment '创建人',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   varchar(50)                        null comment '更新人',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint dict_type
        unique (dict_type)
)
    comment '字典类型表';

create table sys_login_log
(
    id             bigint auto_increment comment '访问ID'
        primary key,
    username       varchar(50)                        null comment '用户账号',
    user_id        bigint                             null comment '用户ID',
    ipaddr         varchar(50)                        null comment '登录IP地址',
    login_location varchar(255)                       null comment '登录地点',
    browser        varchar(50)                        null comment '浏览器类型',
    os             varchar(50)                        null comment '操作系统',
    status         tinyint  default 0                 null comment '登录状态：0-成功 1-失败',
    msg            varchar(255)                       null comment '提示消息',
    login_time     datetime default CURRENT_TIMESTAMP null comment '访问时间'
)
    comment '登录日志表';

create index idx_login_time
    on sys_login_log (login_time);

create index idx_status
    on sys_login_log (status);

create index idx_user_id
    on sys_login_log (user_id);

create index idx_username
    on sys_login_log (username);

create table sys_menu
(
    id          bigint auto_increment comment '菜单ID'
        primary key,
    menu_name   varchar(50)                        not null comment '菜单名称',
    parent_id   bigint   default 0                 null comment '父菜单ID，0表示根菜单',
    menu_type   char                               not null comment '菜单类型：M-目录 C-菜单 F-按钮',
    path        varchar(200)                       null comment '路由地址',
    perms       varchar(100)                       null comment '权限标识',
    icon        varchar(100)                       null comment '菜单图标',
    order_num   int      default 0                 null comment '显示顺序',
    status      tinyint  default 1                 null comment '状态：0-禁用 1-启用',
    create_by   varchar(50)                        null comment '创建人',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   varchar(50)                        null comment '更新人',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '菜单权限表';

create index idx_parent_id
    on sys_menu (parent_id);

create table sys_oper_log
(
    id             bigint auto_increment comment '日志主键'
        primary key,
    title          varchar(50)                        null comment '模块标题',
    business_type  tinyint  default 0                 null comment '业务类型：0-其它 1-新增 2-修改 3-删除 4-查询',
    method         varchar(200)                       null comment '方法名称',
    request_method varchar(10)                        null comment '请求方式（GET/POST/PUT/DELETE）',
    operator_type  tinyint  default 0                 null comment '操作类别：0-其它 1-后台用户 2-手机端用户',
    oper_name      varchar(50)                        null comment '操作人员',
    oper_user_id   bigint                             null comment '操作人员ID',
    oper_url       varchar(500)                       null comment '请求URL',
    oper_ip        varchar(50)                        null comment '主机地址',
    oper_location  varchar(255)                       null comment '操作地点',
    oper_param     text                               null comment '请求参数',
    json_result    text                               null comment '返回参数',
    status         tinyint  default 0                 null comment '操作状态：0-成功 1-失败',
    error_msg      varchar(2000)                      null comment '错误消息',
    oper_time      datetime default CURRENT_TIMESTAMP null comment '操作时间'
)
    comment '操作日志表';

create index idx_business_type
    on sys_oper_log (business_type);

create index idx_oper_time
    on sys_oper_log (oper_time);

create index idx_oper_user_id
    on sys_oper_log (oper_user_id);

create table sys_role
(
    id          bigint auto_increment comment '角色ID'
        primary key,
    role_name   varchar(50)                        not null comment '角色名称',
    role_key    varchar(50)                        not null comment '角色权限字符串',
    role_sort   int      default 0                 null comment '显示顺序',
    status      tinyint  default 1                 null comment '状态：0-禁用 1-启用',
    remark      varchar(500)                       null comment '备注',
    deleted     tinyint  default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_by   varchar(50)                        null comment '创建人',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by   varchar(50)                        null comment '更新人',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint role_key
        unique (role_key)
)
    comment '角色表';

create table sys_role_menu
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    role_id     bigint                             not null comment '角色ID',
    menu_id     bigint                             not null comment '菜单ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uk_role_menu
        unique (role_id, menu_id)
)
    comment '角色菜单关联表';

create index idx_menu_id
    on sys_role_menu (menu_id);

create index idx_role_id
    on sys_role_menu (role_id);

create table sys_user
(
    id                bigint auto_increment comment '用户ID'
        primary key,
    username          varchar(50)                        not null comment '用户名（登录账号）',
    password          varchar(100)                       not null comment '密码（BCrypt加密）',
    real_name         varchar(50)                        not null comment '真实姓名',
    phone             varchar(20)                        not null comment '手机号码',
    id_card           varchar(18)                        null comment '身份证号',
    email             varchar(100)                       null comment '邮箱',
    gender            tinyint  default 0                 null comment '性别：0-未知 1-男 2-女',
    avatar            varchar(255)                       null comment '头像URL',
    resident_type     tinyint                            null comment '住户类型：1-业主 2-租户（仅业主角色有值）',
    check_in_date     date                               null comment '入住日期',
    resident_status   tinyint  default 1                 null comment '住户状态：1-在住 2-已搬离',
    emergency_contact varchar(50)                        null comment '紧急联系人',
    emergency_phone   varchar(20)                        null comment '紧急联系电话',
    status            tinyint  default 1                 null comment '状态：0-禁用 1-启用',
    user_type         tinyint                            not null comment '用户类型：1-系统管理员 2-物业管理员 3-业主 4-维修人员',
    remark            varchar(500)                       null comment '备注',
    deleted           tinyint  default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_by         varchar(50)                        null comment '创建人',
    create_time       datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by         varchar(50)                        null comment '更新人',
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint username
        unique (username)
)
    comment '用户表';

create index idx_phone
    on sys_user (phone);

create index idx_resident_status
    on sys_user (resident_status);

create index idx_status
    on sys_user (status);

create index idx_user_type
    on sys_user (user_type);

create index idx_username
    on sys_user (username);

create table sys_user_role
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    role_id     bigint                             not null comment '角色ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uk_user_role
        unique (user_id, role_id)
)
    comment '用户角色关联表';

create index idx_role_id
    on sys_user_role (role_id);

create index idx_user_id
    on sys_user_role (user_id);

create table unit
(
    id                   bigint auto_increment comment '单元ID'
        primary key,
    building_id          bigint                             not null comment '楼栋ID',
    unit_no              varchar(50)                        not null comment '单元编号',
    unit_name            varchar(100)                       not null comment '单元名称',
    floor_count          int                                not null comment '楼层数',
    room_count_per_floor int                                not null comment '每层房间数',
    remark               varchar(500)                       null comment '备注',
    deleted              tinyint  default 0                 null comment '删除标记：0-未删除 1-已删除',
    create_by            varchar(50)                        null comment '创建人',
    create_time          datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by            varchar(50)                        null comment '更新人',
    update_time          datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '单元表';

create index idx_building_id
    on unit (building_id);

create table user_house
(
    id            bigint auto_increment comment '关联ID'
        primary key,
    user_id       bigint                             not null comment '用户ID',
    house_id      bigint                             not null comment '房产ID',
    relation_type tinyint                            not null comment '关系类型：1-业主 2-租户',
    start_date    date                               not null comment '开始日期',
    end_date      date                               null comment '结束日期',
    is_current    tinyint  default 1                 null comment '是否当前居住：0-否 1-是',
    remark        varchar(500)                       null comment '备注',
    create_by     varchar(50)                        null comment '创建人',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_by     varchar(50)                        null comment '更新人',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_user_house_current
        unique (user_id, house_id, is_current)
)
    comment '用户房产关联表';

create index idx_house_id
    on user_house (house_id);

create index idx_is_current
    on user_house (is_current);

create index idx_user_id
    on user_house (user_id);

create table wallet
(
    id                       bigint auto_increment comment '钱包ID'
        primary key,
    user_id                  bigint                                   not null comment '用户ID（业主）',
    balance                  decimal(10, 2) default 0.00              null comment '当前余额',
    total_recharge           decimal(10, 2) default 0.00              null comment '累计充值金额',
    total_consume            decimal(10, 2) default 0.00              null comment '累计消费金额',
    pay_password             varchar(100)                             null comment '支付密码（BCrypt加密）',
    pay_password_error_count int            default 0                 null comment '支付密码错误次数',
    pay_password_lock_time   datetime                                 null comment '支付密码锁定时间',
    status                   tinyint        default 1                 null comment '钱包状态：1-正常 2-冻结',
    version                  int            default 0                 null comment '乐观锁版本号',
    create_time              datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    update_time              datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint user_id
        unique (user_id)
)
    comment '虚拟钱包表';

create index idx_status
    on wallet (status);

create index idx_user_id
    on wallet (user_id);

create table wallet_transaction
(
    id                 bigint auto_increment comment '交易ID'
        primary key,
    transaction_no     varchar(64)                        not null comment '交易流水号',
    user_id            bigint                             not null comment '用户ID',
    wallet_id          bigint                             not null comment '钱包ID',
    transaction_type   tinyint                            not null comment '交易类型：1-充值 2-消费 3-退款',
    amount             decimal(10, 2)                     not null comment '交易金额',
    balance_before     decimal(10, 2)                     not null comment '交易前余额',
    balance_after      decimal(10, 2)                     not null comment '交易后余额',
    related_bill_id    bigint                             null comment '关联账单ID（消费时）',
    related_order_no   varchar(64)                        null comment '关联订单号',
    transaction_status tinyint  default 1                 null comment '交易状态：1-成功 2-失败',
    remark             varchar(500)                       null comment '备注',
    create_time        datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint transaction_no
        unique (transaction_no)
)
    comment '钱包交易记录表';

create index idx_create_time
    on wallet_transaction (create_time);

create index idx_transaction_no
    on wallet_transaction (transaction_no);

create index idx_transaction_type
    on wallet_transaction (transaction_type);

create index idx_user_id
    on wallet_transaction (user_id);

create index idx_wallet_id
    on wallet_transaction (wallet_id);



-- =============================================
-- 数据库设计说明
-- =============================================
/*
设计规范：
1. 所有表使用 InnoDB 引擎，支持事务
2. 字符集统一使用 utf8mb4，支持 emoji 和特殊字符
3. 主键统一使用 BIGINT 自增
4. 时间字段统一使用 DATETIME 类型
5. 金额字段统一使用 DECIMAL(10,2) 类型
6. 不使用外键约束，在应用层维护数据一致性
7. 重要字段添加索引，提高查询效率
8. 采用逻辑删除（deleted 字段），不物理删除数据
9. 统一添加创建人、创建时间、更新人、更新时间字段
10. 冗余必要的字段（如姓名、编号），减少表关联查询

表结构汇总：
一、用户权限模块（5张表）
  1. sys_user - 用户表（增强版，包含住户信息）
  2. sys_role - 角色表
  3. sys_user_role - 用户角色关联表
  4. sys_menu - 菜单权限表
  5. sys_role_menu - 角色菜单关联表

二、数据字典模块（2张表）
  6. sys_dict_type - 数据字典类型表
  7. sys_dict_data - 数据字典数据表

三、住户房产模块（4张表）
  8. building - 楼栋表
  9. unit - 单元表
  10. house - 房产表
  11. user_house - 用户房产关联表

四、虚拟钱包模块（2张表）
  12. wallet - 虚拟钱包表
  13. wallet_transaction - 钱包交易记录表

五、费用管理模块（4张表）
  14. fee_type - 费用类型表
  15. bill - 账单表
  16. payment_record - 缴费记录表
  17. receipt - 电子收据表

六、投诉管理模块（1张表）
  18. complaint - 投诉表

七、维修管理模块（1张表）
  19. repair_order - 维修工单表

八、公告管理模块（2张表）
  20. notice - 公告表
  21. notice_read - 公告阅读记录表

九、停车管理模块（2张表）
  22. parking_space - 车位表
  23. parking_rental - 车位租赁记录表

十、系统日志模块（2张表）
  24. sys_oper_log - 操作日志表
  25. sys_login_log - 登录日志表

总计：25张表

核心业务表关系：
- sys_user 是用户核心表，关联 wallet（业主钱包）、user_house（房产关系）
- house 是房产核心表，关联 user_house（用户关系）、bill（账单）
- bill 是账单核心表，关联 payment_record（缴费记录）、receipt（电子收据）
- wallet 是钱包核心表，关联 wallet_transaction（交易记录）
- 所有业务单据表都有唯一编号字段（xxxx_no）

性能优化建议：
1. 对高频查询字段建立索引
2. 钱包表使用乐观锁（version 字段）防止并发问题
3. 交易记录表按月分表（数据量大时）
4. 日志表定期归档（保留6个月数据）
5. 使用 Redis 缓存热点数据（用户信息、字典数据等）

业务流程对应表说明：
1. 用户管理流程 → sys_user, sys_role, sys_user_role
2. 住户与房产管理流程 → sys_user, building, unit, house, user_house
3. 虚拟钱包管理流程 → wallet, wallet_transaction
4. 费用管理流程 → fee_type, bill, payment_record, receipt
5. 投诉管理流程 → complaint
6. 维修管理流程 → repair_order
7. 公告管理流程 → notice, notice_read
8. 停车管理流程 → parking_space, parking_rental
9. 统计报表流程 → 基于各业务表统计
10. 系统维护流程 → sys_oper_log, sys_login_log

重要更新说明：
1. 删除滞纳金相关字段（bill.late_fee）
2. 删除线下支付相关字段（operator_id, operator_name等）
3. 简化车位管理，删除车位类型区分（space_type, hourly_rate）
4. 支付方式只保留钱包支付
5. 初始化维修人员A和B，以及20个业主用户
*/