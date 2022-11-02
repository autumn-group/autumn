package cloud.micronative.autumn.compiler.util;

import cloud.micronative.autumn.compiler.model.ReferenceProcessorEntry;

import cloud.micronative.autumn.compiler.model.ServerProcessorEntry;
import lombok.extern.slf4j.Slf4j;

import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MetaHolder {
    private static Integer serverPort = 8761;
    private static ConcurrentHashMap<String, List<? extends TypeMirror>> exportServices = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, ReferenceProcessorEntry> referenceServices = new ConcurrentHashMap<>();

    private MetaHolder() {

    }

    public static void setServerPort(Integer _serverPort) {
        serverPort = _serverPort;
    }

    public static Integer getServerPort() {
        return serverPort;
    }

    public static void addReferService(String referService, ReferenceProcessorEntry referServiceEntry) {
        referenceServices.put(referService, referServiceEntry);
    }


    public static ConcurrentHashMap<String, ReferenceProcessorEntry> getReferService() {
        return referenceServices;
    }

    public static void addExportService(String exportService, List<? extends TypeMirror> types) {
        exportServices.put(exportService, types);
    }

    public static String getThriftInterfaceName(String exportService) {
        List<?> types = exportServices.get(exportService);
        if(null == types || types.size() < 1) {
            return null;
        }
        String interfaceNameIface = types.stream()
                .map(String::valueOf)
                .map(String::trim)
                .filter(it -> it.contains("Iface"))
                .findFirst()
                .get();
        int lastDot = interfaceNameIface.lastIndexOf('.');
        String interfaceName = interfaceNameIface.substring(0, lastDot);
        return interfaceName;
    }

    public static List<ServerProcessorEntry> getConverterEntries() {
        List<ServerProcessorEntry> entries = new ArrayList<>();
        if(exportServices.isEmpty()) {
            return entries;
        }
        exportServices.forEach((k, v) -> {
            ServerProcessorEntry entry = new ServerProcessorEntry();
            entry.setImplClassName(k);
            String interfaceName = getThriftInterfaceName(k);
            entry.setInterfaceName(interfaceName);
            String simpleClassName = ClassNameUtil.getSimpleClassName(k);
            String paramName = ClassNameUtil.getParamName(simpleClassName);
            entry.setParamName(paramName + "_");
            entry.setRegisterName(interfaceName + "$");
            entries.add(entry);
        });
        return entries;
    }


}
