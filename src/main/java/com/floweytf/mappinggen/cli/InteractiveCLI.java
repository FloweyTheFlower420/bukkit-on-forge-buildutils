package com.floweytf.mappinggen.cli;

import com.floweytf.utils.mappings.Mappings;
import com.floweytf.utils.streams.InputStreamUtils;

import java.util.HashMap;
import java.util.Map;

public class InteractiveCLI {
    Map<String, Mappings> vars = new HashMap<>();
    public void run() {
        System.out.println("Mappings CLI | ");
        InputStreamUtils.getStream(System.in).forEach(line -> {

        });
    }

    private Mappings getVar(String s) {
        if(!s.matches("[a-zA-Z_]+"))
            throw new SyntaxException("Invalid name for var");
        return vars.get(s);
    }

    private Mappings setVar(Mappings v, String s) {
        if(!s.matches("[a-zA-Z_]+"))
            throw new SyntaxException("Invalid name for var");
        return vars.put(s, v);
    }

    private Mappings eval(String str) {
        str = str.trim();
        if(str.contains("=")) {
            String[] parts = str.split("=", 1);
            return setVar(eval(parts[1]), parts[0]);
        }
        else if(str.contains("(") && str.endsWith(")") && str.substring(0, str.indexOf("(")).matches("[a-zA-Z_]+")) {
            // this is an method!
            String[] parts = str.split("\\(", 1);

        }
        if(!str.matches("[a-zA-Z_]+")) {
            //return getVar(s);
        }

        return null;
    }
}
