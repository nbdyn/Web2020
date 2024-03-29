package com.nbdyn.community.controller;

//import com.nbdyn.community.annotation.LoginRequired;
import com.nbdyn.community.annotation.LoginRequired;
import com.nbdyn.community.entity.DiscussPost;
import com.nbdyn.community.entity.Page;
import com.nbdyn.community.entity.User;
import com.nbdyn.community.service.DiscussPostService;
import com.nbdyn.community.service.UserService;
import com.nbdyn.community.util.CommunityUtil;
import com.nbdyn.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.INTERNAL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        User user=hostHolder.getUser();//调用selectById

        model.addAttribute("user",user);
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/earning", method = RequestMethod.GET)
    public String getEarningPage(Model model, Page page) {
        User user=hostHolder.getUser();//调用selectById
        model.addAttribute("user",user);

        int fee=0;

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, 0, Integer.MAX_VALUE);
        System.out.println(list.toArray().length);
        if (list != null) {
            for (DiscussPost post : list) {
                fee=fee+post.getCommentCount()+Integer.parseInt(post.getPeopleNum())*3;//令主支付中介费（召集人数*3 元）、接令支付中介费（ 1 元）
            }

        }

        model.addAttribute("fee",fee);

        return "/site/earning";
    }


    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }

        // 生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }





        // 主要就是下面在进行更新
        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // 响应图片
        response.setContentType("image/" + suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    // 修改密码
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword,Model model) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.updatePassword(user.getId(), oldPassword, newPassword,confirmPassword);
        if (map == null || map.isEmpty()) {
            return "redirect:/logout";
        } else {
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            model.addAttribute("confirmPasswordMsg", map.get("confirmPasswordMsg"));

            return "redirect:/user/setting";
        }
    }


    // 修改邮箱
    @RequestMapping(path = "/updateEmail", method = RequestMethod.POST)
    public String updatePassword(String newEmail,Model model) {
        User user = hostHolder.getUser();
        userService.updateEmail(user.getId(), newEmail);
        return "redirect:/user/setting";
    }

    // 修改电话
    @RequestMapping(path = "/updatePhone", method = RequestMethod.POST)
    public String updatePhone(String newPhone,Model model) {
        User user = hostHolder.getUser();
        userService.updatePhone(user.getId(), newPhone);
        return "redirect:/user/setting";
    }

    // 修改简介
    @RequestMapping(path = "/updateCV", method = RequestMethod.POST)
    public String updateCV(String newCV,Model model) {
        User user = hostHolder.getUser();
        userService.updateCV(user.getId(), newCV);
        return "redirect:/user/setting";
    }
}
