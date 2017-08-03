package com.zhizus.forest.dolphin.client;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import org.apache.http.HttpEntity;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dempezheng on 2017/8/3.
 */
public class THttpLoadBalanceClient extends TTransport {
    private String serviceName = null;
    private final ByteArrayOutputStream requestBuffer_ = new ByteArrayOutputStream();
    private InputStream inputStream_ = null;
    private int connectTimeout_ = 0;
    private int readTimeout_ = 0;
    private Map<String, String> customHeaders_ = null;
    private ILoadBalancer iLoadBalancer;

    public THttpLoadBalanceClient( ILoadBalancer iLoadBalancer) {
        this.iLoadBalancer = iLoadBalancer;

    }

    private URL selectURL() {
        Server aDefault = iLoadBalancer.chooseServer("default");

        try {
            return new URL("");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() {
        if (null != this.inputStream_) {
            try {
                this.inputStream_.close();
            } catch (IOException var2) {
            }

            this.inputStream_ = null;
        }
    }

    @Override
    public int read(byte[] buf, int off, int len) throws TTransportException {
        if (this.inputStream_ == null) {
            throw new TTransportException("Response buffer is empty, no request.");
        } else {
            try {
                int ret = this.inputStream_.read(buf, off, len);
                if (ret == -1) {
                    throw new TTransportException("No more data available.");
                } else {
                    return ret;
                }
            } catch (IOException var5) {
                throw new TTransportException(var5);
            }
        }
    }

    public void write(byte[] buf, int off, int len) {
        this.requestBuffer_.write(buf, off, len);
    }

    public void setConnectTimeout(int timeout) {
        this.connectTimeout_ = timeout;

    }

    public void setReadTimeout(int timeout) {
        this.readTimeout_ = timeout;
    }

    private static void consume(HttpEntity entity) throws IOException {
        if (entity != null) {
            if (entity.isStreaming()) {
                InputStream instream = entity.getContent();
                if (instream != null) {
                    instream.close();
                }
            }

        }
    }

    public void flush() throws TTransportException {

        // set callback

        byte[] data = this.requestBuffer_.toByteArray();
        this.requestBuffer_.reset();

        try {
            HttpURLConnection connection = (HttpURLConnection) this.selectURL().openConnection();
            if (this.connectTimeout_ > 0) {
                connection.setConnectTimeout(this.connectTimeout_);
            }

            if (this.readTimeout_ > 0) {
                connection.setReadTimeout(this.readTimeout_);
            }

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-thrift");
            connection.setRequestProperty("Accept", "application/x-thrift");
            connection.setRequestProperty("User-Agent", "Java/THttpClient");
            if (this.customHeaders_ != null) {
                Iterator i$ = this.customHeaders_.entrySet().iterator();

                while (i$.hasNext()) {
                    Map.Entry<String, String> header = (Map.Entry) i$.next();
                    connection.setRequestProperty((String) header.getKey(), (String) header.getValue());
                }
            }

            connection.setDoOutput(true);
            connection.connect();
            connection.getOutputStream().write(data);
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new TTransportException("HTTP Response code: " + responseCode);
            } else {
                this.inputStream_ = connection.getInputStream();
            }
        } catch (IOException var5) {
            throw new TTransportException(var5);
        }
    }

    public static class Factory extends TTransportFactory {
        private final String url;

        public Factory(String url) {
            this.url = url;
        }

        public TTransport getTransport(TTransport trans) {
            try {
                return new THttpClient(this.url);
            } catch (TTransportException var3) {
                return null;
            }
        }
    }
}
