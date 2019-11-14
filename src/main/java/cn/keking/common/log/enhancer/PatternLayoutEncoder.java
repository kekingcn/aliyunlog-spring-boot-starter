package cn.keking.common.log.enhancer;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;

/**
 * @Author wanglaomo
 * @Date 2019/4/12
 **/
public class PatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {

    private PatternLayout patternLayout;

    @Override
    public void start() {
        patternLayout.setContext(context);
        patternLayout.setPattern(getPattern());
        patternLayout.setOutputPatternAsHeader(outputPatternAsHeader);
        patternLayout.start();
        this.layout = patternLayout;
        super.start();
    }

    public void setPatternLayout(PatternLayout layout) {

        this.patternLayout = layout;
    }
}
