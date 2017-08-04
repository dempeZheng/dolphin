package com.zhizus.forest.dolphin.server;

import org.apache.thrift.TProcessor;

/**
 * Created by dempezheng on 2017/8/4.
 */
public interface ProcessorFactory {

     TProcessor getProcessor();
}
