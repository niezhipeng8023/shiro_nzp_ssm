# shiro_nzp_ssm
本文是简单的实现了shiro + ssm 的简单整合和实现。
1.数据库 shiro.sql
/*
MySQL - 5.5.40 : Database - shirotest
*********************************************************************
*/
CREATE DATABASE /*!32312 IF NOT EXISTS*/`shirotest` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `shirotest`;

/*Table structure for table `permission` */

DROP TABLE IF EXISTS `permission`;

CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(30) NOT NULL,
  `roleid` int(11) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `permission` */

insert  into `permission`(`id`,`url`,`roleid`,`description`) values (1,'/readName',1,'查看名单'),(2,'/readData',2,'查看数据');

/*Table structure for table `role` */

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role` varchar(20) NOT NULL,
  `description` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `role` */

insert  into `role`(`id`,`role`,`description`) values (1,'管理员','管理员'),(2,'普通用户','普通用户');

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(20) NOT NULL,
  `password` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

/*Data for the table `user` */

insert  into `user`(`id`,`account`,`password`) values (1,'123','123'),(2,'1234','1234');

/*Table structure for table `user_role` */

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` int(11) NOT NULL,
  `roleid` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 CHECKSUM=1 DELAY_KEY_WRITE=1 ROW_FORMAT=DYNAMIC;

/*Data for the table `user_role` */

insert  into `user_role`(`id`,`userid`,`roleid`) values (1,1,1),(2,2,2);

2. 配置web.xml 文件
3.配置applicationContext-service.xml  
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:p="http://www.springframework.org/schema/p"
       xmlns:aop="http://www.springframework.org/schema/aop" xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:jdbc="http://www.springframework.org/schema/jdbc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
	http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.0.xsd
	http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop-4.0.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-4.0.xsd
	http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-4.0.xsd http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc.xsd">

    <!-- spring自动去扫描base-pack下面或者子包下面的java文件-->
    <!--管理Service实现类-->
    <context:component-scan base-package="com.service"/>

    <!-- 配置事务管理器 -->
    <!-- 配置事务管理器 -->
    <bean id="transactionManager"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <!-- 注入数据库连接池 -->
        <property name="dataSource" ref="dataSource" />
    </bean>


    <!-- 通知 -->
    <tx:advice id="txAdvice" transaction-manager="transactionManager">
        <tx:attributes>
            <!-- 传播行为 -->
            <tx:method name="save*" propagation="REQUIRED" />
            <tx:method name="insert*" propagation="REQUIRED" />
            <tx:method name="add*" propagation="REQUIRED" />
            <tx:method name="create*" propagation="REQUIRED" />
            <tx:method name="delete*" propagation="REQUIRED" />
            <tx:method name="update*" propagation="REQUIRED" />
            <tx:method name="find*" propagation="SUPPORTS" read-only="true" />
            <tx:method name="select*" propagation="SUPPORTS" read-only="true" />
            <tx:method name="get*" propagation="SUPPORTS" read-only="true" />
        </tx:attributes>
    </tx:advice>
    <!-- 切面 -->
    <aop:config>
        <aop:advisor advice-ref="txAdvice"
                     pointcut="execution(* com.service.*.*(..))" />
    </aop:config>


    <!-- 缓存管理器 使用Ehcache实现 -->
    <bean id="cacheManager" class="org.apache.shiro.cache.MemoryConstrainedCacheManager" />

    <!-- 配置Realm,自己定义的myRealm,必须继承AuthorizingRealm -->
    <bean id="myRealm" class="com.shiro.MyReaml">
        <property name="cacheManager" ref="cacheManager" />
        <!-- 配置加密器 -->
        <property name="credentialsMatcher">
            <bean class="org.apache.shiro.authc.credential.HashedCredentialsMatcher">
                <property name="hashAlgorithmName" value="MD5"></property> <!-- 加密算法的名称 -->
                <property name="hashIterations" value="1"></property> <!-- 配置加密的次数 -->
            </bean>
        </property>
    </bean>

    <!-- 1. 配置securityManager，也就是shiro的核心。 -->
    <bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
        <property name="realm" ref="myRealm" />
        <!-- 缓存管理器 -->
        <property name="cacheManager" ref="cacheManager" />
    </bean>

    <!-- 配置shiroFilter id必须和web.xml 文件中配置的DelegatingFilterProxy的filter-name一致 -->
    <bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
        <!-- Shiro的核心安全接口,这个属性是必须的 -->
        <property name="securityManager" ref="securityManager" />
        <property name="loginUrl" value="/user/login" />    <!--没有登录的时候，跳转到这个页面-->
        <property name="unauthorizedUrl" value="/user/nopermission" /> <!--当没有权限的时候，跳转到这个url-->

        <property name="filterChainDefinitions">
            <value>
                /logout.action=logout
                /user/login = anon <!--可以不需要登录-->
                /user/readName = authc, perms[/readName]  <!-- perms 表示需要该权限才能访问的页面 -->
                /user/readData = authc, perms[/readData]
                /user/index = authc, perms[/read]
                /user/* = authc <!-- authc 表示需要认证才能访问的页面 -->
            </value>
        </property>
    </bean>


</beans>
4.applicationContext-mvc.xml  
5.applicationContext-dao.xml   
等等。。。。。






















