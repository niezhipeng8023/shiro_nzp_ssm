package com.service;

import com.bean.Permission;
import com.bean.User;
import com.dao.ShiroDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    private ShiroDao shiroDao;

    public User getUserByUserName(String username) {
        //根据账号获取账号密码
        User userByUserName =  shiroDao.getUserByUserName(username);
        return userByUserName;
    }

    public List<Permission> getPermissionsByUser(User user) {
        //获取到用户角色userRole
        List<Integer> roleId = shiroDao.getUserRoleByUserId(user.getId());
        List<Permission> perArrary = new ArrayList<>();

        if (roleId!=null&&roleId.size()!=0) {
            //根据roleid获取peimission
            for (Integer i : roleId) {
                perArrary.addAll(shiroDao.getPermissionsByRoleId(i));
            }
        }

        System.out.println(perArrary);
        return perArrary;
    }
}
