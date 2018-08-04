package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPollUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2018/8/3.
 */
@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    //@Scheduled(cron = "0 */1 * * * ?")//每个一分钟整数倍执行
    public void closeOrderTaskV1() {
        log.info("关闭订单定时任务启动");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        //iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }

    public void closeOrderTaskV2() {
        log.info("关闭订单定时任务启动");

        long lockName = Long.parseLong(PropertiesUtil.getProperty("close.time", "50000"));
        Long setnx = RedisShardedPollUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis() + lockName));

        if (setnx != null && setnx.intValue() == 1) {
            //如果返回值为1,代表设置成功,获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

        } else {
            log.info("没有获取分布式锁: {}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }

        log.info("关闭订单定时任务结束");
    }

    private void closeOrder(String lockName) {
        RedisShardedPollUtil.expire(lockName, 50);//设置有效期为50秒 防止死锁
        //打印日志是哪个线程获取了锁
        log.info("获取:{} ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour", "2"));
        iOrderService.closeOrder(hour);
        RedisShardedPollUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放:{} ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        log.info("======================================================");


    }
}
