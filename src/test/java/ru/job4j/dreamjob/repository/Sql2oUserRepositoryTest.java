package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;


    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenUserAddThenEquals() {
        User user = new User(0, "mail", "name", "password");
        assertThat(sql2oUserRepository.save(user)).isEqualTo(Optional.of(user));
    }

    @Test
    public void whenUserAddWithSameEmailsThenEmpty() {
        User user1 = new User(0, "mail", "name", "password");
        User user2 = new User(0, "mail", "name1", "password1");
        sql2oUserRepository.save(user1);
        assertThat(sql2oUserRepository.save(user2)).isEqualTo(Optional.empty());
    }

    @Test
    public void whenSaveUsersThenGetAll() {
        var user1 = sql2oUserRepository.save(new User(0, "mail", "name", "password"));
        var user2 = sql2oUserRepository.save(new User(0, "mail2", "name1", "password1"));
        assertThat(sql2oUserRepository.findAll()).isEqualTo(List.of(user1.get(), user2.get()));
    }
}
