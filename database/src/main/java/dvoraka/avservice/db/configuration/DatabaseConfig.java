package dvoraka.avservice.db.configuration;

import dvoraka.avservice.db.repository.DbMessageInfoRepository;
import dvoraka.avservice.db.service.DbMessageInfoService;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
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
 * Database Spring configuration.
 */
@Configuration
@EnableJpaRepositories(basePackages = "dvoraka.avservice.db.repository")
@EnableTransactionManagement
@PropertySource("classpath:avservice.properties")
@Profile("db")
public class DatabaseConfig {

    @Autowired
    private Environment env;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("avservice.db.driver"));
        dataSource.setUrl(env.getProperty("avservice.db.url"));
        dataSource.setUsername(env.getProperty("avservice.db.user"));
        dataSource.setPassword(env.getProperty("avservice.db.pass"));

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
        properties.put("hibernate.dialect", env.getProperty("avservice.db.hibernate.dialect"));
        properties.put("hibernate.show_sql", env.getProperty("avservice.db.hibernate.show_sql"));
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.hbm2ddl.auto", "update");

        return properties;
    }
}
