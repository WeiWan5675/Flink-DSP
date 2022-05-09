package com.weiwan.dsp.console.schedule;

import com.weiwan.dsp.console.service.ScheduleTaskService;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Zhu Shaoqin
 * @email zsqmia@163.com
 * @date 2022/3/21 15:23
 * @description
 */
@Configuration
public class ScheduleTaskConfig {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTaskConfig.class);


    //通过工厂获取调度器, 通过调度器对定时任务进行操作
    @Bean
    public Scheduler scheduler(@Qualifier("schedulerFactoryBean") SchedulerFactoryBean factoryBean) {
        return factoryBean.getScheduler();
    }

    //创建调度器工厂
    @Bean("schedulerFactoryBean")
    public SchedulerFactoryBean createFactoryBean(JobFactory jobFactory, @Qualifier("taskExecutor") Executor taskExecutor) {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(jobFactory);
        factoryBean.setTaskExecutor(taskExecutor);
        factoryBean.setOverwriteExistingJobs(true);
        factoryBean.setStartupDelay(15);
        return factoryBean;
    }

    //创建线程池用于运行定时任务
    @Bean("taskExecutor")
    public Executor getThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(1024);
        executor.setKeepAliveSeconds(4);
        executor.setQueueCapacity(0);
        executor.setRejectedExecutionHandler((Runnable r, ThreadPoolExecutor exe) -> {
            // 利用BlockingQueue的特性，任务队列满时等待放入
            try {
                if (!exe.getQueue().offer(r, 30, TimeUnit.SECONDS)) {
                    throw new Exception("Task offer failed after 30 sec");
                }
            } catch (Exception e) {
                logger.error("set rejected executor handler failed", e);
            }
        });
        return executor;
    }

    /**
     * 自动注入工厂创建
     * 只有有这个配置，quartz才可以使用自动注入，不然quartz无法使用Autowired等。
     * 定时任务的 Job 对象实例化的过程是通过 Quartz 内部自己完成的，但是我们通过 Spring 进行注入的 Bean 却是由 Spring 容器管理的，
     * Quartz 内部无法感知到 Spring 容器管理的 Bean，所以没有办法在创建 Job 的时候就给装配进去。
     * 所以我们要做的是将Job也装配到 Bean 容器中。
     *
     * @param applicationContext
     * @return
     */
    @Bean
    public SpringBeanJobFactory scheduleAutoBeanFactory(ApplicationContext applicationContext) {
        SpringBeanJobFactory springBeanJobFactory = new SpringBeanJobFactory(){
            @Override
            protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
                final Object jobInstance = super.createJobInstance(bundle);
                AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
                beanFactory.autowireBean(jobInstance);
                return jobInstance;
            }
        };
        return  springBeanJobFactory;
    }

}
