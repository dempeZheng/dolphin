package com.dempe.lamp.proto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.codec.pack.Pack;
import com.dempe.lamp.codec.pack.Unpack;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class LampRequest implements Request {

    private String uri;
    private JSONObject data;

    public LampRequest() {
    }

    public LampRequest(String uri, JSONObject paramJSON) {
        this.uri = uri;
        this.data = paramJSON;
    }

    @Override
    public void marshal(Pack pack) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uri",uri);
        jsonObject.put("data",data);
        pack.putVarstr(jsonObject.toJSONString());
    }

    @Override
    public void unmarshal(Unpack unpack) {
        String jsonStr = unpack.popVarstr();
        if (StringUtils.isNotBlank(jsonStr)) {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            if (jsonObject != null) {
                this.uri = jsonObject.getString("uri");
                this.data = jsonObject.getJSONObject("data");
            }

        }
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public JSONObject getParamJSON() {
        return data;
    }
}
