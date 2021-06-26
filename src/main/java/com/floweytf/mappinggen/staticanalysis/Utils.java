package com.floweytf.mappinggen.staticanalysis;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Utils {
    public static String modelToString(CtModel ctModel, CtType ctType) {
        HashSet<String> set = new HashSet<>();
        StringBuffer sb = new StringBuffer();
        DefaultJavaPrettyPrinter defaultJavaPrettyPrinter = new DefaultJavaPrettyPrinter(new Launcher().getEnvironment());
        for(CtType type : ctModel.getElements(new TypeFilter<>(CtType.class))){
            CtCompilationUnit u = ctType.getFactory().CompilationUnit().getOrCreate(type);
            if(set.contains(u.getDeclaredTypeReferences().get(0).getQualifiedName()))
                continue; // otherwise inner classes will cause duplicate compilation units in the buffer
            sb.append(defaultJavaPrettyPrinter.printCompilationUnit(u) + "\n\n");
            for(CtTypeReference reference : u.getDeclaredTypeReferences()){
                set.add(reference.getQualifiedName());
            }
        }
        return sb.toString();
    }

    public static <T extends CtReference> T remapRef(T ref,
                                                      String newName,
                                                      BiConsumer<T, CtPackageReference> setPackage
    ) {
        int i = newName.lastIndexOf('.');
        setPackage.accept(ref, new Launcher().getFactory().Package().createReference(newName.substring(0, i)));
        ref.setSimpleName(newName.substring(i+1));
        return ref;
    }

    private static final Map<String, String> JVM_TYPES = ImmutableMap.copyOf(
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

    public static String sourceToJVM(List<String> params) {
        StringBuilder b  = new StringBuilder("(");
        params.forEach(i -> {
            appendType(i, b);
        });
        b.append(")");
        return b.toString();
    }

    public static String toMappingsSig(String name, String className, List<String> paramName) {
        return className.replace(".", "/") + " " + name + " " + sourceToJVM(paramName);
    }
}
