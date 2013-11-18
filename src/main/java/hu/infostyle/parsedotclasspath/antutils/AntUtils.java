package hu.infostyle.parsedotclasspath.antutils;

public class AntUtils {
    //Common strings
    public static final String BUILD_COMMON_VALUE_ATTR = "value";



    public static final String BUILD_PROJECT_ELEMENT = "project";
    public static final String BUILD_PROJECT_NAME_ATTR = "name";
    public static final String BUILD_PROJECT_DEF_ATTR = "default";
    public static final String BUILD_PROJECT_BASEDIR_ATTR = "basedir";
    public static final String BUILD_PROPERTY_ELEMENT = "property";
    public static final String BUILD_PROPERTY_VALUE_ATTR = "value";
    public static final String BUILD_PROPERTY_DEBUGLEVEL = "debuglevel";
    public static final String BUILD_PROPERTY_DEBUGLEVEL_VALUE = "source,lines,vars";
    public static final String BUILD_PROPERTY_TARGET = "target";
    public static final String BUILD_PROPERTY_VALUE_CODETARGET = "1.6";
    public static final String BUILD_PROPERTY_SOURCE = "source";
    public static final String BUILD_PROPERTY_SOURCE_VALUE = "1.6";
    public static final String BUILD_PROPERTY_ENCODING = "encoding";
    public static final String BUILD_PROPERTY_ENCODING_TYPE = "UTF-8";
    public static final String BUILD_PATH_ELEMENT = "path";
    public static final String BUILD_PATH_ID_ATTR = "id";
    public static final String BUILD_PATHELEMENT = "pathelement";
    public static final String BUILD_PATH_PATHELEMENT_PATH_ATTR = "path";
    public static final String BUILD_TARGET_ELEMENT = "target";
    public static final String BUILD_TARGET_NAME_ATTR = "name";
    public static final String BUILD_TARGET_NAME_INIT = "init";
    public static final String BUILD_MKDIR_ELEMENT = "mkdir";
    public static final String BUILD_MKDIR_DIR_ATTR = "dir";
    public static final String BUILD_COPY_ELEMENT = "copy";
    public static final String BUILD_COPY_TODIR_ATTR = "todir";
    public static final String BUILD_COPY_INCLEMPTYDIRS = "includeemptydirs";
    public static final String BUILD_FILESET_ELEMENT = "fileset";
    public static final String BUILD_FILESET_DIR_ATTR = "dir";
    public static final String BUILD_EXCLUDE_ELEMENT = "exclude";
    public static final String BUILD_EXCLUDE_NAME_ATTR = "name";
    public static final String BUILD_TARGET_NAME_CLEAN = "clean";
    public static final String BUILD_DELETE_ELEMENT = "delete";
    public static final String BUILD_DELETE_DIR_ATTR = "dir";
    public static final String BUILD_TARGET_DEPENDS_ATTR = "depends";
    public static final String BUILD_TARGET_NAME_CLEANALL = "cleanall";
    public static final String BUILD_TARGET_NAME_BUILD = "build";
    public static final String BUILD_TARGET_NAME_BUILDPROJECT = "build-project";
    public static final String BUILD_TARGET_NAME_BUILDSUBPROJECTS = "build-subprojects";
    public static final String BUILD_ECHO_ELEMENT = "echo";
    public static final String BUILD_ECHO_MESSAGE_ATTR = "message";
    public static final String BUILD_ECHO_MESSAGE_VALUE = "${ant.project.name}: ${ant.file}";
    public static final String BUILD_JAVAC_ELEMENT = "javac";
    public static final String BUILD_JAVAC_DEBUG_ATTR = "debug";
    public static final String BUILD_JAVAC_DEBUGLEVEL_ATTR = "debuglevel";
    public static final String BUILD_JAVAC_DEBUGLEVEL_VALUE = "${debuglevel}";
    public static final String BUILD_JAVAC_DESTDIR_ATTR = "destdir";
    public static final String BUILD_JAVAC_SOURCE_ATTR = "source";
    public static final String BUILD_JAVAC_SOURCE_VALUE = "${source}";
    public static final String BUILD_JAVAC_TARGET_ATTR = "target";
    public static final String BUILD_JAVAC_TARGET_VALUE = "${target}";
    public static final String BUILD_JAVAC_ENCODING_ATTR = "encoding";
    public static final String BUILD_JAVAC_ENCODING_VALUE = "${encoding}";
    public static final String BUILD_SRC_ELEMENT = "src";
    public static final String BUILD_SRC_PATH_ATTR = "path";
    public static final String BUILD_CLASSPATH_ELEMENT = "classpath";
    public static final String BUILD_CLASSPATH_REFID_ATTR = "refid";



    //GWT strings
    public static final String BUILD_GWT_TARGET_NAME_VALUE = "gwt-compile";
    public static final String BUILD_GWT_JAVA_ELEMENT = "java";
    public static final String BUILD_GWT_JAVA_FAILONERROR_ATTR = "failonerror";
    public static final String BUILD_GWT_JAVA_FORK_ATTR = "fork";
    public static final String BUILD_GWT_JAVA_CLASSNAME_ATTR = "classname";
    public static final String BUILD_GWT_JAVA_CLASSNAME_VALUE = "com.google.gwt.dev.Compiler";
    public static final String BUILD_GWT_JAVA_JVMARG_ELEMENT = "jvmarg";
    public static final String BUILD_GWT_JAVA_ARG_ELEMENT = "arg";
    public static final String BUILD_GWT_JAVA_ARG_LINE_ATTR = "line";
    public static final String BUILD_GWT_JVMARG_MEMORY_VALUE = "-Xmx1024M";
    public static final String BUILD_GWT_WARARG_LINE = "-war";
    public static final String BUILD_GWT_WARARG_VALUE = "war";
    public static final String BUILD_GWT_GWTARGVARS_VALUE = "${gwt-args}";
}
