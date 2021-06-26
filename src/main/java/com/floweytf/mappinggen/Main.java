package com.floweytf.mappinggen;

import com.floweytf.utils.mappings.Mappings;
import com.floweytf.utils.mappings.MappingsFactory;
import com.floweytf.utils.streams.InputStreamUtils;
import com.floweytf.utils.streams.OutputStreamUtils;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Main {
    public static void main(String... args) throws IOException, ClassNotFoundException {
        CommandLine cli = CLIParser.parse(args);
        if(cli.hasOption('g')) {
            String version = cli.getOptionValue('g');
            InputStreamUtils stream = InputStreamUtils.getStreamClassPath("/" + version + "/bukkit-to-mcp.srg");
            if(stream.toStream() == null) {
                Mappings bukkit = MappingsFactory.readCsrg(InputStreamUtils.getStreamClassPath("/" + version + "/bukkit.csrg")).reverse();
                Mappings srg = MappingsFactory.readTsrg(InputStreamUtils.getStreamClassPath("/" + version + "/srg.tsrg"));
                Mappings mcp = MappingsFactory.applyMCP(
                    srg,
                    InputStreamUtils.getStreamClassPath("/" + version + "/methods.csv"),
                    InputStreamUtils.getStreamClassPath("/" + version + "/fields.csv")
                );
                MappingsFactory.writeSrg(bukkit.merge(mcp), OutputStreamUtils.getStreamFile(cli.getOptionValue('o')));
            }
            else {
                Files.copy(
                    stream.toStream(),
                    Paths.get(cli.getOptionValue('o')),
                    StandardCopyOption.REPLACE_EXISTING
                );
            }
        }

    }
}
