package com.zhizus.forest.dolphin.client.thttp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by dempezheng on 2017/8/16.
 */
public class THttpClient extends TTransport {
    private final ByteArrayOutputStream requestBuffer_ = new ByteArrayOutputStream();
    private InputStream inputStream_ = null;
    private int connectTimeout_ = 0;
    private int readTimeout_ = 0;
    private Map<String, String> customHeaders_ = null;
    private final DelegateLoadBalanceClient client;

    public THttpClient(DelegateLoadBalanceClient client) throws TTransportException {
        this.client = client;

    }

    public void setConnectTimeout(int timeout) {
        this.connectTimeout_ = timeout;
        if (null != this.client) {
//            this.client.getParams().setParameter("http.connection.timeout", Integer.valueOf(this.connectTimeout_));
        }

    }

    public void setReadTimeout(int timeout) {
        this.readTimeout_ = timeout;
        if (null != this.client) {
//            this.client.getParams().setParameter("http.socket.timeout", Integer.valueOf(this.readTimeout_));
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

    private void flushUsingHttpClient() throws TTransportException {
        if (null == this.client) {
            throw new TTransportException("Null HttpClient, aborting.");
        } else {
            byte[] data = this.requestBuffer_.toByteArray();
            this.requestBuffer_.reset();
            HttpPost post = null;
            InputStream is = null;

            try {
                post = new HttpPost();
                post.setHeader("Content-Type", "application/x-thrift");
                post.setHeader("Accept", "application/x-thrift");
                post.setHeader("User-Agent", "Java/THttpClient/HC");
                if (null != this.customHeaders_) {
                    Iterator i$ = this.customHeaders_.entrySet().iterator();

                    while (i$.hasNext()) {
                        Map.Entry<String, String> header = (Map.Entry) i$.next();
                        post.setHeader((String) header.getKey(), (String) header.getValue());
                    }
                }

                post.setEntity(new ByteArrayEntity(data));
                HttpResponse response = this.client.execute(post);
                int responseCode = response.getStatusLine().getStatusCode();
                is = response.getEntity().getContent();
                if (responseCode != 200) {
                    throw new TTransportException("HTTP Response code: " + responseCode);
                } else {
                    byte[] buf = new byte[1024];
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    boolean var8 = false;

                    int len;
                    do {
                        len = is.read(buf);
                        if (len > 0) {
                            baos.write(buf, 0, len);
                        }
                    } while (-1 != len);

                    try {
                        consume(response.getEntity());
                    } catch (IOException var18) {
                        ;
                    }

                    this.inputStream_ = new ByteArrayInputStream(baos.toByteArray());
                }
            } catch (IOException var19) {
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
            this.flushUsingHttpClient();

    }

}
