package com.nbdyn.community.dao;

import com.nbdyn.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dyn
 * @create 2020-11-24-19:39
 */
@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);//offset是每一页起始行的行号 limit每一页做多多少条
    List<DiscussPost> selectDiscussPostsByKind(String kind, int offset, int limit);//offset是每一页起始行的行号 limit每一页做多多少条

    // @Param注解用于给参数取别名,
    // 如果只有一个参数,并且在动态标签<if>里使用,则必须加别名.
    // 找出总共有多少行，即总共多少条
    int selectDiscussPostRows(@Param("userId") int userId);

    int selectDiscussPostRowsBykind(@Param("kind") String kind);


    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);






    //3.6
    int updateCommentCount(int id,int commentCount);



    int deleteDiscussPost(int id);









    int updateType(int id, int type);

    int updateStatus(int id, int status);

    int updateFinishOrNot(int id, int status);

}
