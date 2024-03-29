package com.nbdyn.community.dao;
import com.nbdyn.community.entity.User;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author dyn
 * @create 2020-11-24-15:31
 */
@Mapper
public interface UserMapper {
    User selectById(int id);

    User selectByName(String username);

    User selectByEmail(String email);

    User selectByPhone(String phone);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeader(int id, String headerUrl);

    int updatePassword(int id, String password);

    int updateEmail(int id, String email);

    int updatePhone(int id, String phone);

    int updateCV(int id, String CV);

}
