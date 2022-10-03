package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;
    @Mock
    BookRepository bookRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookMapper bookMapper;

    private final static Random RANDOM = new Random();
    private final static String BOOK_AUTHOR = "test author";
    private final static String BOOK_TITLE = "test title";

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //Given
        long pageCount = RANDOM.nextLong(1, 10000);
        long bookId = RANDOM.nextLong(1, 100000);
        long userId = RANDOM.nextLong(1, 100000);

        Person person = Person.builder().id(userId).build();

        BookDto bookDto = BookDto.builder()
                .userId(userId)
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .build();

        BookDto result = BookDto.builder()
                .id(bookId)
                .userId(userId)
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .build();

        Book book = Book.builder()
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .person(person)
                .build();

        Book savedBook = Book.builder()
                .id(bookId)
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .person(person)
                .build();

        //When
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(userRepository.findById(bookDto.getUserId())).thenReturn(Optional.of(person));
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //Then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(bookId, bookDtoResult.getId());
        assertEquals(userId, bookDtoResult.getUserId());
        assertEquals(BOOK_TITLE, bookDtoResult.getTitle());
        assertEquals(BOOK_AUTHOR, bookDtoResult.getAuthor());
        assertEquals(pageCount, bookDtoResult.getPageCount());
    }

    @Test
    @DisplayName("Создание книги c bookDto = null. Должно выбросить ошибку.")
    void saveBook_WithNullArgument_Exception_Test() {
        assertThatThrownBy(() -> bookService.createBook(null))
                .isInstanceOf(NullPointerException.class);
    }

    // update
    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {
        //Given
        long pageCount = RANDOM.nextLong(1, 10000);
        long bookId = RANDOM.nextLong(1, 100000);
        long userId = RANDOM.nextLong(1, 100000);

        Person person = Person.builder().id(userId).build();

        BookDto bookDto = BookDto.builder()
                .userId(userId)
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .build();

        BookDto result = BookDto.builder()
                .id(bookId)
                .userId(userId)
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .build();


        Book book = Book.builder()
                .id(bookId)
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .person(person)
                .build();


        Book savedBook = Book.builder()
                .id(bookId)
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .person(person)
                .build();


        //When
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        //Then
        BookDto bookDtoResult = bookService.updateBook(bookDto);
        assertEquals(bookId, bookDtoResult.getId());
        assertEquals(userId, bookDtoResult.getUserId());
        assertEquals(BOOK_TITLE, bookDtoResult.getTitle());
        assertEquals(BOOK_AUTHOR, bookDtoResult.getAuthor());
        assertEquals(pageCount, bookDtoResult.getPageCount());
    }

    // get
    @Test
    @DisplayName("Получение книги. Должно пройти успешно.")
    void getBookById_Test() {
        //Given
        long pageCount = RANDOM.nextLong(1, 10000);
        long bookId = RANDOM.nextLong(1, 100000);
        long userId = RANDOM.nextLong(1, 100000);

        Person person = new Person();
        person.setId(userId);

        Book book = Book.builder()
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .person(person)
                .build();

        BookDto result = BookDto.builder()
                .id(bookId)
                .userId(userId)
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .build();

        //When
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(result);

        //Then
        BookDto bookDtoResult = bookService.getBookById(bookId);
        assertEquals(bookId, bookDtoResult.getId());
        assertEquals(userId, bookDtoResult.getUserId());
        assertEquals(BOOK_TITLE, bookDtoResult.getTitle());
        assertEquals(BOOK_AUTHOR, bookDtoResult.getAuthor());
        assertEquals(pageCount, bookDtoResult.getPageCount());
    }

    @Test
    @DisplayName("Получение книги c id = null . Должно выбросить ошибку.")
    void getBookById_WithNullArgument_Exception_Test() {
        assertThatThrownBy(() -> bookService.getBookById(null))
                .isInstanceOf(NotFoundException.class);
    }

    // get all
    @Test
    @DisplayName("Получение всех id книг с нужным userId. Должно пройти успешно.")
    void getAllBooksIdByUserId_Test() {
        //Given
        long pageCount = RANDOM.nextLong(1, 10000);
        long bookId = RANDOM.nextLong(1, 100000);
        long userId = RANDOM.nextLong(1, 100000);

        Person person = Person.builder().id(userId).build();

        List<Long> allBooksIdByUserId = new ArrayList<>();

        Book book = Book.builder()
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .person(person)
                .build();

        BookDto bookDto = BookDto.builder()
                .id(bookId)
                .userId(userId)
                .author(BOOK_AUTHOR)
                .title(BOOK_TITLE)
                .pageCount(pageCount)
                .build();

        allBooksIdByUserId.add(bookId);

        //When
        when(bookRepository.findAllBooksIdByUserId(userId)).thenReturn(allBooksIdByUserId);
        when(bookMapper.bookToBookDto(book)).thenReturn(bookDto);

        //Then
        assertThat(bookService.findAllBooksIdByUserId(userId)).isEqualTo(allBooksIdByUserId);
    }

    @Test
    @DisplayName("Получение книги c userId = null . Должно выбросить ошибку.")
    void getAllBooksIdByUserId_WithNullArgument_Exception_Test() {
        assertThatThrownBy(() -> bookService.findAllBooksIdByUserId(null))
                .isInstanceOf(NullPointerException.class);
    }


    // delete
    @Test
    @DisplayName("Удаление книги. Должно пройти успешно.")
    void deleteBook_Test() {
        //Given
        long bookId = RANDOM.nextLong(1, 100000);

        //When
        bookService.deleteBookById(bookId);

        //Then
        verify(bookRepository).deleteById(bookId);
    }

}
