package cloud.micronative.autumn.compiler;

import cloud.micronative.autumn.compiler.util.MetaHolder;
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

@SupportedAnnotationTypes(value = {"io.microwave.annotation.Export"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
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
        log.info("开始处理注解类Export:{}", annotatedClass);
        if(!annotationElement.toString().equals("io.microwave.annotation.Export")) {
            return;
        }
        TypeElement te = (TypeElement) annotatedClass;
        List<? extends TypeMirror> interfaces = te.getInterfaces();
        MetaHolder.addExportService(annotatedClass.toString(), interfaces);
    }


}
