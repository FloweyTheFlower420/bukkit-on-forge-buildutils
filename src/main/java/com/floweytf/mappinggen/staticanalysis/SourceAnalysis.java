package com.floweytf.mappinggen.staticanalysis;

import com.floweytf.utils.mappings.Mappings;
import spoon.Launcher;
import spoon.experimental.CtUnresolvedImport;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.util.ModelList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SourceAnalysis {
    @SuppressWarnings("rawtypes")
    public static void remapClass(CtClass ctClass, Mappings mappings) {
        CtModel ctModel = ctClass.getFactory().getModel();
        CtType ctType = ctModel.getRootPackage().getElements(new TypeFilter<>(CtType.class)).get(0);

        CtCompilationUnit unit = ctType.getFactory().CompilationUnit().getOrCreate(ctType);
        ModelList<CtImport> imports = unit.getImports();

        // rename class in import statement
        for(CtImport ctImport : imports) {
            CtUnresolvedImport imp = (CtUnresolvedImport) ctImport;
            if(mappings.mapClass(imp.getUnresolvedReference().replace(".", "/")) != null)
                imp.setUnresolvedReference(mappings.mapClass(imp.getUnresolvedReference().replace(".", "/")).replace("/", "."));
        }

        List<CtTypedElement> list = ctModel.getRootPackage().getElements(new TypeFilter<>(CtTypedElement.class));
        for(CtTypedElement e : list) {
            String name = e.getType().getQualifiedName();
            if(mappings.mapClass(name.replace(".", "/")) != null) {
                e.setType(Utils.remapRef(
                    e.getType(),
                    mappings.mapClass(e.getType().getQualifiedName().replace(".", "/")).replace("/", "."),
                    CtTypeReference::setPackage
                ));
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public static void remapMethod(CtClass ctClass, Mappings mappings) {
        CtModel ctModel = ctClass.getFactory().getModel();
        CtType ctType = ctModel.getRootPackage().getElements(new TypeFilter<>(CtType.class)).get(0);

        Map<String, String> ignoreReturnValueMappings = new ConcurrentHashMap<>();
        mappings.getMethodMappings().entrySet().stream().parallel().forEach(pair -> {
            ignoreReturnValueMappings.put(pair.getKey(), pair.getValue().substring(0, pair.getValue().lastIndexOf(")")));
        });

        List<CtInvocation> invocations = ctType.getElements(new TypeFilter<>(CtInvocation.class));
        for(CtInvocation i : invocations){
            String name = i.getExecutable().getSimpleName();
            List<CtTypeReference> parameters = i.getExecutable().getParameters();
            List<String> paramAsString = new ArrayList<>(parameters.size());
            parameters.forEach(e -> {
                paramAsString.add(e.getQualifiedName());
            });
            String className = i.getExecutable().getDeclaringType().getQualifiedName();

            String sig = Utils.toMappingsSig(name, className, paramAsString);
            // obtain mappings sig
            if(ignoreReturnValueMappings.containsKey(sig)) {
                i.getExecutable().setSimpleName(ignoreReturnValueMappings.get(sig).split(" ")[1]);
            }
        }
    }

    public static String remap(String source, Mappings mappings) {
        CtClass ctClass = Launcher.parseClass(source);
        remapClass(ctClass, mappings);
        remapMethod(ctClass, mappings);

        CtModel ctModel = ctClass.getFactory().getModel();
        CtType ctType = ctModel.getRootPackage().getElements(new TypeFilter<>(CtType.class)).get(0);
        return Utils.modelToString(ctModel, ctType);

    }
}
