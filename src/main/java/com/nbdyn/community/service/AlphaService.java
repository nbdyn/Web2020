package com.nbdyn.community.service;

import com.nbdyn.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author dyn
 * @create 2020-11-15-20:19
 */
@Service
//@Scope("prototype")   //singleton
public class AlphaService {

    public AlphaService(){
        System.out.println("实例化Alpha");
    }

    @Autowired
    private AlphaDao alphaDao;

    @PostConstruct
    public void  init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destory(){
        System.out.println("销毁Alpha");
    }



    public String find(){
        return alphaDao.select();//service依赖dao
    }

}
