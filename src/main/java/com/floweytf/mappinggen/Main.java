package com.floweytf.mappinggen;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static String createClassMappings() throws IOException {
        Map<String, String> bukkit = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(
            "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/bukkit-1.16.5-cl.csrg?at=refs%2Fheads%2Fmaster"
        ).openStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if(line.startsWith("#"))
                continue;
            String[] names = line.split(" ");
            names[1] = "/net/minecraft/server/" + names[1];
            bukkit.put(names[1], names[0]);
        }

        // get class names?
        reader = new BufferedReader(new InputStreamReader(Main.class.getClass().getClassLoader().getResourceAsStream("joined.tsrg")));
        Map<String, String> mcp = new HashMap<>();

        line = null;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("\t")) {
                String[] names = line.split(" ");
                mcp.put(names[0], names[1]);
            }
        }

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> element : bukkit.entrySet())
            builder.append("CL: ").append(element.getKey()).append(' ').append(mcp.get(element.getValue())).append('\n');
        System.out.println(builder.toString());
        return builder.toString();
    }

    public static void main(String... args) throws IOException {

    }
}
