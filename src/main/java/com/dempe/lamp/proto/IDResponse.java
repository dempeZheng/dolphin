package com.dempe.lamp.proto;

import com.alibaba.fastjson.JSON;
import com.dempe.lamp.utils.pack.Marshallable;
import com.dempe.lamp.utils.pack.Pack;
import com.dempe.lamp.utils.pack.Unpack;

import java.io.IOException;

/**
 * 返回消息
 * User: Dempe
 * Date: 2016/1/28
 * Time: 17:41
 * To change this template use File | Settings | File Templates.
 */
public class IDResponse implements Marshallable {

    private int id;
    private String data;

    public IDResponse() {
    }


    @Override
    public void marshal(Pack pack) {
        pack.putInt(id);
        pack.putVarstr(JSON.toJSONString(this));
    }

    @Override
    public void unmarshal(Unpack unpack) throws IOException {
        id = unpack.popInt();
        data = unpack.popVarstr();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "JSONResponse{" +
                "getId=" + id +
                ", data=" + data +
                '}';
    }


}
