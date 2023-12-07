package com.kfm.system.ex;

public class RegisterException extends Exception{
    /**
     * 注册异常类
     * 0: 参数校验失败
     * 1: 用户名已存在
     * 2: 密码不一致
     * 3: 未知错误
     */
    private int code;

    public RegisterException() {
        this(3, "注册失败");
    }
    public RegisterException(String message) {
        this(3, message);
    }

    public RegisterException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误码
     * @return 错误码
     */
    public int getCode() {
        return code;
    }
}

