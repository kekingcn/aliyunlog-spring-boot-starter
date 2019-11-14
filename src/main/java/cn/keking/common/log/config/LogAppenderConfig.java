package cn.keking.common.log.config;

import com.aliyun.openservices.aliyun.log.producer.ProducerConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * 通用阿里云日志服务appender配置
 *
 * @Author wanglaomo
 * @Date 2019/4/4
 **/
public class LogAppenderConfig {

    private static final String DEFAULT_PATTERN = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";

    private CommonProjectConfig projectConfig;

    private ProducerConfig producerConfig;

    private boolean enable = false;

    private String topic; //

    private String source; //

    private String timeZone = "UTC";

    private String charset = "UTF-8";

    private String timeFormat = "yyyy-MM-dd'T'HH:mmZ";

    private List<String> loggerFilter = new ArrayList<>();

    private String pattern = DEFAULT_PATTERN;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public CommonProjectConfig getProjectConfig() {
        return projectConfig;
    }

    public void setProjectConfig(CommonProjectConfig projectConfig) {
        this.projectConfig = projectConfig;
    }

    public ProducerConfig getProducerConfig() {
        return producerConfig;
    }

    public void setProducerConfig(ProducerConfig producerConfig) {
        this.producerConfig = producerConfig;
    }

    public List<String> getLoggerFilter() {
        return loggerFilter;
    }

    public void setLoggerFilter(List<String> loggerFilter) {
        this.loggerFilter = loggerFilter;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getSource() {
        return source;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
