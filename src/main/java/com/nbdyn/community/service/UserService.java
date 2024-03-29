package com.nbdyn.community.service;

import com.nbdyn.community.dao.LoginTicketMapper;
import com.nbdyn.community.dao.UserMapper;
import com.nbdyn.community.entity.LoginTicket;
import com.nbdyn.community.entity.User;
import com.nbdyn.community.util.CommunityConstant;
import com.nbdyn.community.util.CommunityUtil;
import com.nbdyn.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import javax.xml.transform.Templates;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dyn
 * @create 2020-11-24-20:35
 */
@Service
public class UserService implements CommunityConstant {
    public static String getType(Object o) {
        return o.getClass().toString();
    }
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextpath;

    public User findUserById(int id){
        return userMapper.selectById(id);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map=new HashMap<>();

        if (user==null){
            throw new IllegalArgumentException("参数不能为空!");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg","账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("emailMsg","邮箱不能为空!");
            return map;
        }

        String us=user.getPassword();
        Pattern pattern = Pattern.compile(".*[a-z]+.*");
        Pattern pattern2 = Pattern.compile(".*[A-Z]+.*");
        Pattern pattern3 = Pattern.compile("[^0-9]");
        Matcher m = pattern3.matcher(us);
        String us_cut=m.replaceAll("").trim();
        if(us.length()<=6){
            map.put("passwordMsg","密码不少于6位!");
            return map;
        }
        if(!pattern.matcher(us).matches()){
            map.put("passwordMsg","密码要包含小写字母！");
            return map;
        }
        if(!pattern2.matcher(us).matches()){
            map.put("passwordMsg","密码要包含大写字母！");
            return map;
        }
        if(us_cut.length()<=1){
            map.put("passwordMsg","密码要包含两个数字！");
            return map;
        }



        User u=userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账号已存在！");
            return map;
        }
        u=userMapper.selectByPhone(user.getPhone());
        if (u!=null){
            map.put("phoneMsg","该手机号已被注册！");
            return map;
        }
        u=userMapper.selectByEmail(user.getEmail());
        if (u!=null){
            map.put("emailMsg","该email已被注册！");
            return map;
        }

//        if(user.getCard_type()!="身份证"||user.getCard_type()!="护照"){
//            map.put("cardTypeMsg","请输入正确的证件类型（身份证或护照）！");
//            return map;
//        }

        if (StringUtils.isBlank(user.getCardNum())){
            map.put("cardNumMsg","证据号码不能为空!");
            return map;
        }
        if (StringUtils.isBlank(user.getCardName())){
            map.put("cardNameMsg","用户姓名不能为空!");
            return map;
        }


        //sign
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        //user.setStatus(0);
        user.setStatus(1);
        user.setActivationCode(CommunityUtil.generateUUID());
        //user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        //user.setHeaderUrl("http://localhost:8080/community/user/header/fbcfc9d351be4ceeaf30d5720efac141.jpg");
        user.setCreateTime(new Date());
        user.setCreateCity("北京");




        userMapper.insertUser(user);



        return map;
    }


    public Map<String, Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map=new HashMap<>();
        //判空
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空");
            return map;
        }



        //验证账号
        User user=userMapper.selectByName(username);
        if (user==null){
            map.put("usernameMsg","该账号不存在");
            return map;
        }


        password=CommunityUtil.md5(password+user.getSalt());
        if (!user.getPassword().equals(password)){
            map.put("passwordMsg","密码不正确");
            return map;
        }



        //生成登陆凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTicketMapper.insertLoginTicket(loginTicket);

        map.put("ticket",loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket,1);

    }
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId, String headerUrl) {
        return userMapper.updateHeader(userId, headerUrl);
    }

    // 修改密码
    public Map<String, Object> updatePassword(int userId, String oldPassword, String newPassword, String confirmPassword) {
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (StringUtils.isBlank(oldPassword)) {
            map.put("oldPasswordMsg", "原密码不能为空！！");
            return map;
        }
        if (StringUtils.isBlank(newPassword)) {
            map.put("newPasswordMsg", "新密码不能为空！！");
            return map;
        }
        // 验证原始密码
        User user = userMapper.selectById(userId);
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!user.getPassword().equals(oldPassword)) {
            map.put("oldPasswordMsg", "原密码输入有误！！");
            return map;
        }
        if (!newPassword.equals(confirmPassword)){
            map.put("confirmPasswordMsg", "两次输入的密码不同！！");
            return map;
        }


        String us=newPassword;
        Pattern pattern = Pattern.compile(".*[a-z]+.*");
        Pattern pattern2 = Pattern.compile(".*[A-Z]+.*");
        Pattern pattern3 = Pattern.compile("[^0-9]");
        Matcher m = pattern3.matcher(us);
        String us_cut=m.replaceAll("").trim();
        if(us.length()<=6){
            map.put("passwordMsg","密码不少于6位!");
            return map;
        }
        if(!pattern.matcher(us).matches()){
            map.put("passwordMsg","密码要包含小写字母！");
            return map;
        }
        if(!pattern2.matcher(us).matches()){
            map.put("passwordMsg","密码要包含大写字母！");
            return map;
        }
        if(us_cut.length()<=1){
            map.put("passwordMsg","密码要包含两个数字！");
            return map;
        }



        // 更新密码
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userMapper.updatePassword(userId, newPassword);
        return map;
    }

    public int updateEmail(int userId, String email){
        return userMapper.updateEmail(userId, email);
    }

    public int updatePhone(int userId, String phone){
        return userMapper.updatePhone(userId, phone);
    }

    public int updateCV(int userId, String CV){
        return userMapper.updateCV(userId, CV);
    }















    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        User user = this.findUserById(userId);

        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                switch (user.getType()) {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;
    }


}
