package com.kfm.system.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
@Component
@Data
public class IPInfo implements Serializable {
    Map<String, Integer> ipMap;

    public IPInfo(){
        ipMap = new HashMap<>();
    }
}
