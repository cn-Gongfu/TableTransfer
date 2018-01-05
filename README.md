# 数据迁移
## 不同数据库,不同表结构的数据迁移

## 操作流程
### 配置jdbc信息, 源数据库连接信息 和 目标数据库连接信息
```
SRC_DBDRIVER=oracle.jdbc.driver.OracleDriver
SRC_DBURL=jdbc:oracle:thin:@192.168.10.46:1521:ORCL
SRC_DBUSER=gs_wsbs_old
SRC_DBPASS=gs_wsbs_old

TARGET_DBDRIVER=oracle.jdbc.driver.OracleDriver
TARGET_DBURL=jdbc:oracle:thin:@192.168.10.204:1521:ORCL
TARGET_DBUSER=APL_ONLINE
TARGET_DBPASS=APL_ONLINE
```
### 配置SqlMapper.xml 映射文件, 将源sql语句得到的结果取出来, 将获取的数据作为insert 参数目的表.
```
<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<TABLES>
<!-- 如果 enable = off 则忽略这张表 -->
	<TABLE enable="off"> 
	<!-- 源数据库数据 -->
		<SRC>
		<![CDATA[
SELECT
	BOD500 ID,
	bsc011 username,
	AAC003 real_name,
	AAC002 id_card,
	AAB004 company,
	AAB007 org_code,
	aae007 zip_code,
	AAE006 address,
	aae014 phone,
	AAE015 email,
	AAE005 tel,
	aac011 age,
	bod50a JOB,
	bab204 qq,
	CASE bod509 WHEN '1' then '2' WHEN '2' THEN '1' END account_type
       
FROM
    OD50
WHERE
    bod50c != '3'

		]]>
		</SRC>
		
		<!-- 目标数据库数据 COUNT属性表示参数个数 -->
		<TARGET COUNT='15'>
		<![CDATA[
INSERT INTO SYS_USER (
	ID,
	username,
	real_name,
	id_card,
	company,
	org_code,
	zip_code,
	address,
	phone,
	email,
	tel,
	age,
	JOB,
	qq,
	account_type
)
VALUES
	(
		?,?,?,?,?,?,?,?,?,?,?,?,?,?,?
	)			
		]]>
		</TARGET>
	</TABLE>
	
	<TABLE enable="on">
		<SRC>
		<![CDATA[
SELECT
	c.bsc001 dept_id,
	1 object_type,
	CASE A .BOD541
WHEN '001' THEN
	0
WHEN '002' THEN
	2
WHEN '003' THEN
	1
WHEN '004' THEN
	4
ELSE
	1
END consult_type,
 A .bod545 OPEN,
 A .aac003 NAME,
 A .aac004 gender,
 A .BOD54C age,
 A .aac030 phone,
 A .aae005 tel,
 A .aae015 email,
 A .bod54b card_code,
 A .aae007 zip_code,
 A .aae006 address,
 A .bod542 mail_title,
 A .bod543 mail_context,
 A .aae006 LOCAL,
 A .bod548 interact_code,
 888888 PASSWORD,
 A .aae036 consult_time,
 b.bod555 reply_user_name,
 b.bod552 reply_context,
 b.bod557 reply_time,
 NVL2 (b.bod557, 1, 0) reply_status,
 A .bod500 user_id
FROM
	od54 A
LEFT JOIN OD55 b ON A .bod540 = b.BOD540
LEFT JOIN SC01 c ON A .aae017 = c.bsc001		
		]]>
		</SRC>
		<TARGET COUNT='24'>
		<![CDATA[
INSERT INTO INTERACT_CONTENT (
    id,
    dept_id,
    object_type,
    consult_type,
    open,
    name,
    gender,
    age,
    phone,
    tel,
    email,
    card_code,
    zip_code,
    address,
    mail_title,
    mail_context,
    local,
    interact_code,
    password,
    consult_time,
    reply_user_name,
    reply_context,
    reply_time,
    reply_status,
    user_id
)
values(SEQ_INTERACT_CONTENT.nextval,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)

		]]>
		</TARGET>
	</TABLE>
</TABLES>

```

### 执行Main类的main函数.根据提示在命令行输入配置文件路径.
