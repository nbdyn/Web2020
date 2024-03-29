package com.nbdyn.community.controller;

import com.nbdyn.community.entity.Comment;
import com.nbdyn.community.entity.DiscussPost;
import com.nbdyn.community.entity.Page;
import com.nbdyn.community.entity.User;
import com.nbdyn.community.service.CommentService;
import com.nbdyn.community.service.DiscussPostService;
import com.nbdyn.community.service.UserService;
import com.nbdyn.community.util.CommunityConstant;
import com.nbdyn.community.util.CommunityUtil;
import com.nbdyn.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content,String peopleNum,String kind,String endDate) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录哦!");
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        post.setFinishOrNot(0);
        post.setPeopleNum(peopleNum);
        post.setKind(kind);
        post.setEndDate(endDate);

        discussPostService.addDiscussPost(post);

        // 报错的情况,将来统一处理.
        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //帖子
        DiscussPost post=discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);
        //作者
        User user= userService.findUserById(post.getUserId());
        model.addAttribute("user",user);


        page.setLimit(5);
        page.setPath("/discuss/detail/"+discussPostId);
        page.setRows(post.getCommentCount());

        //评论：给帖子的评论
        //回复：给评论的评论
        //评论列表
        List<Comment> commentList=commentService.findCommentsByEntity(
                ENTITY_TYPE_POST,post.getId(), page.getOffset(),page.getLimit());
        //评论的View object列表
        List<Map<String,Object>> commentVoList=new ArrayList<>();
        if(commentList!=null){
            for (Comment comment:commentList){
                //一个评论的VO
                Map<String,Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //评论的作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                //回复列表
                List<Comment> replyList=commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE);
                //回复的VO列表
                List<Map<String,Object>> replyVoList=new ArrayList<>();
                if(replyList!=null){
                    for(Comment reply:replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        //回复的作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复的目标
                        User target=reply.getTargetId() ==0?null:userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);

                //回复数量
                int replyCount=commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);


                commentVoList.add(commentVo);
            }
        }



        model.addAttribute("comments",commentVoList);




        return "/site/discuss-detail";

    }














    // 置顶
    @RequestMapping(path = "/top", method = RequestMethod.POST)
    @ResponseBody
    public String setTop(int id) {
        discussPostService.updateType(id, 1);


        //等search
//        // 触发发帖事件
//        Event event = new Event()
//                .setTopic(TOPIC_PUBLISH)
//                .setUserId(hostHolder.getUser().getId())
//                .setEntityType(ENTITY_TYPE_POST)
//                .setEntityId(id);
//        eventProducer.fireEvent(event);



        return CommunityUtil.getJSONString(0);
    }

    // 加精
    @RequestMapping(path = "/wonderful", method = RequestMethod.POST)
    @ResponseBody
    public String setWonderful(int id) {
        discussPostService.updateStatus(id, 1);


        //等search
//        // 触发发帖事件
//        Event event = new Event()
//                .setTopic(TOPIC_PUBLISH)
//                .setUserId(hostHolder.getUser().getId())
//                .setEntityType(ENTITY_TYPE_POST)
//                .setEntityId(id);
//        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }

    // 删除
    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public String setDelete(int id) {
        discussPostService.updateStatus(id, 2);



        //等search
        //有改event文件夹的EventConsumer 7.3
//        // 触发删帖事件
//        Event event = new Event()
//                .setTopic(TOPIC_DELETE)
//                .setUserId(hostHolder.getUser().getId())
//                .setEntityType(ENTITY_TYPE_POST)
//                .setEntityId(id);
//        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0);
    }


}
