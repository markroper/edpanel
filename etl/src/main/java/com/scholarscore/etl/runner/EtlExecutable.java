package com.scholarscore.etl.runner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FilenameFilter;

/**
 * User: jordan
 * Date: 11/12/15
 * Time: 5:21 PM
 */
public class EtlExecutable {

    private final static Logger LOGGER = LoggerFactory.getLogger(EtlExecutable.class);


    public static final String ARGS_GPA_DIR = "d";  
    public static final String ARGS_GPA_DIR_LONG = "dir-path";

    public static final String ARGS_FILE_PREFIX = "p";
    public static final String ARGS_FILE_PREFIX_LONG = "prefix";

    private static final String ETL_SPRING_CONTEXT_FILENAME = "etl.xml";
    
    public static void main(String... args) throws ParseException {

        EtlSettings settings = parseSettings(args);
        loadSpringConfigAndLaunchETL(settings);
    }

    /**
     *
     * @param args
     * @return
     * @throws ParseException
     */
    private static EtlSettings parseSettings(String[] args) throws ParseException {

        Options options = new Options();
        //String opt, String longOpt, boolean hasArg, String description)
        options.addOption(new Option(ARGS_GPA_DIR, ARGS_GPA_DIR_LONG, true,
                "Specify the directory containing the GPA .csv files to load"));

        options.addOption(new Option(ARGS_FILE_PREFIX, ARGS_FILE_PREFIX_LONG, true,
                "Specify the filename prefix (e.g. 'gpa-' -> gpa-1.csv, gpa-2.csv)"));

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse( options, args);
        EtlSettings settings = new EtlSettings();

        if (cmd.hasOption(ARGS_GPA_DIR) && cmd.hasOption(ARGS_FILE_PREFIX)) {
            String fileNamePrefix = cmd.getOptionValue(ARGS_FILE_PREFIX);
            File dir = new File(cmd.getOptionValue(ARGS_GPA_DIR));
            File[] foundFiles = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith(new String(fileNamePrefix));
                }
            });

            if (foundFiles != null) {
                for (File file : foundFiles) {
                    settings.getGpaImportFiles().add(file);
                    LOGGER.info("Imported GPA file " + file);
                }
            } else {
                LOGGER.warn("WARN: --dir-path and --prefix set, but no files found");
            }
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
