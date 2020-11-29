package com.nbdyn.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

/**
 * @author dyn
 * @create 2020-11-15-20:10
 */
@Repository("alphaHibernate")
public class AlphaDaoHibernatelmpl implements AlphaDao{

    @Override
    public String select() {
        return "Hibernate";
    }
}
