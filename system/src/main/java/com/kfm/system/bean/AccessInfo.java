package com.kfm.system.bean;

import lombok.Data;

import java.io.Serializable;
@Data
public class AccessInfo implements Serializable {
    private long millis;

    private Integer count;
}
