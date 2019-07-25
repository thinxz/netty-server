package com.thinxz;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author thinxz
 */
@SpringBootApplication
public class NettyServerApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = new SpringApplicationBuilder(NettyServerApp.class)
                // 禁用解析命令行参数, 启动时命令参数失效
                //.addCommandLineProperties(false)
                // 输出启动程序PID
                //.listeners(new ApplicationPidFileWriter("./app.pid"))
                .run(args);
        ApplicationContext applicationContext = run;
    }
}
