package com.zhizus.forest.dolphin.client;

import org.apache.http.HttpStatus;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dempezheng on 2017/8/16.
 */
public class THttpClient extends TTransport {
    private final ByteArrayOutputStream requestBuffer_ = new ByteArrayOutputStream();
    private InputStream inputStream_ = null;
    private final THttpLoadBalancerClient client;
    private String path;

    public THttpClient(String path, THttpLoadBalancerClient client) throws TTransportException {
        this.path = path;
        this.client = client;
    }

    public void open() {
    }

    public void close() {
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

    public void flush() throws TTransportException {
        byte[] data = this.requestBuffer_.toByteArray();
        this.requestBuffer_.reset();

        try {
            ClientHttpResponse response = this.client.execute(path, data);
            int responseCode = response.getStatusCode().value();
            if (responseCode != HttpStatus.SC_OK) {
                throw new TTransportException("HTTP Response code: " + responseCode);
            }
            InputStream is = response.getBody();
            byte[] buf = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len = 0;
            do {
                len = is.read(buf);
                if (len > 0) {
                    baos.write(buf, 0, len);
                }
            } while (-1 != len);

            inputStream_ = new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException var19) {
            throw new TTransportException(var19);
        }

    }

}
