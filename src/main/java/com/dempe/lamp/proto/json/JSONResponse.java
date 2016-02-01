package com.dempe.lamp.proto.json;

import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.codec.pack.Pack;
import com.dempe.lamp.codec.pack.Unpack;
import com.dempe.lamp.proto.Response;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 14:55
 * To change this template use File | Settings | File Templates.
 */
public class JSONResponse implements Response {

    private int id;

    private String uri;
    private JSONObject data;

    public JSONResponse() {
    }

    public JSONResponse(String uri, JSONObject paramJSON) {
        this.uri = uri;
        this.data = paramJSON;
    }

    @Override
    public void marshal(Pack pack) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uri", uri);
        jsonObject.put("data", data);
        jsonObject.put("id", id);
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
                this.id = jsonObject.getInteger("id");
            }

        }
    }

    @Override
    public int id() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "JSONResponse{" +
                "id=" + id +
                ", uri='" + uri + '\'' +
                ", data=" + data +
                '}';
    }
}
