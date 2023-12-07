package com.kfm.system.mapper;



import com.kfm.system.domain.User;


import java.util.List;

/**
* @author wangxu
* @description 针对表【user】的数据库操作Mapper
* @createDate 2023-11-10 20:29:11
* @Entity generator.domain.User
*/
public interface UserMapper {
    List<User> selectAll (User user);
    Integer insertSelective(User user);

    Integer updateByUser(User user);
    Integer updatePasswordByUser(User user);
}




