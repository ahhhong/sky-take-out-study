package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface UserMapper {

    /**
     * 根据openid查找用户
     * @param openid
     */
    @Select("select * from user where openid=#{openid}")
    User findByOpenId(String openid);

    /**
     *
     * @param user
     */
    void insert(User user);
}
