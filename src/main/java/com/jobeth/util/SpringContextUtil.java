package com.jobeth.util;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
public class SpringContextUtil  {

    /**
     * Spring上下文
     */
    public static ApplicationContext applicationContext;

    @SuppressWarnings("unchecked")
    public static <B> B getBean(String beanName) {
        return (B) applicationContext.getBean(beanName);
    }

    public static <B> B getBean(Class<B> clazz) {
        return applicationContext.getBean(clazz);
    }


    public static  SqlSession getSqlBatchSession(){
        SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        return sqlSessionFactory.openSession(ExecutorType.BATCH,false);
    }
}