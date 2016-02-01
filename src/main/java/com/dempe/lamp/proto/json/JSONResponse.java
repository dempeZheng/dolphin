package com.dempe.lamp.proto.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.proto.Response;
import com.dempe.lamp.utils.pack.Pack;
import com.dempe.lamp.utils.pack.Unpack;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

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
        pack.putVarstr(JSON.toJSONString(this));
    }

    @Override
    public void unmarshal(Unpack unpack) throws IOException {
        String jsonStr = unpack.popVarstr();
        ObjectMapper objectMapper = new ObjectMapper();

        JSONResponse jsonResponse = objectMapper.readValue(jsonStr, JSONResponse.class);
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
                "getId=" + id +
                ", getUri='" + uri + '\'' +
                ", data=" + data +
                '}';
    }
}
