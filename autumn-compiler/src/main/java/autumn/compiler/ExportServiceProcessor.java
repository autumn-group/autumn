package autumn.compiler;

import autumn.compiler.util.MetaHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes(value = {"autumn.core.annotation.Export"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_11)
public class ExportServiceProcessor extends AbstractProcessor {
    private Logger log = LoggerFactory.getLogger(ExportServiceProcessor.class);
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotationElement: annotations) {
            Set<? extends Element> annotatedClasses = roundEnv.getElementsAnnotatedWith(annotationElement);
            for(Element annotatedClass: annotatedClasses) {
                handleAnnotationClass(annotationElement, annotatedClass);
            }
        }
        return true;
    }

    private void handleAnnotationClass(TypeElement annotationElement, Element annotatedClass) {
        log.info("begin handle Export:{}", annotatedClass);
        if(!annotationElement.toString().equals("autumn.core.annotation.Export")) {
            return;
        }
        TypeElement te = (TypeElement) annotatedClass;
        List<? extends TypeMirror> interfaces = te.getInterfaces();
        MetaHolder.addExportService(annotatedClass.toString(), interfaces);
    }


}
