package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.UserRowMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    /**
     * Create user by user dto.
     * Creating a user and saving it to a database with a unique identifier.
     *
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
     *
     * @param userDto user dto for update.
     * @return UserDto updated or created user dto.
     */
    @Override
    public UserDto updateUser(UserDto userDto) {
        log.info("Got update user by user DTO: {}", userDto);
        final String UPDATE_SQL = "UPDATE PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ? WHERE ID = ?";
        final Long userId = userDto.getId();

        if (userId != null && getUserById(userId) != null) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
                ps.setString(1, userDto.getFullName());
                ps.setString(2, userDto.getTitle());
                ps.setLong(3, userDto.getAge());
                ps.setLong(4, userId);

                return ps;
            });
            log.info("Updated user with id: {}", userId);
            log.info("Updated user data: {}", userDto);

            return userDto;
        } else {
            UserDto createdUser = createUser(userDto);
            log.info("No user with the required id was found. Therefore, a new user was created: {}", createdUser);

            return createdUser;
        }
    }

    /**
     * Getting a user by its ID from database.
     *
     * @param id user ID.
     * @return UserDto if the user is found in the database. null if the user not found in the database.
     */
    @Override
    public UserDto getUserById(Long id) {
        log.info("Wants get user by user id: {}", id);
        final String GET_SQL = "SELECT * FROM PERSON WHERE ID = ?";

        UserDto userDto = null;
        List<UserDto> users = jdbcTemplate.query(GET_SQL, ps -> ps.setLong(1, id), userRowMapper);

        if (!users.isEmpty()) {
            userDto = users.get(0);
        }

        log.info("Received user: {}", userDto);

        return userDto;
    }

    /**
     * Deleting a user from the database by its ID.
     * If there is no user with this ID, then nothing happens.
     *
     * @param id user ID.
     */
    @Override
    public void deleteUserById(Long id) {
        log.info("Got delete user by user id: {}", id);
        final String DELETE_SQL = "DELETE FROM PERSON WHERE ID = ?";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
            ps.setLong(1, id);

            return ps;
        });

        log.info("User was deleted with id: {}", id);
    }
}
