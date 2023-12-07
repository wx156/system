package com.kfm.system.bean;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
@Data
@Component
public class IpAccessList implements Serializable {
    Map<String, AccessInfo> ipMap;

    public IpAccessList(){
        ipMap = new HashMap<>();
    }
}
