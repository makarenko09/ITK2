package tech.itk.task.task.infrastructure.secondary;

import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Конфигурация Hibernate SessionFactory через JPA.
 */
@Configuration
public class HibernateConfiguration {

  @Value("${spring.jpa.hibernate.ddl-auto:update}")
  private String ddlAuto;

  @Value("${spring.jpa.properties.hibernate.dialect:org.hibernate.dialect.PostgreSQLDialect}")
  private String dialect;

  @Value("${spring.jpa.properties.hibernate.format_sql:true}")
  private String formatSql;

  @Value("${spring.jpa.show-sql:true}")
  private String showSql;

  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
    emf.setDataSource(dataSource);
    emf.setPackagesToScan("tech.itk.task.task.domain");
    emf.setPersistenceProvider(new HibernatePersistenceProvider());
    emf.setJpaProperties(hibernateProperties());
    return emf;
  }

  @Bean
  public SessionFactory sessionFactory(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
    return entityManagerFactory.getNativeEntityManagerFactory().unwrap(SessionFactory.class);
  }

  private Properties hibernateProperties() {
    Properties properties = new Properties();
    properties.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
    properties.setProperty("hibernate.dialect", dialect);
    properties.setProperty("hibernate.format_sql", formatSql);
    properties.setProperty("hibernate.show_sql", showSql);
    return properties;
  }
}
