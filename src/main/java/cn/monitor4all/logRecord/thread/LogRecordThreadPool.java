package cn.monitor4all.logRecord.thread;

import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

@Slf4j
@Component
//当value所对应配置文件中的值为false时，注入不生效，不为fasle注入生效;
//只有当value 或 name 对应的值与havingValue的值相同时，注入生效,该属性为true时，配置文件中缺少对应的value或name的对应的属性值，也会注入成功
@ConditionalOnProperty(name = "log-record.thread-pool.enabled", havingValue = "true", matchIfMissing = true)
//把使用 @ConfigurationProperties 的类进行了一次注入
@EnableConfigurationProperties({LogRecordProperties.class})
public class LogRecordThreadPool {

    //线程池中ThreadFactory用于设置创建线程工厂，通过线程工厂给每个创建出来的线程设置更有意义的名字
    private static final ThreadFactory THREAD_FACTORY = new CustomizableThreadFactory("log-record-");

    private final ExecutorService LOG_RECORD_POOL_EXECUTOR;

    public LogRecordThreadPool(LogRecordProperties logRecordProperties) {
        log.info("LogRecordThreadPool init poolSize [{}]", logRecordProperties.getThreadPool().getPoolSize());
        int poolSize = logRecordProperties.getThreadPool().getPoolSize();
        //线程数量;线程池中的最大线程数量;当线程池中空闲线程数量超过corePoolSize时，多余的线程会在多长时间内被销毁；
        //任务队列，被添加到线程池中，但尚未被执行的任务；它一般分为直接提交队列、有界任务队列、无界任务队列、优先任务队列几种
        //拒绝策略；当任务太多来不及处理时，如何拒绝任务
        this.LOG_RECORD_POOL_EXECUTOR = new ThreadPoolExecutor(poolSize, poolSize, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024), THREAD_FACTORY, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public ExecutorService getLogRecordPoolExecutor() {
        return LOG_RECORD_POOL_EXECUTOR;
    }
}
