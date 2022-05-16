package com.jobeth;

import com.jobeth.common.util.SpringContextUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
@SpringBootApplication
@MapperScan("com.jobeth.mapper")
@EnableCaching
public class StockApplication {
    public static void main(String[] args) {
        SpringContextUtils.applicationContext = SpringApplication.run(StockApplication.class,args);
    }
}
