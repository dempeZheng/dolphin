package com.zhizus.forest.dolphin.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadPoolServer.Args;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


public abstract class AbstractThriftServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractThriftServer.class);

    private static final int threadNum = Math.max(1, Runtime.getRuntime().availableProcessors() * 2);

    private TServerSocket serverTransport;
    private ExecutorService servingExecutor;
    private TThreadPoolServer server;

    private  String thriftServerName = "forest_thrift_server";

    public void start() {


        LOGGER.info("Starting Thrift Server, bind to {}", getPort());

        try {
            serverTransport = new TServerSocket(new InetSocketAddress(getPort()));
        } catch (TTransportException e) {
            throw new RuntimeException("Failed to start Thrift Source!", e);
        }

        // Protocol factory
        TProtocolFactory protocolFactory = new TBinaryProtocol.Factory(true, true);

        TTransportFactory inTransportFactory = new TFramedTransport.Factory();
        TTransportFactory outTransportFactory = new TFramedTransport.Factory();

        final TProcessor processor = getProcessor();

        Args args = new Args(serverTransport).minWorkerThreads(threadNum) //
                .inputTransportFactory(inTransportFactory).outputTransportFactory(outTransportFactory) //
                .inputProtocolFactory(protocolFactory).outputProtocolFactory(protocolFactory) //
                .processor(processor);

        server = new TThreadPoolServer(args);

        final String threadName = String.format("Thrift Server %s I/O Boss", getServerName());
        final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat(threadName).build();
        servingExecutor = Executors.newSingleThreadExecutor(threadFactory);

        /**
         * Start serving.
         */
        servingExecutor.submit(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Thrift Server {} starting up...", getServerName());
                server.serve();
            }
        });

        long timeAfterStart = System.currentTimeMillis();
        while (!server.isServing()) {
            try {
                if (System.currentTimeMillis() - timeAfterStart >= 10000) {
                    throw new RuntimeException("Thrift Server " + getServerName() + " failed to start!");
                }
                TimeUnit.MILLISECONDS.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for Thrift Server " + getServerName() + " to start.", e);
            }
        }
    }

    public void close() {
        LOGGER.info("Stopping thrift server {}...", getServerName());
        if (server != null && server.isServing()) {
            server.stop();
        }
        servingExecutor.shutdown();
        try {
            if (!servingExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                servingExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while waiting for Thrift Server " + getServerName() + "to be shutdown.");
        }
    }

    public String getServerName() {
        return thriftServerName;
    }

    public AbstractThriftServer setThriftServerName(String thriftServerName) {
        this.thriftServerName = thriftServerName;
        return this;
    }

    public abstract int getPort();

    public abstract TProcessor getProcessor();

}
