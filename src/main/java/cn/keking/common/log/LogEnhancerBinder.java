package cn.keking.common.log;

import cn.keking.common.log.enhancer.DefaultLog4j2Enhancer;
import cn.keking.common.log.enhancer.DefaultLogbackEnhancer;
import cn.keking.common.log.enhancer.LogEnhancer;
import cn.keking.common.log.utils.LogEnvUtils;

/**
 * @Author wanglaomo
 * @Date 2019/4/3
 **/
public class LogEnhancerBinder {

    private volatile static LogEnhancer INSTANCE;

    private static ClassLoader classLoader = ClassLoader.getSystemClassLoader();


    public static void bindClassLoader(ClassLoader classLoader) {

        if(INSTANCE == null) {

            synchronized (LogEnhancerBinder.class) {

                if(INSTANCE == null) {
                    LogEnhancerBinder.classLoader = classLoader;
                }
            }
        }
    }

    public static LogEnhancer getInstance() {

        if(INSTANCE == null) {

            synchronized (LogEnhancerBinder.class) {

                if(INSTANCE == null) {

                    INSTANCE = doInit(classLoader);
                }

            }
        }

        return INSTANCE;
    }

    private static LogEnhancer doInit(ClassLoader classLoader) {

        LogEnhancer enhancer = null;
        if(LogEnvUtils.isLog4j2Usable(classLoader)) {
            enhancer = new DefaultLog4j2Enhancer(classLoader);
        } else if(LogEnvUtils.isLogbackUsable(classLoader)) {
            enhancer = new DefaultLogbackEnhancer(classLoader);
        } else {
            throw new IllegalStateException("No applicable logging system found");
        }

        return enhancer;
    }

}
