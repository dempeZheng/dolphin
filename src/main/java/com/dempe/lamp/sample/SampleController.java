package com.dempe.lamp.sample;

import com.alibaba.fastjson.JSONObject;
import com.dempe.lamp.Param;
import com.dempe.lamp.Path;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * sample业务开发controller
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
@Controller("sample")
public class SampleController {

    @Resource
    SampleService lampService;

    /**
     * 默认匹配path getUri=/sample/hello
     * 默认注入request name属性的参数值
     *
     * @param name
     * @return
     */
    @Path
    public JSONObject hello(@Param String name,@Param int age) {
//        System.out.println(age);
        return lampService.hello(name);
    }
}
