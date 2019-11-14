package cn.keking.common.log.config;

import com.aliyun.openservices.aliyun.log.producer.ProjectConfig;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @Author wanglaomo
 * @Date 2019/4/8
 **/
public class CommonProjectConfig {

    private static final String CONFIG_FILENAME = "aliyunlog.properties";

    private static final String DEFAULT_LOGSTORE = "logstore";

    private static final String DEFAULT_PROJECT_NAME = "name";

    private static final String DEFAULT_ENDPOINT = "endpoint";

    private static final String DEFAULT_ACCESSKEY_ID = "accessKeyId";

    private static final String DEFAULT_ACCESSKEY_SECRET = "accessKeySecret";

    private static final String DEFAULT_STS_TOKEN = "stsToken";

    private static final String DEFAULT_USER_AGENT = "userAgent";

    private String name;

    private String logstore;

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String stsToken;

    private String userAgent;

    private final static CommonProjectConfig COMMON_PROJECT_CONFIG = new CommonProjectConfig();

    private CommonProjectConfig() {
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties(CONFIG_FILENAME, CommonProjectConfig.class.getClassLoader());
            this.name = properties.getProperty(DEFAULT_PROJECT_NAME);
            this.logstore = properties.getProperty(DEFAULT_LOGSTORE);
            this.endpoint = properties.getProperty(DEFAULT_ENDPOINT);
            this.accessKeyId = properties.getProperty(DEFAULT_ACCESSKEY_ID);
            this.accessKeySecret = properties.getProperty(DEFAULT_ACCESSKEY_SECRET);
            this.stsToken = properties.getProperty(DEFAULT_STS_TOKEN);
            this.userAgent = properties.getProperty(DEFAULT_USER_AGENT);
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public static CommonProjectConfig instance(){
        return COMMON_PROJECT_CONFIG;
    }

    public ProjectConfig buildProjectConfig() {

        return new ProjectConfig(name, endpoint, accessKeyId, accessKeySecret, stsToken, userAgent);
    }

    public String getLogstore() {
        return logstore;
    }

    public void setLogstore(String logstore) {
        this.logstore = logstore;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getStsToken() {
        return stsToken;
    }

    public void setStsToken(String stsToken) {
        this.stsToken = stsToken;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
