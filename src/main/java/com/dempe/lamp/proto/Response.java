package com.dempe.lamp.proto;

import com.dempe.lamp.utils.pack.Marshallable;
import com.dempe.lamp.utils.pack.Pack;
import com.dempe.lamp.utils.pack.Unpack;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/3/9
 * Time: 10:08
 * To change this template use File | Settings | File Templates.
 */
public class Response implements Marshallable {

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Resp{" +
                "data='" + data + '\'' +
                "} " + super.toString();
    }

    @Override
    public void marshal(Pack pack) {

        pack.putVarstr(data);
    }

    @Override
    public void unmarshal(Unpack unpack) throws IOException {
        data = unpack.popVarstr();
    }
}
