drop database stock_db;
create database stock_db character set utf8mb4 collate utf8mb4_0900_ai_ci;

use stock_db;
create table plate_info
(
    code          varchar(20) null comment '代码',
    name          varchar(55) null comment '名字',
    category_type varchar(1)  null comment '1:地域
2:行业
3:概念',
    category_name varchar(50) null comment '板块分类名字'
)
    comment '板块';

create table plate_stock_info
(
    plate_code varchar(20) null comment '板块编码',
    stock_code varchar(50) null comment '股票代码',
    stock_name varchar(50) null comment '股票简称'
)
    comment '板块成分股';

create table stock_day_info
(
    code          varchar(20)   null comment '股票代码',
    date          date          null comment '日期',
    open          decimal(6, 2) null comment '开盘价',
    close         decimal(6, 2) null comment '收盘价',
    highest       decimal(6, 2) null comment '最高价',
    lowest        decimal(6, 2) null comment '最低价',
    volume        int           null comment '成交量',
    turnover      bigint        null comment '成交额',
    turnover_rate decimal(3, 2) null comment '换手率'
)
    comment '日k数据';

create table stock_info
(
    MARKET_CODE     varchar(20)  null comment '股票代码',
    A_STOCK_CODE    varchar(20)  null comment '股票代码',
    B_STOCK_CODE    varchar(20)  null comment 'B股代码',
    SEC_NAME_CN     varchar(255) null comment '股票简称',
    SEC_NAME_FULL   varchar(255) null comment '股票全称',
    COMPANY_ABBR_EN varchar(255) null comment '公司名称（英语）',
    COMPANY_ABBR    varchar(255) null comment '公司名称',
    COMPANY_CODE    varchar(20)  null comment '股票代码',
    LIST_DATE       date         null comment '上市时间',
    LIST_BOARD      varchar(10)  null comment '上市板块',
    AB_FLAG         varchar(10)  null comment 'A： A股
B：B股'
)
    comment '股票信息表';
