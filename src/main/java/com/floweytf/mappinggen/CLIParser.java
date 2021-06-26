package com.floweytf.mappinggen;

import org.apache.commons.cli.*;

public class CLIParser {
    /**
     * parses command line arguments, args are:
     *      -g, --generate-mappings: creates the mappings, from an config file
     * @param args program arguments
     * @return parsed commandline args
     */
    public static CommandLine parse(String... args) {
        // parse cli arguments
        Options options = new Options();

        Option gen = new Option("g", "generate-mappings", true, "generates mappings based of off config");
        gen.setRequired(true);

        Option out = new Option("o", "output", true, "where to output the file");
        out.setRequired(true);

        options.addOption(gen);
        options.addOption(out);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("mapping-gen", options);
            System.exit(1);
        }

        return cmd;
    }
}
