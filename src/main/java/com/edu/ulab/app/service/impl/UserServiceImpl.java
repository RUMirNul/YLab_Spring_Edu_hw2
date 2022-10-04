package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
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

        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user DTO to person: {}", user);

        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);

        UserDto returnedUserDto = userMapper.personToUserDto(savedUser);
        log.info("Mapped person to user DTO: {}", returnedUserDto);

        return returnedUserDto;
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

        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped DTO to person: {}", user);

        Person updatedUser = userRepository.save(user);
        log.info("Updated user: {}", updatedUser);

        UserDto returnedUserDto = userMapper.personToUserDto(updatedUser);
        log.info("Mapped person to user DTO: {}", returnedUserDto);

        return returnedUserDto;
    }

    /**
     * Getting a user by its ID from database.
     * @param id user ID.
     * @return UserDto if the user is found in the database. null if the user not found in the database.
     */
    @Override
    public UserDto getUserById(Long id) {
        log.info("Wants get user by user id: {}", id);

        Person receivedUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found user with id = " + id ));
        log.info("Received user: {}", receivedUser);

        UserDto returnedUserDto = userMapper.personToUserDto(receivedUser);
        log.info("Mapped person to user DTO: {}", returnedUserDto);

        return returnedUserDto;
    }

    /**
     * Deleting a user from the database by its ID.
     * If there is no user with this ID, then nothing happens.
     * @param id user ID.
     */
    @Override
    public void deleteUserById(Long id) {
        log.info("Got delete user by user id: {}", id);

        try {
            userRepository.deleteById(id);
            log.info("User was deleted with id: {}", id);
        } catch (Exception e) {
            //ignore
            log.info("No have user for deleting: {}", id);
        }
    }
}
