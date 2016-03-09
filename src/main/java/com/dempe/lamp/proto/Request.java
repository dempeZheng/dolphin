package com.dempe.lamp.proto;

import com.dempe.lamp.utils.pack.MarshallUtils;
import com.dempe.lamp.utils.pack.Marshallable;
import com.dempe.lamp.utils.pack.Pack;
import com.dempe.lamp.utils.pack.Unpack;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/3/9
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
public class Request implements Marshallable {

    protected int messageID;

    private String uri;

    private Map<String, String> paramMap;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }

    public int getMessageID() {
        return messageID;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    @Override
    public String toString() {
        return "Req{" +
                "uri='" + uri + '\'' +
                ", paramMap=" + paramMap +
                "} " + super.toString();
    }

    @Override
    public void marshal(Pack pack) {
        pack.putInt(messageID);
        pack.putVarstr(uri);
        MarshallUtils.packMap(pack, paramMap, String.class, String.class);
    }

    @Override
    public void unmarshal(Unpack unpack) {
        messageID = unpack.popInt();
        uri = unpack.popVarstr();
        paramMap = MarshallUtils.unpackMap(unpack, String.class, String.class, false);
    }
}
