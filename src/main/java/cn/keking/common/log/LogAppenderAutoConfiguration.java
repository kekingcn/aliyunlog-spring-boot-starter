package cn.keking.common.log;
import cn.keking.common.log.config.CommonLogAppenderConfigLoader;
import cn.keking.common.log.config.LogAppenderConfig;
import cn.keking.common.log.config.LogAppenderConfigLoader;
import cn.keking.common.log.enhancer.LogEnhancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author wanglaomo
 * @Date 2019/4/9
 **/
@Configuration
public class LogAppenderAutoConfiguration implements EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(LogAppenderAutoConfiguration.class);
    public static final String AUTH_USERNAME = "username";
    /**
     * 未接入授权网关的变种的header参数
     */
    private static final String AUTH_USERNAME_VARIANT = "userName";
    /**
     * 销售平台用户认证header参数
     */
    private static final String AUTH_USERNAME_SALES = "token";
    /**
     * 销售平台tokenUsername分隔符
     */
    private static final String AUTH_USERNAME_SALES_SPLIT = "-";

    /**
     * 销售平台tokenUsername分隔后数组的长度
     */
    private static final Integer AUTH_USERNAME_SALES_SPLIT_LENGTH = 2;
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                String username = request.getHeader(AUTH_USERNAME);
                if(StringUtils.isEmpty(username)){
                    username = request.getHeader(AUTH_USERNAME_VARIANT);
                }
                /*
                   销售平台认证的header详情：token:baa469d88b0702914df31046b5e1815f-chenkailing
                 */
                if(StringUtils.isEmpty(username)){
                    String tokenUsername =request.getHeader(AUTH_USERNAME_SALES);
                    if(!StringUtils.isEmpty(tokenUsername) &&  tokenUsername.split(AUTH_USERNAME_SALES_SPLIT).length == AUTH_USERNAME_SALES_SPLIT_LENGTH){
                        username = tokenUsername.split(AUTH_USERNAME_SALES_SPLIT)[1];
                    }
                }
                MDC.put(AUTH_USERNAME,username);
                filterChain.doFilter(request, response);
            }
        });
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Override
    public void setEnvironment(Environment environment) {

        logger.info("kk aliyun log appender has been integrated into system");

        ConfigurableEnvironment env = (ConfigurableEnvironment)environment;

        // load config
        LogAppenderConfigLoader loader = new CommonLogAppenderConfigLoader();
        final LogAppenderConfig config;
        try {
            config = loader.load(env);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load log appender config", e);
        }

        if(!config.isEnable()) {
            return;
        }

        // init logEnhancer
        ClassLoader classLoader = env.getClass().getClassLoader();
        LogEnhancerBinder.bindClassLoader(classLoader);
        LogEnhancer logEnhancer = LogEnhancerBinder.getInstance();

        // do nothing if aliyun appender has been bound
        if(!logEnhancer.alreadyBound()) {

            logEnhancer.enhance(config);
            logger.info("kk aliyun log appender has been successfully initialized");
        }
    }


}
