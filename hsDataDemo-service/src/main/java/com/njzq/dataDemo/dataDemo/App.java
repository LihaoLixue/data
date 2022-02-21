package com.njzq.dataDemo.dataDemo;

import com.hundsun.jrescloud.common.boot.CloudApplication;
import com.hundsun.jrescloud.common.boot.CloudBootstrap;
import org.springframework.boot.SpringApplication;

/**
 * Hello world!
 *
 */
@CloudApplication
public class App
{
    public static void main( String[] args )
    {
        try {
            System.out.println("启动了+++++++++++++++++++++++数据团队接口");
            CloudBootstrap.run(App.class, args);
//            SpringApplication.run(App.class, args);
        } catch (Exception e) {
            System.out.println("启动报错");
            e.printStackTrace();
        }
    }
}
