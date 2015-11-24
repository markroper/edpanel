package com.scholarscore.etl.runner;

import org.apache.commons.cli.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

/**
 * User: jordan
 * Date: 11/12/15
 * Time: 5:21 PM
 */
public class EtlExecutable {

    public static final String ARGS_GPA_FILE_SETTINGS = "g";
    public static final String ARGS_GPA_FILE_SETTINGS_LONG = "gpa-file";

    private static final String ETL_SPRING_CONTEXT_FILENAME = "etl.xml";
    
    public static void main(String... args) throws ParseException {

        EtlSettings settings = parseSettings(args);
        loadSpringConfigAndLaunchETL(settings);
    }

    private static EtlSettings parseSettings(String[] args) throws ParseException {

        Options options = new Options();
        //String opt, String longOpt, boolean hasArg, String description)
        options.addOption(new Option(ARGS_GPA_FILE_SETTINGS, ARGS_GPA_FILE_SETTINGS_LONG, false,
                "Specify the GPA file to parse as part of ETL loading from the external source"));

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);

        EtlSettings settings = new EtlSettings();

        if (cmd.hasOption(ARGS_GPA_FILE_SETTINGS)) {
            settings.setGpaImportFile(new File(cmd.getOptionValue(ARGS_GPA_FILE_SETTINGS)));
        }
        return settings;
    }

    public static void loadSpringConfigAndLaunchETL(EtlSettings settings) {
        ApplicationContext context =
                new ClassPathXmlApplicationContext(ETL_SPRING_CONTEXT_FILENAME);
        EtlRunner runner = context.getBean(EtlRunner.class);
        runner.migrateDistrict(settings);
    }
    
}
