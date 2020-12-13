package com.nbdyn.community.controller;


import com.nbdyn.community.entity.DiscussPost;
import com.nbdyn.community.entity.Page;
import com.nbdyn.community.entity.User;
import com.nbdyn.community.service.CommentService;
import com.nbdyn.community.service.DiscussPostService;
import com.nbdyn.community.service.UserService;
import com.nbdyn.community.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dyn
 * @create 2020-12-06-12:00
 */

@Controller
@RequestMapping("/master")
public class MasterController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;



    @RequestMapping(path = "/mypost",method = RequestMethod.GET)
    public String getMypost(Model model, Page page) {
        User user=hostHolder.getUser();//调用selectById
        model.addAttribute("user",user);


        page.setRows(discussPostService.findDiscussPostRows(user.getId()));
        page.setPath("/master/mypost");

        List<DiscussPost> list = discussPostService.findDiscussPosts(user.getId(), page.getOffset(), page.getLimit());
//        System.out.println(list.getClass().toString());
//        System.out.println(list.get(0).getClass().toString());

        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("discussPosts_length", page.getRows());



        return "/site/mypost";
    }

    @RequestMapping(path = "/myget",method = RequestMethod.GET)
    public String getMyget(Model model, Page page) {
        User user=hostHolder.getUser();//调用selectById
        model.addAttribute("user",user);


        page.setRows(commentService.findDistinctEntityId(user.getId(),0,Integer.MAX_VALUE).toArray().length);
//        System.out.println("leng="+page.getRows());
        page.setPath("/master/myget");

//        System.out.println("lllllllllllllllllllll="+user.getId());
        List<DiscussPost> list = commentService.findDistinctDiscussPosts(user.getId(), page.getOffset(), page.getLimit());

//        System.out.println("leng2="+list.toArray().length);
//        System.out.println(list.get(0).toString());

        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("discussPosts_length", page.getRows());



        return "/site/myget";
    }

}
