package cn.keking.common.log.config;

import cn.keking.common.log.Constants;
import com.aliyun.openservices.aliyun.log.producer.ProducerConfig;
import com.aliyun.openservices.aliyun.log.producer.ProjectConfigs;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Load log appender config from environment
 *
 * @Author wanglaomo
 * @Date 2019/4/8
 **/
public class CommonLogAppenderConfigLoader implements LogAppenderConfigLoader {

    @Override
    public LogAppenderConfig load(ConfigurableEnvironment environment) throws InvocationTargetException, IllegalAccessException {

        LogAppenderConfig config = readConfigFromContext(environment);

        return config;
    }

    private LogAppenderConfig readConfigFromContext(ConfigurableEnvironment environment) throws InvocationTargetException, IllegalAccessException {

        Set<String> configKeys = new HashSet<>();
        Iterator<PropertySource<?>> propertySourceIterator = environment.getPropertySources().iterator();
        while (propertySourceIterator.hasNext()) {
            PropertySource propertySource = propertySourceIterator.next();
            if (propertySource instanceof EnumerablePropertySource) {
                configKeys.addAll(Arrays.asList(((EnumerablePropertySource) propertySource)
                        .getPropertyNames()));
            }
        }

        Map<String, String> context = new HashMap<>();
        Map<String, String> projectContext = new HashMap<>();
        Map<String, String> producerContext = new HashMap<>();
        for(String key : configKeys) {
            if(filterAllLogConfig(key)) {
                if(key.startsWith(Constants.LOG_PRODUCER_CONFIG_PREFIX)) {
                    producerContext.put(
                            parseConfigKey(Constants.LOG_PRODUCER_CONFIG_PREFIX, key),
                            environment.getProperty(key));
                } else if(key.startsWith(Constants.LOG_PROJECT_CONFIG_PREFIX)) {
                    projectContext.put(
                            parseConfigKey(Constants.LOG_PROJECT_CONFIG_PREFIX, key),
                            environment.getProperty(key));
                } else {
                    context.put(
                            parseConfigKey(Constants.LOG_CONFIG_PREFIX, key),
                            environment.getProperty(key));
                }
            }
        }

        LogAppenderConfig config = new LogAppenderConfig();
        BeanUtils.populate(config, context);
        String filterStr = context.get(Constants.LOG_CONFIG_LOGGER_FILTER);
        if(!StringUtils.isBlank(filterStr)) {
            List<String> loggerFilter = Arrays.asList(filterStr.split(","));
            config.getLoggerFilter().addAll(loggerFilter);
        }
        if(StringUtils.isBlank(config.getTopic())) {
            throw new IllegalStateException("Topic must not be null");
        }

        CommonProjectConfig commonProjectConfig = CommonProjectConfig.instance();
        BeanUtils.populate(commonProjectConfig, projectContext);

        ProjectConfigs projectConfigs = new ProjectConfigs();
        projectConfigs.put(commonProjectConfig.buildProjectConfig());
        ProducerConfig producerConfig = new ProducerConfig(projectConfigs);
        BeanUtils.populate(producerConfig, producerContext);

        config.setProducerConfig(producerConfig);
        config.setProjectConfig(commonProjectConfig);

        return config;
    }

    private String parseConfigKey(String prefix, String key) {
        return key.replace(prefix+".", "");
    }

    private boolean filterAllLogConfig(String key) {

        return key.startsWith(Constants.LOG_CONFIG_PREFIX);
    }

}
