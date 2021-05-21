package com.floweytf.mappinggen.staticanalysis;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodSignature {
    List<String> params;
    String returnValue;

    private static Map<String, String> JVM_TYPES = ImmutableMap.copyOf(
        Stream.of(
            new AbstractMap.SimpleEntry<>("boolean","Z"),
            new AbstractMap.SimpleEntry<>("byte", "B"),
            new AbstractMap.SimpleEntry<>("char", "C"),
            new AbstractMap.SimpleEntry<>("short", "S"),
            new AbstractMap.SimpleEntry<>("int", "I"),
            new AbstractMap.SimpleEntry<>("long", "J"),
            new AbstractMap.SimpleEntry<>("float", "F"),
            new AbstractMap.SimpleEntry<>("double", "D")
        ).collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))
    );


    private static void appendType(String str, StringBuilder b) {
        int c = StringUtils.countMatches(str, "[]");
        str = str .replace("[]", "");
        if(JVM_TYPES.containsKey(str))
            str = JVM_TYPES.get(str);
        else
            str = "L" + str .replace(".", "/") + ";";

        b.append(StringUtils.repeat('[', c)).append(str);
    }

    public static String sourceToJVM(MethodSignature source) {
        StringBuilder b  = new StringBuilder("(");
        source.params.forEach(i -> {
            appendType(i, b);
        });
        b.append(")");
        appendType(source.returnValue, b);
        return b.toString();
    }
}
