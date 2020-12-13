package com.nbdyn.community.entity;

/**
 * @author dyn
 * @create 2020-12-13-10:21
 */
public class UserAndPost{
    private int userId;
    private int entity_id;

    @Override
    public String toString() {
        return "UserAndPost{" +
                "userId=" + userId +
                ", entity_id=" + entity_id +
                '}';
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityId() {
        return entity_id;
    }

    public void setEntityId(int entity_id) {
        this.entity_id = entity_id;
    }



}
