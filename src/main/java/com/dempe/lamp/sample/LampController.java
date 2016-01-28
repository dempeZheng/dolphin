package com.dempe.lamp.sample;

import com.dempe.lamp.Param;
import com.dempe.lamp.Path;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
@Controller("lamp")
public class LampController {

    @Resource
    LampService lampService;

    @Path
    public void test(@Param String name) {
        System.out.println("name===>" + name);
        lampService.say();
        System.out.println("-------------------");
    }
}
