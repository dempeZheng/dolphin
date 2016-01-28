package com.dempe.lamp.core;


import com.dempe.lamp.AppConfig;
import com.dempe.lamp.proto.Request;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.ApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: zhengdaxia
 * Date: 15/10/17
 * Time: 上午10:45
 * To change this template use File | Settings | File Templates.
 */
public class ServerContext {

    static final ThreadLocal<Context> localContext = new ThreadLocal<Context>();
    public RequestMapping mapping;
    public AppConfig config;

    private ApplicationContext context;
    public ServerContext(AppConfig config,ApplicationContext context) {
        this.config = config;
        this.context = context;
        this.mapping = new RequestMapping(config,context);
    }

    public static Request getYYProto() {
        return getContext().getYYProto();
    }

    static Context getContext() {
        Context context = localContext.get();
        if (context == null) {
            throw new RuntimeException("Please apply " + ServerContext.class.getName()
                    + " to any request which uses servlet scopes.");
        }
        return context;
    }

    public void doFilter(Request proto, ChannelHandlerContext ctx) {
        localContext.set(new Context(proto, ctx));
    }

    public void removeLocalContext() {
        localContext.remove();
    }

    public ActionMethod tackAction(String uri) {
        return mapping.tack(uri);
    }

    static class Context {

        final Request yyProto;

        final ChannelHandlerContext response;

        Context(Request yyProto, ChannelHandlerContext response) {
            this.yyProto = yyProto;
            this.response = response;
        }

        Request getYYProto() {
            return yyProto;
        }

        ChannelHandlerContext getResponse() {
            return response;
        }
    }
}
