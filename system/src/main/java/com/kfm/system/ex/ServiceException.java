package com.kfm.system.ex;

/**
 * ServiceException类是一个继承自Exception的异常类。
 */
public class ServiceException extends Exception{

    /**
     * 构造函数，根据给定的消息创建一个ServiceException对象。
     *
     * @param message 异常消息
     */
    public ServiceException(String message) {
        super(message);
    }

    /**
     * 构造函数，根据给定的消息和原因创建一个ServiceException对象。
     *
     * @param message   异常消息
     * @param cause 异常原因
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
