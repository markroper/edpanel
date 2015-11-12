package com.scholarscore.etl.runner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * User: jordan
 * Date: 11/12/15
 * Time: 5:21 PM
 */
public class ETLExecutable {
    
    public static void main(String... args) {
        loadSpringConfigAndLaunchETL();
    }

    public static void loadSpringConfigAndLaunchETL() {
        ApplicationContext context =
                new ClassPathXmlApplicationContext("etl.xml");
        ETLRunner runner = context.getBean(ETLRunner.class);
        runner.migrateDistrict();
    }
    
}
