package com.zhizus.forest.dolphin.ribbon;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.InterceptingHttpAccessor;
import org.springframework.util.Assert;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

/**
 * Created by dempezheng on 2017/8/25.
 */
public class THttpTemplate extends InterceptingHttpAccessor {

    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

    public ClientHttpResponse execute(URI url, byte[] body) throws IOException {
        Assert.notNull(url, "'url' must not be null");
        ClientHttpResponse response = null;
        try {
            ClientHttpRequest request = createRequest(url, HttpMethod.POST);
            HttpHeaders headers = request.getHeaders();
            headers.add("Content-Type", "application/x-thrift");
            headers.add("Accept", "application/x-thrift");
            headers.add("User-Agent", "Java/THttpClient/HC");
            request.getBody().write(body);
            response = request.execute();
          //  handleResponse(url, HttpMethod.POST, response);
            return response;
        } catch (IOException ex) {
            throw new ResourceAccessException("I/O error on " + HttpMethod.POST.name() +
                    " request for \"" + url + "\": " + ex.getMessage(), ex);
        }
    }

    protected void handleResponse(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        ResponseErrorHandler errorHandler = getErrorHandler();
        boolean hasError = errorHandler.hasError(response);
        if (logger.isDebugEnabled()) {
            try {
                logger.debug(method.name() + " request for \"" + url + "\" resulted in " +
                        response.getRawStatusCode() + " (" + response.getStatusText() + ")" +
                        (hasError ? "; invoking error handler" : ""));
            } catch (IOException ex) {
                // ignore
            }
        }
        if (hasError) {
            errorHandler.handleError(response);
        }
    }


    /**
     * Set the error handler.
     * <p>By default, RestTemplate uses a {@link DefaultResponseErrorHandler}.
     */
    public void setErrorHandler(ResponseErrorHandler errorHandler) {
        Assert.notNull(errorHandler, "ResponseErrorHandler must not be null");
        this.errorHandler = errorHandler;
    }

    /**
     * Return the error handler.
     */
    public ResponseErrorHandler getErrorHandler() {
        return this.errorHandler;
    }


}
