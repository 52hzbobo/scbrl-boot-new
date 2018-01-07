package com.scbrl.wechat;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication
@MapperScan(basePackages = "com.scbrl.mapper")
@Slf4j
public class ScbrlWechatApplication {

	public static void main(String[] args) {

		SpringApplication.run(ScbrlWechatApplication.class, args);
		log.info("[------------------- >>> ScbrlWechatApplication Main Start In ScbrlApplication <<< -------------------]" );
	}


}
