package com.nbdyn.community.service;

import com.nbdyn.community.dao.DiscussPostMapper;
import com.nbdyn.community.entity.DiscussPost;
import com.nbdyn.community.entity.UserAndPost;
import com.nbdyn.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author dyn
 * @create 2020-11-24-20:28
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }
    public List<DiscussPost> findDiscussPostsByKind(String kind, int offset, int limit) {
        return discussPostMapper.selectDiscussPostsByKind(kind, offset, limit);
    }





    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }


    public int findDiscussPostRowsBykind(String kind) {
        return discussPostMapper.selectDiscussPostRowsBykind(kind);
    }


    public int addDiscussPost(DiscussPost post){
        if (post==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }





    public int updateCommentCount(int id,int commentCount){
        return discussPostMapper.updateCommentCount(id,commentCount);
    }
    public int updateFinishOrNoT(int id,int status){
        return discussPostMapper.updateFinishOrNot(id,status);
    }



    public int deleteDiscussPost(int id){
        return discussPostMapper.deleteDiscussPost(id);
    }











    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }



}
