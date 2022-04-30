package com.jobeth.common.util;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
public class SpringContextUtils {

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


    public static SqlSession getSqlBatchSession() {
        SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        return sqlSessionFactory.openSession(ExecutorType.BATCH, false);
    }
}