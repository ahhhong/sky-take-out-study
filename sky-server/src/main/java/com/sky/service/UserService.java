package com.sky.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;


public interface UserService {

    /**
     * 登录
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO) throws JsonProcessingException;
}
