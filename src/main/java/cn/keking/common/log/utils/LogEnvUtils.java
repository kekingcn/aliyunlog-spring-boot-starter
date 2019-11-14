package cn.keking.common.log.utils;

public final class LogEnvUtils {

    private LogEnvUtils() {
    }

    public static boolean isLogbackUsable(ClassLoader classloader) {

        try {
            return classloader.loadClass("ch.qos.logback.classic.LoggerContext") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isLog4j2Usable(ClassLoader classloader) {

        try {
            return (classloader.loadClass("org.apache.logging.slf4j.Log4jLoggerFactory") != null);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static Class loadAliyunLog4j2AppenderExist(ClassLoader classloader) {

        try {
            return classloader.loadClass("com.aliyun.openservices.log.log4j2.LoghubAppender");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static Class loadAliyunLogbackAppenderExist(ClassLoader classloader) {

        try {
            return classloader.loadClass("com.aliyun.openservices.log.logback.LoghubAppender");
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
