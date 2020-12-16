package com.nbdyn.community.service;

import com.nbdyn.community.dao.CommentMapper;
import com.nbdyn.community.entity.Comment;
import com.nbdyn.community.entity.DiscussPost;
import com.nbdyn.community.entity.UserAndPost;
import com.nbdyn.community.util.CommunityConstant;
import com.nbdyn.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommentService implements CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private  SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;




    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    //3.6
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }

        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
            discussPostService.updateFinishOrNoT(comment.getEntityId(),1);
        }

        return rows;
    }












    public List<UserAndPost> findDistinctEntityId(int userId, int offset, int limit) {
        return commentMapper.selectDistinctEntityId(userId, offset, limit);
    }



    public List<DiscussPost> findDistinctDiscussPosts(int userId, int offset, int limit) {
        List<DiscussPost> list=new ArrayList<>();

//        System.out.println(commentMapper.selectDistinctEntityId(userId, offset, limit).get(0).toString());
//        System.out.println("----------------------------");
        for(UserAndPost userAndPost:commentMapper.selectDistinctEntityId(userId, offset, limit)){
            System.out.println(userAndPost.toString());
            list.add(discussPostService.findDiscussPostById(userAndPost.getEntityId()));
        }
//        System.out.println(commentMapper.selectDistinctEntityId(userId, offset, limit).getClass().toString());
//
//        System.out.println(commentMapper.selectDistinctEntityId(userId, offset, limit).toArray().length);
//
//        System.out.println(commentMapper.selectDistinctEntityId(userId, offset, limit).toArray().getClass().toString());
//
//        UserAndPost userAndPost=(UserAndPost) commentMapper.selectDistinctEntityId(userId, offset, limit).get(0);
//        list.add(discussPostService.findDiscussPostById(userAndPost.getDiscussPostId()));
        return list;
    }

    public int findPostedOrNot(int userId,int entityId){
        return commentMapper.selectPostedOrNot(userId,entityId);
    }




}
