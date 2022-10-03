package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    private final static Random RANDOM = new Random();
    private final static String BOOK_AUTHOR = "test author";
    private final static String BOOK_TITLE = "test title";
    private final static String USER_FULL_NAME = "Test Test";
    private final static String USER_TITLE = "reader";
    private final static Long DEFAULT_BOOK_ID = 2002L;
    private final static String DEFAULT_BOOK_TITLE = "default book";
    private final static String DEFAULT_BOOK_AUTHOR = "author";
    private final static long DEFAULT_BOOK_PAGE_COUNT = 5500L;
    private final static int DEFAULT_BOOK_COUNT_IN_DB = 2;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу и автора. Число select должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void findAllBadges_thenAssertDmlCount() {
        //Given
        int userAge = RANDOM.nextInt(1, 100);
        long bookPageCount = RANDOM.nextLong(1, 1000);

        Person person = new Person();
        person.setFullName(USER_FULL_NAME);
        person.setTitle(USER_TITLE);
        person.setAge(userAge);


        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor(BOOK_AUTHOR);
        book.setTitle(BOOK_TITLE);
        book.setPageCount(bookPageCount);
        book.setPerson(savedPerson);

        //When
        Book result = bookRepository.save(book);

        //Then
        assertNotNull(result.getId());
        assertEquals(BOOK_AUTHOR, result.getAuthor());
        assertEquals(BOOK_TITLE, result.getTitle());
        assertEquals(bookPageCount, result.getPageCount());

        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    // update
    @DisplayName("Обновить книгу. Число update должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBook_thenAssertDmlCount() {
        // Given
        long bookPageCount = RANDOM.nextLong(1, 1000);
        Book book = new Book();
        book.setId(DEFAULT_BOOK_ID);
        book.setAuthor(BOOK_AUTHOR);
        book.setTitle(BOOK_TITLE);
        book.setPageCount(bookPageCount);

        // When
        Book result = bookRepository.save(book);

        // Then
        assertEquals(DEFAULT_BOOK_ID, result.getId());
        assertEquals(BOOK_AUTHOR, result.getAuthor());
        assertEquals(BOOK_TITLE, result.getTitle());
        assertEquals(bookPageCount, result.getPageCount());

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }

    @DisplayName("Создать/Обновить книгу c Book = null. Должно выбросить ошибку.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBook_WithNullArgument_Exception_Test() {
        // when
        Throwable throwable = catchThrowable(() -> bookRepository.save(null));

        // then
        assertThat(throwable).hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    // get
    @DisplayName("Получить книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBook_thenAssertDmlCount() {
        // When
        Book result = bookRepository.findById(DEFAULT_BOOK_ID).orElseThrow();

        // Then
        assertEquals(DEFAULT_BOOK_ID, result.getId());
        assertEquals(DEFAULT_BOOK_AUTHOR, result.getAuthor());
        assertEquals(DEFAULT_BOOK_TITLE, result.getTitle());
        assertEquals(DEFAULT_BOOK_PAGE_COUNT, result.getPageCount());

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить книгу с id = null. Должно выбросить ошибку.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBookById_WithNullArgument_Exception_Test() {
        // when
        Throwable throwable = catchThrowable(() -> bookRepository.findById(null));

        // then
        assertThat(throwable).hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    // get all
    @DisplayName("Получить все книги. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllBooks_thenAssertDmlCount() {
        // When
        List<Book> allBooks = bookRepository.findAll();

        // Then
        assertEquals(DEFAULT_BOOK_COUNT_IN_DB, allBooks.size());

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }

    // delete
    @DisplayName("Удалить книгу. Число delete должно равняться 1.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBook_thenAssertDmlCount() {
        // When
        bookRepository.deleteById(DEFAULT_BOOK_ID);

        // Then

        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    // * failed
    @DisplayName("Удалить книгу с id = null. Должно выбросить ошибку.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBook_WithNullArgument_Exception_Test() {
        // when
        Throwable throwable = catchThrowable(() -> bookRepository.deleteById(null));

        // then
        assertThat(throwable).hasRootCauseInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Удалить книгу с id, которого нет в БД. Должно выбросить ошибку.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBook_WithNonExistId_Exception_Test() {
        // when
        Throwable throwable = catchThrowable(() -> bookRepository.deleteById(1509L));

        // then
        assertThat(throwable).isInstanceOf(EmptyResultDataAccessException.class);

    }
}
