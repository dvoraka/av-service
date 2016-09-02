package dvoraka.avservice.configuration;

import dvoraka.avservice.db.repository.MessageInfoRepository;
import dvoraka.avservice.db.service.DefaultMessageInfoService;
import dvoraka.avservice.db.service.MessageInfoService;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Database Spring configuration.
 */
@Configuration
@ComponentScan("dvoraka.avservice.db")
@EnableJpaRepositories("dvoraka.avservice.db")
@Profile("database")
@EnableTransactionManagement
public class DatabaseConfig {


    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
                new LocalContainerEntityManagerFactoryBean();

        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan("dvoraka.avservice.db");

        return entityManagerFactoryBean;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/docker");
        dataSource.setUsername("docker");
        dataSource.setPassword("docker");

        return dataSource;
    }

//    @Bean
//    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
//        return new HibernateTransactionManager(sessionFactory);
//    }

//    private Properties hibernateProperties() {
//        Properties properties = new Properties();
//        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
//        properties.put("hibernate.show_sql", "true");
//        properties.put("hibernate.format_sql", "true");
//
//        return properties;
//    }

    @Bean
    public MessageInfoService messageInfoService(MessageInfoRepository messageInfoRepository) {
        return new DefaultMessageInfoService(messageInfoRepository);
    }
}
