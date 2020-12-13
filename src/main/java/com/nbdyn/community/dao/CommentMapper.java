package com.nbdyn.community.dao;

import com.nbdyn.community.entity.Comment;
import com.nbdyn.community.entity.DiscussPost;
import com.nbdyn.community.entity.UserAndPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);


    List<UserAndPost> selectDistinctEntityId(int userId, int offset, int limit);//offset是每一页起始行的行号 limit每一页做多多少条



}
