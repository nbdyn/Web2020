package com.nbdyn.community;

import com.nbdyn.community.dao.AlphaDao;
import com.nbdyn.community.service.AlphaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
class CommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;//能手动从Spring获取所需要的bean

	@Test
	public void testApplicationContext() {
		System.out.println(applicationContext);
		AlphaDao alphaDao=applicationContext.getBean(AlphaDao.class);
		System.out.println(alphaDao.select());
		alphaDao=applicationContext.getBean("alphaHibernate",AlphaDao.class);//指定bean的名字输出
		System.out.println(alphaDao.select());
	}
	@Test
	public void testBeanManagement() {
		AlphaService alphaService=applicationContext.getBean(AlphaService.class);
		System.out.println("alphaService = " + alphaService);
		alphaService=applicationContext.getBean(AlphaService.class);
		System.out.println("alphaService = " + alphaService);
	}

	@Test
	public void testBeanConfig() {
		SimpleDateFormat simpleDateFormat=applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
		System.out.println("simpleDateFormat = " + simpleDateFormat);
		simpleDateFormat=applicationContext.getBean(SimpleDateFormat.class);
		System.out.println("simpleDateFormat = " + simpleDateFormat);


	}


	@Autowired
	@Qualifier("alphaHibernate")//为了不选优先级最高的mybatis
	private AlphaDao alphaDao;

	@Autowired
	private AlphaService alphaService;

	@Test
	public void TestDI(){
		System.out.println(alphaDao);
		System.out.println(alphaService);
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;//这就是spring容器
	}
}
