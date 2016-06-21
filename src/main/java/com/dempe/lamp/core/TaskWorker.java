package com.dempe.lamp.core;

import com.dempe.lamp.proto.IDResponse;
import com.dempe.lamp.proto.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * 业务处理线程类
 * User: Dempe
 * Date: 2015/12/11
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
public class TaskWorker implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(TaskWorker.class);

    private ChannelHandlerContext ctx;
    private ServerContext context;
    private Request request;

//    private static MetricThread metricThread = new MetricThread("write");

    public TaskWorker(ChannelHandlerContext ctx, ServerContext context, Request request) {
        this.ctx = ctx;
        this.context = context;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            // set执行上下文环境
            context.setLocalContext(request, ctx);
            ActionTake tack = new ActionTake(context);
            final IDResponse act = tack.act(request);
            if (act != null) {
                // 写入的时候已经release msg 无需显示的释放
//                LOGGER.info("act:{}", act.toString());
                // 无需显示调用netty io现成提交任务，ctx.writeAndFlush方法已经做好了封装
                ctx.writeAndFlush(act);
//                ctx.executor().submit(new Runnable() {
//                    @Override
//                    public void run() {
//                        ctx.writeAndFlush(act);
//                    }
//                });

//                metricThread.increment();
            } else {
                ReferenceCountUtil.release(act);
            }
        } catch (InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (InstantiationException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            // remove执行上下文环境
            context.removeLocalContext();
        }

    }
}
