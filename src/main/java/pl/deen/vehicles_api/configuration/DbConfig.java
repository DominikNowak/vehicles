package pl.deen.vehicles_api.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    private DataSource dataSource;

    @Autowired
    public DbConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init(){
        String sql = "CREATE TABLE IF NOT EXISTS vehicles(vehicle_id int NOT NULL PRIMARY KEY AUTO_INCREMENT, mark varchar(50), model varchar(50), color varchar(50), year int(4))";
        getJdbcTemplate().update(sql);
    }
}
