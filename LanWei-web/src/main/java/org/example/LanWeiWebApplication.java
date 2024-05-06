package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 张根勇
 */
@EnableTransactionManagement
@SpringBootApplication
public class LanWeiWebApplication {
    public static void main(String[] args) {
        // 命令行 参数 输入
        System.out.println(args[0]);
        System.out.println(args[1]);
        // b'n
        SpringApplication.run(LanWeiWebApplication.class,args);
        System.out.println("项目启动成功");
    }
}