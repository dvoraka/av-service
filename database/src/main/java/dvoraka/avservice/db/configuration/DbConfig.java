package dvoraka.avservice.db.configuration;

import dvoraka.avservice.db.repository.db.DbMessageInfoRepository;
import dvoraka.avservice.db.service.DbMessageInfoService;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
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
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
                new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan("dvoraka.avservice.db.model");
        entityManagerFactoryBean.setJpaProperties(hibernateProperties());

        return entityManagerFactoryBean;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);

        return transactionManager;
    }

    @Bean
    public MessageInfoService messageInfoService(DbMessageInfoRepository messageInfoRepository) {
        return new DbMessageInfoService(messageInfoRepository);
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "update");

        return properties;
    }
}
