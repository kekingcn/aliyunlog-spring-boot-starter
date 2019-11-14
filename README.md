# kk-aliyun-log-appender
`kk-aliyun-log-appender`能够自动感知应用环境内引入的的日志实现（Log4j2及Logback）并自动接入阿里云日志服务，在保留灵活配置的同时，大幅度简化开发配置，让应用适配日志服务更简单。

## 一、使用场景
1. 简化繁琐的阿里云日志服务接入的配置，`kk-aliyun-log-appender`已集成不同日志框架的阿里云日志服务接入实现，且能自动感知引入的日志实现，无需根据不同的日志实现去配置不同的接入逻辑。
2. 提供了默认配置，大多数场景下仅需要配置topic属性即可接入。
3. 提供了灵活的配置方案。
4. 接入了新版[Aliyun LOG Java Producer](https://github.com/aliyun/aliyun-log-java-producer)

   > Aliyun LOG Java Producer 是对老版 log-loghub-producer 的全面升级，解决了上一版存在的多个问题，包括网络异常情况下 CPU 占用率过高、关闭 producer 可能出现少量数据丢失等问题。另外，在容错方面也进行了加强，即使您存在误用，在资源、吞吐、隔离等方面都有较好的保证。
5. 对原有的log4j2.xml或logback.xml中的配置无影响

## 二、快速开始
### 2.1 springboot项目接入
1.添加maven依赖
```xml
<dependency>
    <groupId>com.github.kekingcn</groupId>
    <artifactId>aliyunlog-spring-boot-starter</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```
2.配置必备属性topic
<br>
topic用以标记一批日志,例如可以通过配置topic属性来区分不同的应用及应用profile(kk-call-center-prod)
```properties
aliyun.log.enable=true
aliyun.log.topic=test-topic
```
3.`kk-aliyun-log-appender`默认为所有的logger接入阿里云日志服务，且日志级别为logger上定义的日志级别。
<br/>
4.启动应用<br/>
如果接入成功，则会打印如下log
```
[main] INFO  cn.keking.common.log.LogAppenderAutoConfiguration - kk aliyun log appender has been successfully initialized
```
### 2.2 spring mvc项目接入
和spring boot相似，需要在spring-xx.xml配置中添加类扫描即可
```xml
<context:component-scan base-package="cn.keking.common.log"></context:component-scan>
```

## 三、配置参数

仅aliyun.log.topic为必填属性

| 参数 | 类型 | 默认值 | 说明 |
| :--- | :---: | :---: | --- |
| aliyun.log.enable | boolean | false | 是否上传至阿里云日志 |
| **aliyun.log.topic** | String | 无 | 日志主题，**必填** |
| aliyun.log.charset | String | UTF-8 | 输出到日志服务的字符集，默认是 UTF-8 |
| aliyun.log.timeZone | String | UTC | 输出到日志服务的时间的时区，默认是 UTC |
| aliyun.log.timeFormat | String | yyyy-MM-dd'T'HH:mmZ | 输出到日志服务的时间的格式 |
| aliyun.log.logger.filter | List | 空 | 日志logger过滤器 |
| aliyun.log.pattern | String | %d{HH:mm:ss.SSS}  \[%thread\] %-5level %logger{36} - %msg%n | 自定义上传日志pattern |
| aliyun.log.project.name | String | kk-log-proj | 日志服务的 project 名 |
| aliyun.log.project.logstore | String | apps-shanghai | 日志服务的 logstore 名 |
| aliyun.log.project.endpoint | String | cn-shanghai-intranet.log.aliyuncs.com | 日志服务的 HTTP 地址 |
| aliyun.log.project.accessKeyId | String | LTAIC***SO32Qy | 用户身份标识 |
| aliyun.log.project.accessKeySecret | String | csvZDMv******DiyaTaRzal | 用户身份标识 |
| aliyun.log.project.stsToken | String | null | 为RAM角色签发的STS Token来访问阿里云服务 |
| aliyun.log.project.userAgent | String | aliyun-log-java-producer | userAgent |
| aliyun.log.producer.totalSizeInBytes | int | 100 * 1024 * 1024 | 单个producer实例能缓存的日志大小上限，默认为100MB。 |
| aliyun.log.producer.maxBlockMs | long | 60*1000 | 如果producer可用空间不足，调用者在send方法上的最大阻塞时间，默认为60秒。如果超过这个时间后所需空间仍无法得到满足，send方法会抛出TimeoutException。如果将该值设为0，当所需空间无法得到满足时，send方法会立即抛出TimeoutException。如果您希望send方法一直阻塞直到所需空间得到满足，可将该值设为负数。 |
| aliyun.log.producer.ioThreadCount | int | availableProcessors | 执行日志发送任务的线程池大小，默认为可用处理器个数。 |
| aliyun.log.producer.batchSizeThresholdInBytes | int | 512 * 1024 | 当一个ProducerBatch中缓存的日志大小大于等于batchSizeThresholdInBytes时，该batch将被发送，默认为512KB，最大可设置成5MB。 |
| aliyun.log.producer.batchCountThreshold | int | 4096 | 当一个ProducerBatch中缓存的日志条数大于等于batchCountThreshold时，该batch将被发送，默认为4096，最大可设置成40960。 |
| aliyun.log.producer.lingerMs | int | 2000 | 一个ProducerBatch从创建到可发送的逗留时间，默认为2秒，最小可设置成100毫秒。 |
| aliyun.log.producer.retries | int | 10 | 如果某个ProducerBatch首次发送失败，能够对其重试的次数，默认为10次。如果retries小于等于0，该ProducerBatch首次发送失败后将直接进入失败队列。 |
| aliyun.log.producer.maxReservedAttempts | int | 11 | 每个ProducerBatch每次被尝试发送都对应着一个Attempt，此参数用来控制返回给用户的attempt个数，默认只保留最近的11次attempt信息。该参数越大能让您追溯更多的信息，但同时也会消耗更多的内存。 |
| aliyun.log.producer.baseRetryBackoffMs | long | 100 | 默认值 | 首次重试的退避时间，默认为100毫秒。Producer采样指数退避算法，第N次重试的计划等待时间为baseRetryBackoffMs*2^(N-1)。 |
| aliyun.log.producer.maxRetryBackoffMs | long | 50 * 1000 | 重试的最大退避时间，默认为50秒。 |
| aliyun.log.producer.adjustShardHash | boolean | true | 如果调用send方法时指定了shardHash，该参数用于控制是否需要对其进行调整，默认为true。 |
| aliyun.log.producer.buckets | int | 64 | 当且仅当adjustShardHash为true时，该参数才生效。此时，producer会自动将shardHash重新分组，分组数量为buckets。如果两条数据的shardHash不同，它们是无法合并到一起发送的，会降低producer吞吐量。将shardHash重新分组后，能让数据有更多地机会被批量发送。该参数的取值范围是[1,256]，且必须是2的整数次幂，默认为64。 |
参阅：[Aliyun LOG Java Producer](https://github.com/aliyun/aliyun-log-java-producer)

## 四、使用限制
1. jdk`1.8` 
2. Spring`4`以上
3. 需要统一日志编程接口至slf4j
4. 应用内的日志框架logback `1.2.3`版本以上或log4j2 `2.0.2`版本以上（与原阿里SDK一致）
5. 如果已接入阿里SDK，则本日志组件将不会起任何作用

## 五、常见场景
#### 5.1 开发环境不上传日志到阿里云
在application-dev.properties中配置关闭上传日志
```properties
aliyun.log.enable=false
```
该参数默认为false，在生产环境中应当设置enable=true
#### 5.2 自定义logger上传日志
例如在log4j2中有如下的配置
```xml
<Loggers>
    <Logger name="mylog1" level="trace" additivity="false">
        <AppenderRef ref="Console" />
    </Logger>
    <Logger name="mylog2" level="trace" additivity="false">
        <AppenderRef ref="Console" />
    </Logger>
    <Root level="error">
        <AppenderRef ref="Console" />
    </Root>
</Loggers>
```
如果想要达到只有自定义的**mylog1**的log上传至阿里云
可以在application.properties中配置
```properties
aliyun.log.logger.filter=ROOT,mylog2
```
#### 5.3 关闭日志发送失败重试
可以在application.properties中配置
```properties
aliyun.log.producer.reties=0
```
#### 5.4 配置上传日志pattern
可以在application.properties中配置
```properties
aliyun.log.pattern=%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n
```
格式参阅:
[log4j2](https://logging.apache.org/log4j/2.x/manual/layouts.html)
[logback](https://logback.qos.ch/manual/layouts.html)
#### 5.5 整合skywalking traceId
已集成skywalking toolkit，log4j2请使用\[%traceId]，logback请使用\[%tid]
```properties
aliyun.log.pattern=%d{HH:mm:ss.SSS} [%traceId] [%t] %-5level %logger{36} - %msg%n
```
#### 5.6 未接入授权网关，但是使用授权认证的怎么记录username？
有些系统使用了授权，但是没有对接授权网关，导致日志组件获取不到username属性，有三种方法可解决问题：
- 1、对接授权网关，使用网关统一鉴权，推荐这种方式
- 2、用户登录后，在请求中设置header参数username、或者userName，如username = kl 。目前已兼容参数名字userName和销管平台的token模式了
- 3、如果应用已经有类似的header参数传递，只是属性名字不是上面兼容的这些，为了不产生歧义，也可在后台统一filter里是用MDC.put("username",""kl")设值
