package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    private final static Random RANDOM = new Random();
    private final static String USER_FULL_NAME = "test name";
    private final static String USER_TITLE = "test title";

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void saveUser_Test() {
        //Given
        int userAge = RANDOM.nextInt(1, 100);
        long userId = RANDOM.nextLong(1, 100000);

        UserDto userDto = UserDto.builder()
                .fullName(USER_FULL_NAME)
                .title(USER_TITLE)
                .age(userAge)
                .build();

        Person person = Person.builder()
                .fullName(USER_FULL_NAME)
                .title(USER_TITLE)
                .age(userAge)
                .build();


        Person savedPerson = Person.builder()
                .id(userId)
                .fullName(USER_FULL_NAME)
                .title(USER_TITLE)
                .age(userAge)
                .build();

        UserDto result = UserDto.builder()
                .id(userId)
                .fullName(USER_FULL_NAME)
                .title(USER_TITLE)
                .age(userAge)
                .build();


        //When
        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        //Then
        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(userId, userDtoResult.getId());
        assertEquals(USER_FULL_NAME, userDtoResult.getFullName());
        assertEquals(USER_TITLE, userDtoResult.getTitle());
        assertEquals(userAge, userDtoResult.getAge());
    }

    // update
    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updateUser_Test() {
        //Given
        int userAge = RANDOM.nextInt(1, 100);
        long userId = RANDOM.nextLong(1, 100000);

        UserDto userDto = UserDto.builder()
                .id(userId)
                .fullName(USER_FULL_NAME)
                .title(USER_TITLE)
                .age(userAge)
                .build();

        Person person = Person.builder()
                .id(userId)
                .fullName(USER_FULL_NAME)
                .title(USER_TITLE)
                .age(userAge)
                .build();

        Person savedPerson = Person.builder()
                .id(userId)
                .fullName(USER_FULL_NAME)
                .title(USER_TITLE)
                .age(userAge)
                .build();


        UserDto result = UserDto.builder()
                .id(userId)
                .fullName(USER_FULL_NAME)
                .title(USER_TITLE)
                .age(userAge)
                .build();

        //When
        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        //Then
        UserDto userDtoResult = userService.updateUser(userDto);
        assertEquals(userId, userDtoResult.getId());
        assertEquals(USER_FULL_NAME, userDtoResult.getFullName());
        assertEquals(USER_TITLE, userDtoResult.getTitle());
        assertEquals(userAge, userDtoResult.getAge());
    }


    // get
    @Test
    @DisplayName("Получение пользователя. Должно пройти успешно.")
    void getUserById_Test() {
        //Given
        int userAge = RANDOM.nextInt(1, 100);
        long userId = RANDOM.nextLong(1, 100000);

        Person person = Person.builder()
                .id(userId)
                .fullName(USER_FULL_NAME)
                .title(USER_TITLE)
                .age(userAge)
                .build();

        UserDto result = UserDto.builder()
                .id(userId)
                .fullName(USER_FULL_NAME)
                .title(USER_TITLE)
                .age(userAge)
                .build();

        //When
        when(userRepository.findById(userId)).thenReturn(Optional.of(person));
        when(userMapper.personToUserDto(person)).thenReturn(result);

        //Then
        UserDto userDtoResult = userService.getUserById(userId);
        assertEquals(userId, userDtoResult.getId());
        assertEquals(USER_FULL_NAME, userDtoResult.getFullName());
        assertEquals(USER_TITLE, userDtoResult.getTitle());
        assertEquals(userAge, userDtoResult.getAge());
    }

    @Test
    @DisplayName("Получение пользователя c id = null. Должно выбросить ошибку.")
    void getUser_WithNullArgument_Exception_Test() {
        assertThatThrownBy(() -> userService.getUserById(null))
                .isInstanceOf(NotFoundException.class);
    }

    // delete
    @Test
    @DisplayName("Удаление пользователя. Должно пройти успешно.")
    void deleteUser_Test() {
        //Given
        long userId = RANDOM.nextLong(1, 100000);

        //When
        userService.deleteUserById(userId);

        //Then
        verify(userRepository).deleteById(userId);
    }
}
