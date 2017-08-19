package com.zhizus.forest.dolphin.experiment;

import com.netflix.client.http.HttpRequest;
import com.netflix.niws.client.http.RestClient;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Dempe on 2017/8/13 0013.
 */
public class THttpClient {

    private URL url_ = null;
    private final ByteArrayOutputStream requestBuffer_ = new ByteArrayOutputStream();
    private InputStream inputStream_ = null;
    private int connectTimeout_ = 0;
    private int readTimeout_ = 0;
    private Map<String, String> customHeaders_ = null;
    private final HttpHost host;
    private final RestClient client;

    public THttpClient(String url) throws TTransportException {
        try {
            this.url_ = new URL(url);
            this.client = null;
            this.host = null;
        } catch (IOException var3) {
            throw new TTransportException(var3);
        }
    }

    public THttpClient(String url, RestClient client) throws TTransportException {
        try {
            this.url_ = new URL(url);
            this.client = client;
            this.host = new HttpHost(this.url_.getHost(), -1 == this.url_.getPort() ? this.url_.getDefaultPort() : this.url_.getPort(), this.url_.getProtocol());
        } catch (IOException var4) {
            throw new TTransportException(var4);
        }
    }


    public void setCustomHeaders(Map<String, String> headers) {
        this.customHeaders_ = headers;
    }

    public void setCustomHeader(String key, String value) {
        if (this.customHeaders_ == null) {
            this.customHeaders_ = new HashMap();
        }

        this.customHeaders_.put(key, value);
    }

    public void open() {
    }

    public void close() {
        if (null != this.inputStream_) {
            try {
                this.inputStream_.close();
            } catch (IOException var2) {
                ;
            }
            this.inputStream_ = null;
        }

    }

    public boolean isOpen() {
        return true;
    }

    public int read(byte[] buf, int off, int len) throws TTransportException {
        if (this.inputStream_ == null) {
            throw new TTransportException("Response buffer is empty, no request.");
        } else {
            try {
                int iox = this.inputStream_.read(buf, off, len);
                if (iox == -1) {
                    throw new TTransportException("No more data available.");
                } else {
                    return iox;
                }
            } catch (IOException var5) {
                throw new TTransportException(var5);
            }
        }
    }

    public void write(byte[] buf, int off, int len) {
        this.requestBuffer_.write(buf, off, len);
    }


    private void flushUsingHttpClient() throws TTransportException {
        if (null == this.client) {
            throw new TTransportException("Null HttpClient, aborting.");
        } else {
            byte[] data = this.requestBuffer_.toByteArray();
            this.requestBuffer_.reset();
            HttpPost post = null;
            InputStream is = null;

            try {

                post = new HttpPost(this.url_.getFile());
                post.setHeader("Content-Type", "application/x-thrift");
                post.setHeader("Accept", "application/x-thrift");
                post.setHeader("User-Agent", "Java/THttpClient/HC");
                if (null != this.customHeaders_) {
                    Iterator ioe = this.customHeaders_.entrySet().iterator();

                    while (ioe.hasNext()) {
                        Map.Entry responseCode = (Map.Entry) ioe.next();
                        post.setHeader((String) responseCode.getKey(), (String) responseCode.getValue());
                    }
                }

                post.setEntity(new ByteArrayEntity(data));
                HttpRequest request = HttpRequest.newBuilder().uri(host.getHostName()).build(); // 3

                com.netflix.client.http.HttpResponse ioe1 = this.client.execute(request);
                int responseCode1 = ioe1.getStatus();

                if (responseCode1 != 200) {
                    throw new TTransportException("HTTP Response code: " + responseCode1);
                }
                this.inputStream_ = ioe1.getInputStream();
            } catch (Exception var19) {
                if (null != post) {
                    post.abort();
                }

                throw new TTransportException(var19);
            } finally {
                if (null != is) {
                    try {
                        is.close();
                    } catch (IOException var17) {
                        throw new TTransportException(var17);
                    }
                }

            }
        }
    }

    public void flush() throws TTransportException {
        if (null != this.client) {

            this.flushUsingHttpClient();
        } else {
            byte[] data = this.requestBuffer_.toByteArray();
            this.requestBuffer_.reset();

            try {
                HttpURLConnection iox = (HttpURLConnection) this.url_.openConnection();
                if (this.connectTimeout_ > 0) {
                    iox.setConnectTimeout(this.connectTimeout_);
                }

                if (this.readTimeout_ > 0) {
                    iox.setReadTimeout(this.readTimeout_);
                }

                iox.setRequestMethod("POST");
                iox.setRequestProperty("Content-Type", "application/x-thrift");
                iox.setRequestProperty("Accept", "application/x-thrift");
                iox.setRequestProperty("User-Agent", "Java/THttpClient");
                if (this.customHeaders_ != null) {
                    Iterator responseCode = this.customHeaders_.entrySet().iterator();

                    while (responseCode.hasNext()) {
                        Map.Entry header = (Map.Entry) responseCode.next();
                        iox.setRequestProperty((String) header.getKey(), (String) header.getValue());
                    }
                }

                iox.setDoOutput(true);
                iox.connect();
                iox.getOutputStream().write(data);
                int responseCode1 = iox.getResponseCode();
                if (responseCode1 != 200) {
                    throw new TTransportException("HTTP Response code: " + responseCode1);
                } else {
                    this.inputStream_ = iox.getInputStream();
                }
            } catch (IOException var5) {
                throw new TTransportException(var5);
            }
        }
    }

    public static class Factory extends TTransportFactory {
        private final String url;
        private final HttpClient client;

        public Factory(String url) {
            this.url = url;
            this.client = null;
        }

        public Factory(String url, HttpClient client) {
            this.url = url;
            this.client = client;
        }

        public TTransport getTransport(TTransport trans) {
            try {
                return null != this.client ? new org.apache.thrift.transport.THttpClient(this.url, this.client) : new org.apache.thrift.transport.THttpClient(this.url);
            } catch (TTransportException var3) {
                return null;
            }
        }
    }
}
