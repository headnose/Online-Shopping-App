package com.headnose.personalproject.config;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {


    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {

        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource);

        // Make sure this matches your entity package exactly
        factory.setPackagesToScan("com.headnose.personalproject.entity");

        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        props.put("hibernate.hbm2ddl.auto", "update");

        factory.setHibernateProperties(props);

        return factory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}
