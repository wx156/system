package com.kfm.system.service;


import com.kfm.system.domain.User;
import com.kfm.system.ex.ServiceException;


import java.util.List;

/**
* @author wangxu
* @description 针对表【user】的数据库操作Service
* @createDate 2023-11-10 20:29:11
*/
public interface UserService {
    User selectAll(User user) throws ServiceException;
    Integer insert(User user) throws ServiceException;

    Integer update(User user) throws ServiceException;
}
