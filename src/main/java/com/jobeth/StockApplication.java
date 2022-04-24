package com.jobeth;

import com.jobeth.util.SpringContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/**
 * Created with IntelliJ IDEA.
 *
 * @author JyrpoKoo
 * @date 2022/4/17 12:45:45
 * Description: -
 */
@SpringBootApplication
@MapperScan("com.jobeth.mapper")
public class StockApplication {
    public static void main(String[] args) {
        SpringContextUtil.applicationContext = SpringApplication.run(StockApplication.class,args);
    }
}
