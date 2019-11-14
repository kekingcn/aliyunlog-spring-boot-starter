package cn.keking.common.log.appender.log4j2;

import cn.keking.common.log.appender.AliYunAppenderCallback;
import cn.keking.common.log.config.LogAppenderConfig;
import com.aliyun.openservices.aliyun.log.producer.*;
import com.aliyun.openservices.aliyun.log.producer.errors.LogSizeTooLargeException;
import com.aliyun.openservices.aliyun.log.producer.errors.TimeoutException;
import com.aliyun.openservices.log.common.LogItem;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.util.Throwables;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cn.keking.common.log.LogAppenderAutoConfiguration.AUTH_USERNAME;

/**
 * Aliyun appender for log4j2
 *
 * @Author wanglaomo
 * @Date 2019/4/3
 **/
public class Log4j2AliYunAppender extends AbstractAppender {

    public static final String APPENDER_NAME = "ALI_YUN_APPENDER";

    private Producer producer;

    private final LogAppenderConfig config;

    protected DateTimeFormatter formatter;

    public Log4j2AliYunAppender(LogAppenderConfig config) {

        super(APPENDER_NAME, null, null);
        this.config = config;
    }


    public Log4j2AliYunAppender(LogAppenderConfig config, PatternLayout layout) {

        super(APPENDER_NAME, null, layout);
        this.config = config;
    }

    @Override
    public void start() {
        super.start();

        formatter = DateTimeFormat.forPattern(config.getTimeFormat()).withZone(DateTimeZone.forID(config.getTimeZone()));
        ProducerConfig producerConfig =  config.getProducerConfig();
        if (producerConfig == null) {
            ProjectConfigs projectConfigs = new ProjectConfigs();
            projectConfigs.put(config.getProjectConfig().buildProjectConfig());
            producerConfig = new ProducerConfig(projectConfigs);
        }
        producer = new LogProducer(producerConfig);
    }

    @Override
    public void stop() {
        super.stop();
        if (producer != null) {
            try {
                producer.close();
            } catch (Exception e) {
                LOGGER.error("Failed to close aliyun log producer: ", e);
            }
        }
    }

    @Override
    public void append(LogEvent event) {

        List<LogItem> logItems = new ArrayList<LogItem>();
        LogItem item = new LogItem();
        logItems.add(item);
        item.SetTime((int) (event.getTimeMillis() / 1000));
        DateTime dateTime = new DateTime(event.getTimeMillis());
        item.PushBack("time", dateTime.toString(formatter));
        item.PushBack("level", event.getLevel().toString());
        item.PushBack("thread", event.getThreadName());
        item.PushBack("username", MDC.get(AUTH_USERNAME));

        StackTraceElement source = event.getSource();
        if (source == null && (!event.isIncludeLocation())) {
            event.setIncludeLocation(true);
            source = event.getSource();
            event.setIncludeLocation(false);
        }

        item.PushBack("location", source == null ? "Unknown(Unknown Source)" : source.toString());

        String message = event.getMessage().getFormattedMessage();
        item.PushBack("message", message);

        String throwable = getThrowableStr(event.getThrown());
        if (throwable != null) {
            item.PushBack("throwable", throwable);
        }

        if (getLayout() != null) {
            item.PushBack("log", new String(getLayout().toByteArray(event)));
        }

        Map<String, String> properties = event.getContextMap();
        if (properties.size() > 0) {
            Object[] keys = properties.keySet().toArray();
            Arrays.sort(keys);
            for (int i = 0; i < keys.length; i++) {
                item.PushBack(keys[i].toString(), properties.get(keys[i].toString()));
            }
        }

        try {
            producer.send(
                    config.getProjectConfig().getName(),
                    config.getProjectConfig().getLogstore(),
                    config.getTopic(),
                    null,
                    logItems,
                    new AliYunAppenderCallback(config, logItems));
        } catch (InterruptedException e) {
            LOGGER.warn("The current thread has been interrupted during send logs.");
        } catch (Exception e) {
            if (e instanceof LogSizeTooLargeException) {
                LOGGER.error("The size of log is larger than the maximum allowable size, e={}", e);
            } else if (e instanceof TimeoutException) {
                LOGGER.error("The time taken for allocating memory for the logs has surpassed., e={}", e);
            } else {
                LOGGER.error("Failed to send log, logItems={}, e=", logItems, e);
            }
        }
    }

    private String getThrowableStr(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String s : Throwables.toStringList(throwable)) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(System.getProperty("line.separator"));
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public LogAppenderConfig getConfig() {
        return config;
    }


}
