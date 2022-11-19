package autumn.compiler.util;

import autumn.compiler.model.ExportEntry;
import freemarker.core.ParseException;
import freemarker.template.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class FreemarkerUtil {

    public static void handleServer(String className, Writer writer) {
        String packageName = ClassNameUtil.getPackageName(className);
        String simpleClassName = ClassNameUtil.getSimpleClassName(className);
        //Resource resource = new ClassPathResource("template/");
        try {
            // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
            // 第二步：设置模板文件所在的路径。
            configuration.setClassForTemplateLoading(FreemarkerUtil.class, "/templates");
            // 第三步：设置模板文件使用的字符集。一般就是utf-8.
            configuration.setDefaultEncoding("utf-8");
            // 第四步：加载一个模板，创建一个模板对象。
            Template template = configuration.getTemplate("export.ftl");

            // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。
            Map<String, Object> root = new HashMap<>();
            // 向数据集中添加数据
            root.put("packageName", packageName);
            root.put("simpleClassName", simpleClassName);
            List<ExportEntry> entries = MetaHolder.getConverterEntries();
            root.put("entries", entries);
            root.put("serverPort", "8761");
            // 第六步：创建一个Writer对象，一般创建一FileWriter对象，指定生成的文件名。
            // 第七步：调用模板对象的process方法输出文件。
            Writer out = new StringWriter();
            if(null != writer) {
                template.process(root, writer);
            }
            template.process(root, out);
            // 第八步：关闭流。
            out.close();
            writer.flush();
            writer.close();
        } catch (TemplateException e) {
            log.warn("FreemarkerUtil TemplateException:{}", ExceptionUtils.getStackTrace(e));
        } catch (TemplateNotFoundException e) {
            log.warn("FreemarkerUtil TemplateNotFoundException:{}", ExceptionUtils.getStackTrace(e));
        } catch (ParseException e) {
            e.printStackTrace();
            log.warn("FreemarkerUtil ParseException:{}", ExceptionUtils.getStackTrace(e));
        } catch (MalformedTemplateNameException e) {
            log.warn("FreemarkerUtil MalformedTemplateNameException:{}", ExceptionUtils.getStackTrace(e));
        } catch (IOException e) {
            log.warn("FreemarkerUtil IOException:{}", ExceptionUtils.getStackTrace(e));
        }

    }
//
//    public static void handleProxy(String className, ReferenceProcessorEntry entry, Writer writer) {
//        int lastDot = className.lastIndexOf('.');
//        if (lastDot > 0) {
//            className = className.substring(0, lastDot);
//        }
//        String packageName = ClassNameUtil.getPackageName(className);
//        String simpleClassName = ClassNameUtil.getSimpleClassName(className);
//        try {
//            // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
//            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
//            // 第二步：设置模板文件所在的路径。
//            configuration.setClassForTemplateLoading(FreemarkerUtil.class, "/templates");
//            // 第三步：设置模板文件使用的字符集。一般就是utf-8.
//            configuration.setDefaultEncoding("utf-8");
//            // 第四步：加载一个模板，创建一个模板对象。
//            Template template = configuration.getTemplate("reference.ftl");
//
//            // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。
//            Map<String, Object> root = new HashMap<>();
//            // 向数据集中添加数据
//            root.put("packageName", packageName);
//            root.put("simpleClassName", simpleClassName);
//            root.put("className", className);
//            root.put("entry", entry);
//            root.put("methodElements", entry.getMethodElements());
//            // 第六步：创建一个Writer对象，一般创建一FileWriter对象，指定生成的文件名。
//            // 第七步：调用模板对象的process方法输出文件。
//            Writer out = new StringWriter();
//            if(null != writer) {
//                template.process(root, writer);
//            }
//            template.process(root, out);
//            // 第八步：关闭流。
//            out.close();
//            writer.flush();
//            writer.close();
//        } catch (TemplateException e) {
//            log.warn("FreemarkerUtil TemplateException:{}", ExceptionUtils.getStackTrace(e));
//        } catch (TemplateNotFoundException e) {
//            log.warn("FreemarkerUtil TemplateNotFoundException:{}", ExceptionUtils.getStackTrace(e));
//        } catch (ParseException e) {
//            e.printStackTrace();
//            log.warn("FreemarkerUtil ParseException:{}", ExceptionUtils.getStackTrace(e));
//        } catch (MalformedTemplateNameException e) {
//            log.warn("FreemarkerUtil MalformedTemplateNameException:{}", ExceptionUtils.getStackTrace(e));
//        } catch (IOException e) {
//            log.warn("FreemarkerUtil IOException:{}", ExceptionUtils.getStackTrace(e));
//        }
//
//    }
//
//    public static void handlePoolFactory(String className, ReferenceProcessorEntry entry, Writer writer) {
//        int lastDot = className.lastIndexOf('.');
//        if (lastDot > 0) {
//            className = className.substring(0, lastDot);
//        }
//        String packageName = ClassNameUtil.getPackageName(className);
//        String simpleClassName = ClassNameUtil.getSimpleClassName(className);
//        try {
//            // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
//            Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
//            // 第二步：设置模板文件所在的路径。
//            configuration.setClassForTemplateLoading(FreemarkerUtil.class, "/templates");
//            // 第三步：设置模板文件使用的字符集。一般就是utf-8.
//            configuration.setDefaultEncoding("utf-8");
//            // 第四步：加载一个模板，创建一个模板对象。
//            Template template = configuration.getTemplate("reference-pool-factory.ftl");
//
//            // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。
//            Map<String, Object> root = new HashMap<>();
//            // 向数据集中添加数据
//            root.put("packageName", packageName);
//            root.put("simpleClassName", simpleClassName);
//            root.put("className", className);
//            root.put("entry", entry);
//            // 第六步：创建一个Writer对象，一般创建一FileWriter对象，指定生成的文件名。
//            // 第七步：调用模板对象的process方法输出文件。
//            Writer out = new StringWriter();
//            if(null != writer) {
//                template.process(root, writer);
//            }
//            template.process(root, out);
//            // 第八步：关闭流。
//            out.close();
//            writer.flush();
//            writer.close();
//        } catch (TemplateException e) {
//            log.warn("FreemarkerUtil TemplateException:{}", ExceptionUtils.getStackTrace(e));
//        } catch (TemplateNotFoundException e) {
//            log.warn("FreemarkerUtil TemplateNotFoundException:{}", ExceptionUtils.getStackTrace(e));
//        } catch (ParseException e) {
//            e.printStackTrace();
//            log.warn("FreemarkerUtil ParseException:{}", ExceptionUtils.getStackTrace(e));
//        } catch (MalformedTemplateNameException e) {
//            log.warn("FreemarkerUtil MalformedTemplateNameException:{}", ExceptionUtils.getStackTrace(e));
//        } catch (IOException e) {
//            log.warn("FreemarkerUtil IOException:{}", ExceptionUtils.getStackTrace(e));
//        }
//
//    }

}
