package com.bcloud.fire;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

public class ApplicationStartup implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(final ApplicationEvent event) {

        if (event instanceof ApplicationEnvironmentPreparedEvent) {
            config((ApplicationEnvironmentPreparedEvent) event);
        } else if (event instanceof ApplicationReadyEvent) {
            ready((ApplicationReadyEvent) event);
        }

    }

    private void config(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment env = event.getEnvironment();

        DataSource dataSource = null;
        String jndiName = env.getProperty("spring.datasource.jndi-name");
        if (jndiName != null) {
            JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
            dataSource = dataSourceLookup.getDataSource("java:comp/env/" + jndiName.trim());
        } else {
            DriverManagerDataSource dmDataSource = new DriverManagerDataSource();
            dmDataSource.setUrl(env.getProperty("spring.datasource.url"));
            dmDataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
            dmDataSource.setUsername(env.getProperty("spring.datasource.username"));
            dmDataSource.setPassword(env.getProperty("spring.datasource.password"));
            dataSource = dmDataSource;
        }

        try {
            loadSysParam(dataSource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private Properties loadSysParam(DataSource dataSource) throws Exception {
        Properties props = new Properties();

        try (
                Connection connection = dataSource.getConnection();
                PreparedStatement ps = connection.prepareStatement("select param_name,param_value from sys_param");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String key = rs.getString("param_name");
                String value = rs.getString("param_value");
                if (StringUtils.isBlank(value)) {
                    value = "";
                }
                if (StringUtils.isNotBlank(key)) {
                    props.setProperty(key, value);
                    System.setProperty(key, value); // 同时保存到SystemProperties
                }
            }
        }

        return props;
    }

    private void ready(ApplicationReadyEvent event) {
    }
}