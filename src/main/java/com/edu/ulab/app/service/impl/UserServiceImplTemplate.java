package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Create user by user dto.
     * Creating a user and saving it to a database with a unique identifier.
     * @param userDto data for create user.
     * @return UserDto user dto with ID.
     */
    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Got create user by user DTO: {}", userDto);
        final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());

                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Created user: {}", userDto);

        return userDto;
    }

    /**
     * Update user by user dto.
     * If the updated user is not in the database, then a new one is created.
     * @param userDto user dto for update.
     * @return UserDto updated or created user dto.
     */
    @Override
    public UserDto updateUser(UserDto userDto) {
        log.info("Got update user by user DTO: {}", userDto);
        final String UPDATE_SQL = "UPDATE PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ? WHERE ID = ?";
        final Long userId = userDto.getId();

        if (userId != null && getUserById(userId) != null) {
            jdbcTemplate.update(UPDATE_SQL, userDto.getFullName(), userDto.getTitle(), userDto.getAge(), userId);
            log.info("Updated user with id: {}", userId);

            return userDto;
        } else {
            UserDto createdUser = createUser(userDto);
            log.info("No user with the required id was found. Therefore, a new user was created: {}", createdUser);

            return createdUser;
        }
    }

    /**
     * Getting a user by its ID from database.
     * @param id user ID.
     * @return UserDto if the user is found in the database. null if the user not found in the database.
     */
    @Override
    public UserDto getUserById(Long id) {
        log.info("Wants get user by user id: {}", id);
        final String GET_SQL = "SELECT * FROM PERSON WHERE ID = ?";

        SqlRowSet userRowSet = jdbcTemplate.queryForRowSet(GET_SQL, id);
        UserDto userDto = null;
        if (userRowSet.first()) {
            userDto = UserDto.builder()
                    .id(userRowSet.getLong("ID"))
                    .fullName(userRowSet.getString("FULL_NAME"))
                    .title(userRowSet.getString("TITLE"))
                    .age(userRowSet.getInt("AGE"))
                    .build();
        }
        log.info("Received user: {}", userDto);

        return userDto;
    }

    /**
     * Deleting a user from the database by its ID.
     * If there is no user with this ID, then nothing happens.
     * @param id user ID.
     */
    @Override
    public void deleteUserById(Long id) {
        log.info("Got delete user by user id: {}", id);
        final String DELETE_SQL = "DELETE FROM PERSON WHERE ID = ?";

        jdbcTemplate.update(DELETE_SQL, id);
        log.info("User was deleted with id: {}", id);
    }
}
