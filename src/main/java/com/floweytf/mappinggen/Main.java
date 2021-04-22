package com.floweytf.mappinggen;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    private static Map<String, String> bukkit = new HashMap<>();

    public static String createClassMappings() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(
            "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/bukkit-1.16.5-cl.csrg?at=656df5e622bba97efb4e858e8cd3ec428a0b2d71"
        ).openStream()));
        String line = null;
        while ((line = reader.readLine()) != null) {
            if(line.startsWith("#"))
                continue;
            String[] names = line.split(" ");
            names[1] = "net/minecraft/server/" + names[1];
            bukkit.put(names[1], names[0]);
        }

        // get class names?
        reader = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream("joined.tsrg")));
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
        return builder.toString();
    }

    public static String createMembersMappings() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(
            "https://hub.spigotmc.org/stash/projects/SPIGOT/repos/builddata/raw/mappings/bukkit-1.16.5-members.csrg?at=656df5e622bba97efb4e858e8cd3ec428a0b2d71"
        ).openStream()));

        String line = null;

        Map<String, String> bukkitMethod = new HashMap<>();
        Map<String, String> bukkitField = new HashMap<>();

        while ((line = reader.readLine()) != null) {
            if(line.startsWith("#"))
                continue;
            String[] names = line.split(" ");
            // ChatModifier a (Ljava/lang/String;)LChatModifier; setInsertion
            names[0] = "net/minecraft/server/" + names[0];
            if(names.length == 3)
                bukkitField.put(bukkit.get(names[0]) + '/' + names[1], names[2]);
            else
                bukkitMethod.put(bukkit.get(names[0]) + '/' + names[1], names[3] + ' ' + names[2]);
        }
    }

    public static void main(String... args) throws IOException {
        File yourFile = new File("bukkit-to-mcp.srg");
        yourFile.createNewFile(); // if file already exists will do nothing
        FileOutputStream oFile = new FileOutputStream(yourFile, false);

        System.out.println("Creating mappings...");
        oFile.write(createClassMappings().getBytes());
    }
}
