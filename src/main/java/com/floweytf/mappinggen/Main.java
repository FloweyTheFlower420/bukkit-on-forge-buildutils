package com.floweytf.mappinggen;

import com.floweytf.mappinggen.staticanalysis.SourceAnalysis;
import com.floweytf.utils.mappings.Mappings;
import com.floweytf.utils.mappings.MappingsFactory;
import com.floweytf.utils.streams.InputStreamUtils;
import com.floweytf.utils.streams.OutputStreamUtils;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;

public class Main {
    public static void main(String... args) throws IOException, ClassNotFoundException {
        CommandLine cli = CLIParser.parse(args);
        if(cli.hasOption('g')) {
            Mappings m = ParseJsonConfig.parse(cli.getOptionValue('g'));
            MappingsFactory.writeSrg(m, OutputStreamUtils.getStream(System.out));
        }

        Mappings mappings = null;

        System.out.println(SourceAnalysis.remapClass(InputStreamUtils.getStreamFile("test.java").toString(), MappingsFactory.readCsrg(InputStreamUtils.getStreamClassPath("/testing/test.csrg"))));
        System.out.println("hi");
    }
}
