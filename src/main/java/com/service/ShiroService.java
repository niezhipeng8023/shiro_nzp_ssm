package com.service;

import com.bean.Permission;
import com.bean.User;
import org.springframework.stereotype.Service;

import java.util.List;



public interface ShiroService {
    /**
     * 根据账号获取账号密码
     * @param username
     * @return UserPojo
     */
    User getUserByUserName(String username);

    /**
     * 根据账号获取该账号的权限
     *
     * @param user
     * @return List
     */
    List<Permission> getPermissionsByUser(User user);

}
