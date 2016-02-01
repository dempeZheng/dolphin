package com.dempe.lamp.proto.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.proto.Request;
import com.dempe.lamp.utils.pack.Pack;
import com.dempe.lamp.utils.pack.Unpack;
import org.apache.commons.lang3.StringUtils;

/**
 * json request协议的实现
 * User: Dempe
 * Date: 2016/1/28
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class JSONRequest implements Request {

    private int id;
    private String uri;
    private JSONObject data;

    public JSONRequest() {
    }

    public JSONRequest(String uri, JSONObject paramJSON) {
        this.uri = uri;
        this.data = paramJSON;
    }

    @Override
    public void marshal(Pack pack) {
        String str = JSON.toJSONString(this);
        pack.putVarstr(str);
    }

    @Override
    public void unmarshal(Unpack unpack) {
        String jsonStr = unpack.popVarstr();
        if (StringUtils.isNotBlank(jsonStr)) {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            if (jsonObject != null) {
                this.uri = jsonObject.getString("uri");
                this.data = jsonObject.getJSONObject("data");
                this.id = jsonObject.getInteger("id");
            }

        }
    }


    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
