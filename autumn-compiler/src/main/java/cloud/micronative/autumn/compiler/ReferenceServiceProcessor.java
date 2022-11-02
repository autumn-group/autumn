package cloud.micronative.autumn.compiler;

import cloud.micronative.autumn.compiler.model.MethodElement;
import cloud.micronative.autumn.compiler.model.ReferenceProcessorEntry;
import cloud.micronative.autumn.compiler.util.ClassNameUtil;
import cloud.micronative.autumn.compiler.util.FreemarkerUtil;
import cloud.micronative.autumn.compiler.util.MetaHolder;
import cloud.micronative.autumn.core.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@SupportedAnnotationTypes(value = {"io.microwave.annotation.Reference"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
public class ReferenceServiceProcessor extends AbstractProcessor {
    private Logger log = LoggerFactory.getLogger(ReferenceServiceProcessor.class);
    private volatile AtomicBoolean executed = new AtomicBoolean(false);
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotationElement: annotations) {
            Set<? extends Element> annotatedClasses = roundEnv.getElementsAnnotatedWith(annotationElement);
            for(Element annotatedClass: annotatedClasses) {
                handleAnnotationClass(annotationElement, annotatedClass);
            }
        }
        if(!executed.getAndSet(true)) {
            handleWrite();
        }

        return true;
    }

    private void handleWrite() {
        ConcurrentHashMap<String, ReferenceProcessorEntry> referenceEntries =  MetaHolder.getReferService();
        referenceEntries.forEach((k, v) -> {
            try {
                String sourceName = "";
                int lastDot = k.lastIndexOf('.');
                if (lastDot > 0) {
                    sourceName = k.substring(0, lastDot);
                }
                String packageName = ClassNameUtil.getPackageName(sourceName);
                String simpleClassName = ClassNameUtil.getSimpleClassName(sourceName);
                JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(packageName + "._" + simpleClassName + "Proxy");
                FreemarkerUtil.handleProxy(k, v, builderFile.openWriter());
                JavaFileObject builderFactoryFile = processingEnv.getFiler().createSourceFile(packageName + "._" + simpleClassName + "PoolFactory");
                FreemarkerUtil.handlePoolFactory(k, v, builderFactoryFile.openWriter());
            } catch (Exception e) {
                log.warn("handleWrite Proxy Exception:", e);
            }
        });
    }

    private void handleAnnotationClass(TypeElement annotationElement, Element annotatedClass) {
        log.info("开始处理注解类Reference:{}", annotatedClass);
        if(!annotationElement.toString().equals("io.microwave.annotation.Reference")) {
            return;
        }
        Reference reference = annotatedClass.getAnnotation(Reference.class);
        ExecutableElement executableElement = (ExecutableElement) annotatedClass;

        DeclaredType interfaceClass = (DeclaredType)executableElement.getReturnType();
        List<? extends Element> elements = interfaceClass.asElement().getEnclosedElements();
        if(elements.isEmpty()) {
            log.info("处理Reference, 类名称:{}中没有任何方法", interfaceClass);
            return;
        }
        List<MethodElement> methodElements = new ArrayList<>();
        for(Element ele: elements) {
            MethodElement method = new MethodElement();
            ExecutableElement ee = (ExecutableElement)ele;
            method.setName(ee.getSimpleName().toString());
            TypeMirror returnType = ee.getReturnType();
            method.setReturnType(returnType.toString());
            methodElements.add(method);
            List<? extends VariableElement> ves = ee.getParameters();
            if(ves.isEmpty()) {
                continue;
            }
            List<String> params = new ArrayList<>();
            for(VariableElement ve: ves) {
                params.add(ve.asType().toString());
            }
            method.setParamTypes(params);
        }

        ReferenceProcessorEntry entry = new ReferenceProcessorEntry();
        entry.setInterfaceName(interfaceClass.toString());
        entry.setMethodElements(methodElements);
        log.info("entry:{}", JSON.toJSONString(entry));
        MetaHolder.addReferService(interfaceClass.toString(), entry);
    }


}
