package cn.keking.common.log.enhancer;

import cn.keking.common.log.config.LogAppenderConfig;

/**
 * @Author wanglaomo
 * @Date 2019/4/3
 **/
public interface LogEnhancer {

    // 判断是否已经绑定阿里云SDK
    boolean alreadyBound();

    // 接入阿里云日志服务
    void enhance(LogAppenderConfig config);

    void cleanUp();
}
