package dvoraka.avservice.db.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Database configuration for the import.
 */
@Configuration
@EnableJpaRepositories(basePackages = "dvoraka.avservice.db.repository")
@EnableTransactionManagement
@PropertySource("classpath:avservice.properties")
@Profile("db")
public class DbConfig {

    @Value("${avservice.db.driver}")
    private String driver;
    @Value("${avservice.db.url}")
    private String url;

    @Value("${avservice.db.user}")
    private String user;
    @Value("${avservice.db.pass}")
    private String pass;

    @Value("${avservice.db.hibernate.dialect}")
    private String dialect;
    @Value("${avservice.db.hibernate.show_sql}")
    private boolean showSql;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);

        return dataSource;
    }

    @Bean
    public Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "update");

        return properties;
    }
}
