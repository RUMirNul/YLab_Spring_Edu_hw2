package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Random;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    private final static Random RANDOM = new Random();
    private final static String PERSON_FULL_NAME = "Test Test";
    private final static String PERSON_TITLE = "reader";
    private final static Long DEFAULT_PERSON_ID = 1001L;
    private final static String DEFAULT_PERSON_FULL_NAME = "default user";
    private final static String DEFAULT_PERSON_TITLE = "reader";
    private final static int DEFAULT_PERSON_AGE = 55;
    private final static int DEFAULT_PERSON_COUNT_IN_DB = 1;


    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertPerson_thenAssertDmlCount() {
        //Given
        int userAge = RANDOM.nextInt(1, 100);
        Person person = new Person();
        person.setFullName(PERSON_FULL_NAME);
        person.setTitle(PERSON_TITLE);
        person.setAge(userAge);

        //When
        Person result = userRepository.save(person);

        //Then
        assertNotNull(result.getId());
        assertEquals(PERSON_FULL_NAME, result.getFullName());
        assertEquals(PERSON_TITLE, result.getTitle());
        assertEquals(userAge, result.getAge());

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    // update
    @DisplayName("Обновить пользователя. Число update должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePerson_thenAssertDmlCount() {
        // Given
        int userAge = RANDOM.nextInt(1, 100);

        Person person = new Person();
        person.setId(DEFAULT_PERSON_ID);
        person.setFullName(PERSON_FULL_NAME);
        person.setTitle(PERSON_TITLE);
        person.setAge(userAge);


        // When
        Person result = userRepository.save(person);

        // Then
        assertNotNull(result.getId());
        assertEquals(DEFAULT_PERSON_ID, result.getId());
        assertEquals(PERSON_FULL_NAME, result.getFullName());
        assertEquals(PERSON_TITLE, result.getTitle());
        assertEquals(userAge, result.getAge());

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }

    @DisplayName("Обновить пользователя Person = null. Должно выбросить ошибку.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void update_WithNullArgument_Exception_Test() {
        // when
        Throwable throwable = catchThrowable(() -> userRepository.save(null));

        // then
        assertThat(throwable)
                .isInstanceOf(InvalidDataAccessApiUsageException.class)
                .hasRootCauseInstanceOf(IllegalArgumentException.class);
    }


    // get
    @DisplayName("Получить пользователя. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPerson_thenAssertDmlCount() {

        // When
        Person result = userRepository.findById(DEFAULT_PERSON_ID).orElseThrow();

        // Then
        assertNotNull(result.getId());
        assertEquals(DEFAULT_PERSON_ID, result.getId());
        assertEquals(DEFAULT_PERSON_FULL_NAME, result.getFullName());
        assertEquals(DEFAULT_PERSON_TITLE, result.getTitle());
        assertEquals(DEFAULT_PERSON_AGE, result.getAge());

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить пользователя с id = null. Должно выбросить ошибку.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPerson_WithNullArgument_Exception_Test() {

        // when
        Throwable throwable = catchThrowable(() -> userRepository.findById(null));

        // then
        assertThat(throwable).isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    // get all

    @DisplayName("Получить всех пользователей. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllBooks_thenAssertDmlCount() {
        // When
        List<Person> allPerson = userRepository.findAll();

        // Then
        assertEquals(DEFAULT_PERSON_COUNT_IN_DB, allPerson.size());

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }

    // delete
    @DisplayName("Удалить пользователя. Число delete должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePerson_thenAssertDmlCount() {
        // When
        userRepository.deleteById(DEFAULT_PERSON_ID);

        // Then
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить пользователя с id, которого нет в БД. Должно выбросить ошибку.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePersonWithIdThatDoesntExist() {
        // When
        Throwable throwable = catchThrowable(() -> userRepository.deleteById(1509L));
        // Then
        assertThat(throwable).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @DisplayName("Удалить пользователя с id = null. Должно выбросить ошибку.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePerson_WithNullArgument_Exception_Test() {
        // When
        Throwable throwable = catchThrowable(() -> userRepository.deleteById(null));

        // Then
        assertThat(throwable).hasRootCauseInstanceOf(IllegalArgumentException.class);

    }
}
