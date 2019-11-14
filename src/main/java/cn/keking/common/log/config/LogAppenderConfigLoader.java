package cn.keking.common.log.config;

import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.InvocationTargetException;

/**
 * Config loader
 *
 * @Author wanglaomo
 * @Date 2019/4/8
 **/
public interface LogAppenderConfigLoader {

    // 从spring environment获取配置
    LogAppenderConfig load(ConfigurableEnvironment environment) throws InvocationTargetException, IllegalAccessException;

}
