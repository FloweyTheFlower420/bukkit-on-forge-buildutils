package com.floweytf.mappinggen;

import com.floweytf.utils.mappings.Mappings;
import com.floweytf.utils.mappings.MappingsFactory;
import com.floweytf.utils.streams.OutputStreamUtils;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;

public class Main {
    public static void main(String... args) throws IOException {
        CommandLine cli = CLIParser.parse(args);
        if(cli.hasOption('g')) {
            Mappings m = ParseJsonConfig.parse(cli.getOptionValue('g'));
            MappingsFactory.writeSrg(m, OutputStreamUtils.getStreamFile(cli.getOptionValue('o')));
        }
    }
}
