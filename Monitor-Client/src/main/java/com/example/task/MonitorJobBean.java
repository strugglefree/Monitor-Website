package com.example.task;

import com.example.entity.RuntimeDetail;
import com.example.utils.MonitorUtils;
import com.example.utils.NetUtil;
import jakarta.annotation.Resource;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @author Ll
 * @description: 监视器定时任务执行
 * @date 2024/7/31 下午1:09
 */
@Component
public class MonitorJobBean extends QuartzJobBean {

    @Resource
    NetUtil net;
    @Resource
    MonitorUtils util;


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        RuntimeDetail runtimeDetail = util.monitorRuntimeDetail();
        net.updateRuntimeDetail(runtimeDetail);
    }
}
