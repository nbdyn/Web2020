package com.nbdyn.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author dyn
 * @create 2020-11-15-20:14
 */
@Repository
@Primary
public class AlphaDaoMybatis implements AlphaDao{

    @Override
    public String select() {
        return "Mybatis";
    }
}
