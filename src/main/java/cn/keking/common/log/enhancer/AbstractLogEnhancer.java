package cn.keking.common.log.enhancer;

import cn.keking.common.log.config.LogAppenderConfig;

/**
 * Abstract log enhancer
 *
 * @Author wanglaomo
 * @Date 2019/4/4
 **/
public abstract class AbstractLogEnhancer implements LogEnhancer {

    @Override
    public void enhance(LogAppenderConfig config) {

        try {
            if(!hasBeanEnhanced()) {
                doEnhance(config);
            }
        } catch (Exception e) {
            handlerEnhanceError(e);
        } finally {
            afterEnhance();
        }
    }

    protected abstract boolean hasBeanEnhanced();

    protected abstract void doEnhance(LogAppenderConfig config);

    protected abstract void afterEnhance();

    protected void handlerEnhanceError(Exception exception) {

        cleanUp();
        System.err.println("Failed to add aliyun appender");
        exception.printStackTrace(System.err);
        throw new IllegalStateException(exception);
    }
}
