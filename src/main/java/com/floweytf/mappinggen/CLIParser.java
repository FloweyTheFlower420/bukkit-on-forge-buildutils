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

        Option r1 = new Option("g", "generate-mappings", true, "generates mappings based of off config");
        r1.setRequired(false);

        Option out = new Option("o", "output", true, "where to output the file");
        r1.setRequired(true);

        Option r2 = new Option("a", "apply", true, "applies a mapping");
        r2.setRequired(false);

        Option r3 = new Option("i", "interactive", false, "interactive CLI to generate/apply mappings");
        r2.setRequired(false);

        options.addOption(r1);
        options.addOption(r2);
        options.addOption(r3);
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
