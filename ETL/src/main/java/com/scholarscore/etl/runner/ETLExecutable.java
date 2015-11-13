package com.scholarscore.etl.runner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * User: jordan
 * Date: 11/12/15
 * Time: 5:21 PM
 */
public class EtlExecutable {
    
    private static final String ETL_SPRING_CONTEXT_FILENAME = "etl.xml";
    
    public static void main(String... args) {
        loadSpringConfigAndLaunchETL();
    }

    public static void loadSpringConfigAndLaunchETL() {
        ApplicationContext context =
                new ClassPathXmlApplicationContext(ETL_SPRING_CONTEXT_FILENAME);
        EtlRunner runner = context.getBean(EtlRunner.class);
        runner.migrateDistrict();
    }
    
}
