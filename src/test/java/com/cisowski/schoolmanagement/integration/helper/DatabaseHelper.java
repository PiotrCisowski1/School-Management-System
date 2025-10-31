package com.cisowski.schoolmanagement.integration.helper;

import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import jakarta.transaction.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class DatabaseHelper {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseHelper(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void clearDatabase() {
        jdbcTemplate.execute("DROP SCHEMA public CASCADE");
        jdbcTemplate.execute("CREATE SCHEMA public");
        jdbcTemplate.execute("GRANT ALL ON SCHEMA public TO public");
    }

    public void clearData() {
        List<String> tables = jdbcTemplate.queryForList(
                "SELECT tablename FROM pg_tables WHERE schemaname = 'public'", String.class
        );

        jdbcTemplate.execute("TRUNCATE TABLE " + String.join(",", tables) + " RESTART IDENTITY CASCADE");
    }

    public void fillBasicAuthorities() {
        List<String> authorities = List.of(
                "ADMINISTRATOR",
                "STUDENT",
                "PARENT",
                "TEACHER"
        );
        authorities.forEach(auth -> {
            jdbcTemplate.execute("INSERT INTO authorities (authority) VALUES ('" + auth + "')");
        });
    }

    public Optional<AuthorityEntity> fetchAuthorityByName(String authority) {
        String query = "SELECT id, authority FROM authorities WHERE authority = ?";
        AuthorityEntity auth = jdbcTemplate.queryForObject(query, (rs, rowNum) -> {
            AuthorityEntity authEntity = new AuthorityEntity();
            authEntity.setId(rs.getInt("id"));
            authEntity.setAuthority(rs.getString("authority"));
            return authEntity;
        }, authority);
        return Optional.ofNullable(auth);
    }
}
