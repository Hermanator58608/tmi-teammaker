package hu.adsd.tmi.tmi_teammaker.java;

import org.springframework.boot.jdbc.*;
import org.springframework.context.annotation.*;
import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://fks1.hesselaar.dev:3306/tmi");
        dataSourceBuilder.username("tmi");
        dataSourceBuilder.password("Tmi2023!");
        return dataSourceBuilder.build();
    }
}
