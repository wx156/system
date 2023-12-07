package com.kfm.system.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resp implements Serializable {


    private Integer code;

    private String message;

    private Object data;

    public static Resp ok() {
        return new Resp(200, "操作成功", null);
    }
    public static Resp ok(String message) {
        return new Resp(200, message, null);
    }
    public static Resp ok(Integer code, String message) {
        return new Resp(code, message, null);
    }
    public static Resp ok(Integer code, String message, Object data) {
        return new Resp(code, message, data);
    }
    public static Resp ok(String message, Object data) {
        return new Resp(200,message, data);
    }

    public static Resp ok(Object data) {
        return new Resp(200, "操作成功", data);
    }

    public static Resp error(String message) {
        return new Resp(500, message, null);
    }

    public static Resp error(String message, Object data){
        return new Resp(500, message, data);
    }

    public static Resp error(Integer code, String message, Object data){
        return new Resp(code, message, data);
    }
}
