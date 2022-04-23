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
@Component
public class SpringContextUtil implements ApplicationContextAware {

    /**
     * Spring上下文
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

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