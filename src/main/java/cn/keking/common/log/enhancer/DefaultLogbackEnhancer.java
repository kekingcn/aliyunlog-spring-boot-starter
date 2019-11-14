package cn.keking.common.log.enhancer;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.spi.FilterReply;
import cn.keking.common.log.appender.logback.LogbackAliYunAppender;
import cn.keking.common.log.config.LogAppenderConfig;
import cn.keking.common.log.utils.LogEnvUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.skywalking.apm.toolkit.log.logback.v1.x.TraceIdPatternLogbackLayout;
import org.slf4j.ILoggerFactory;
import org.slf4j.Marker;
import org.slf4j.impl.StaticLoggerBinder;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * Default log enhancer for logback
 *
 * @Author wanglaomo
 * @Date 2019/4/3
 **/
public class DefaultLogbackEnhancer extends AbstractLogEnhancer {

    private final ClassLoader classLoader;

    private static LoggerContext ctx;

    public DefaultLogbackEnhancer(ClassLoader classLoader) {
        this.classLoader = classLoader;
        ctx = getLoggerContext();
    }

    @Override
    public boolean alreadyBound() {

        Class clazz = LogEnvUtils.loadAliyunLogbackAppenderExist(classLoader);
        if(clazz == null) {
            return false;
        } else {
            List<Logger> loggerList = ctx.getLoggerList();
            for(Logger logger : loggerList) {
                Iterator<Appender<ILoggingEvent>> iterator = logger.iteratorForAppenders();
                while(iterator.hasNext()) {
                    Appender appender = iterator.next();
                    if (appender.getClass().isAssignableFrom(clazz)) {
                        return true;
                    }
                }
            }
            return false;
        }

    }

    @Override
    public void cleanUp() {

        markAsUnEnhanced();
    }

    @Override
    protected void doEnhance(LogAppenderConfig config) {

        LogbackAliYunAppender aliYunAppender = new LogbackAliYunAppender(config);
        aliYunAppender.setContext(ctx);

        // config pattern
        if(!StringUtils.isBlank(config.getPattern())) {
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setPattern(config.getPattern());
            encoder.setContext(ctx);
            encoder.setPatternLayout(new TraceIdPatternLogbackLayout());
            encoder.start();
            encoder.setCharset(Charset.forName(config.getCharset()));
            aliYunAppender.setEncoder(encoder);
        }

        aliYunAppender.start();
        List<String> loggerFilter = config.getLoggerFilter();

        ctx.addTurboFilter(new TurboFilter() {

            @Override
            public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {

                if (!logger.isAttached(aliYunAppender) && !loggerFilter.contains(logger.getName())) {

                    logger.addAppender(aliYunAppender);
                }
                return FilterReply.NEUTRAL;
            }
        });
    }


    protected void afterEnhance() {
        markAsEnhanced();
    }

    protected boolean hasBeanEnhanced() {
        return ctx.getObject(DefaultLogbackEnhancer.class.getCanonicalName()) != null;
    }

    private void markAsEnhanced() {

        ctx.putObject(DefaultLogbackEnhancer.class.getCanonicalName(), new Object());
    }

    private void markAsUnEnhanced() {

        ctx.removeObject(DefaultLogbackEnhancer.class.getCanonicalName());
    }

    private LoggerContext getLoggerContext() {
        ILoggerFactory factory = StaticLoggerBinder.getSingleton().getLoggerFactory();

        if(factory instanceof LoggerContext) {

            return (LoggerContext) factory;
        }
        throw new IllegalStateException("ILoggerFactory is not a Logback LoggerContext, but Logback is on the classpath.");
    }
}
