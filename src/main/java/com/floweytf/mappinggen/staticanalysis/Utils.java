package com.floweytf.mappinggen.staticanalysis;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtCompilationUnit;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.HashSet;
import java.util.function.BiConsumer;

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
}
