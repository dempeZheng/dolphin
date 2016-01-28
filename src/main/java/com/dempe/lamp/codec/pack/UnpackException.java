package com.dempe.lamp.codec.pack;

/**
 * Created with IntelliJ IDEA.
 * User: Dempe
 * Date: 2016/1/28
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class UnpackException extends RuntimeException {
    public static final long serialVersionUID = 12L;

    public UnpackException() {
        this("Unpack error");
    }

    public UnpackException(String message) {
        super(message);
    }

    public UnpackException(Throwable cause) {
        super(cause);
    }

    public UnpackException(String message, Throwable cause) {
        super(message, cause);
    }

}
