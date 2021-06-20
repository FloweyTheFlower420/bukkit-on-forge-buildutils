package com.floweytf.mappinggen.staticanalysis;

import com.floweytf.utils.mappings.Mappings;
import spoon.Launcher;
import spoon.experimental.CtUnresolvedImport;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.util.ModelList;

import java.util.List;

public class SourceAnalysis {
    @SuppressWarnings("rawtypes")
    public static String remapClass(String source, Mappings mappings) {
        CtClass ctClass = Launcher.parseClass(source);
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
        return Utils.modelToString(ctModel, ctType);
    }
}
