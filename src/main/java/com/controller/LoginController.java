package com.controller;

import com.bean.User;
import com.service.ShiroService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class LoginController {

    @Autowired
    private ShiroService shiroService;


    @RequestMapping(value = "/login")
    public String Login(String username, String password, User user, HttpServletRequest request, Model model){
        if(username==null){
            model.addAttribute("message", "账号不为空");
            return "login";
        }
        //主体,当前状态为没有认证的状态“未认证”
        Subject subject = SecurityUtils.getSubject();
        // 登录后存放进shiro token
        UsernamePasswordToken token=new UsernamePasswordToken(username,password);
        //登录方法（认证是否通过）
        //使用subject调用securityManager,安全管理器调用Realm
        try {
            //利用异常操作
            //需要开始调用到Realm中
            subject.login(token);
            user = (User)subject.getPrincipal();
            HttpSession session = request.getSession();
            session.setAttribute("user",subject);
            model.addAttribute("message", "登录完成");
            System.out.println("登录完成");
        } catch (UnknownAccountException e) {
            model.addAttribute("message", "账号密码不正确");
            return "index";
        }
        return "test";
    }

    @RequestMapping("/check")
    public String check( HttpServletRequest request){
        HttpSession session = request.getSession();
        Subject subject=(Subject)session.getAttribute("user");
        User user=(User)subject.getPrincipal();
        System.out.println(user.toString());
        return "permission";
    }

    @RequestMapping("/readName")
    public String readName(){

        return "name";
    }

    @RequestMapping("/readData")
    public String readData(){

        return "data";
    }

    @RequestMapping("/index")
    public String readData1(){

        return "index";
    }

    @RequestMapping("/nopermission")
    public String noPermission(){
        return "error";
    }



}
