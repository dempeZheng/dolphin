package com.dempe.lamp.codec.pack;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public class PackException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public PackException() {
        this("PackError");
    }

    public PackException(String message) {
        super(message);
    }

    public PackException(String message, Throwable cause) {
        super(message, cause);
    }

    public PackException(Throwable cause) {
        super(cause);
    }
}
