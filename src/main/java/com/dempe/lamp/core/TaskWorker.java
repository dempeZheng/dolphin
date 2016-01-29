package com.dempe.lamp.core;

import com.dempe.lamp.proto.Request;
import com.dempe.lamp.proto.Response;
import com.dempe.lamp.proto.json.JSONRequest;
import com.dempe.lamp.proto.json.JSONResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
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
            context.doFilter(request, ctx);
            ActionTake tack = new ActionTake(context);
            Response act = tack.act(request);
            if (act != null) {
                // 写入的时候已经release msg 无需显示的释放
//                LOGGER.info("act:{}", act.toString());
                ctx.writeAndFlush(act);
//                metricThread.increment();
            } else {
                ReferenceCountUtil.release(act);
            }
        } catch (InvocationTargetException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            context.removeLocalContext();
        }

    }
}
