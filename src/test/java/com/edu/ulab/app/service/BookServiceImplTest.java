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
        //given
        long pageCount = RANDOM.nextLong(1, 10000);
        long bookId = RANDOM.nextLong(1, 100000);
        long userId = RANDOM.nextLong(1, 100000);

        Person person = new Person();
        person.setId(userId);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(userId);
        bookDto.setAuthor(BOOK_AUTHOR);
        bookDto.setTitle(BOOK_TITLE);
        bookDto.setPageCount(pageCount);

        BookDto result = new BookDto();
        result.setId(bookId);
        result.setUserId(userId);
        result.setAuthor(BOOK_AUTHOR);
        result.setTitle(BOOK_TITLE);
        result.setPageCount(pageCount);

        Book book = new Book();
        book.setPageCount(1000);
        book.setAuthor(BOOK_AUTHOR);
        book.setTitle(BOOK_TITLE);
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(pageCount);
        savedBook.setPageCount(pageCount);
        savedBook.setTitle(BOOK_TITLE);
        savedBook.setAuthor(BOOK_AUTHOR);
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(userRepository.findById(bookDto.getUserId())).thenReturn(Optional.of(person));
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then
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
        // given
        long pageCount = RANDOM.nextLong(1, 10000);
        long bookId = RANDOM.nextLong(1, 100000);
        long userId = RANDOM.nextLong(1, 100000);

        Person person = new Person();
        person.setId(userId);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(userId);
        bookDto.setAuthor(BOOK_AUTHOR);
        bookDto.setTitle(BOOK_TITLE);
        bookDto.setPageCount(pageCount);

        BookDto result = new BookDto();
        result.setId(bookId);
        result.setUserId(userId);
        result.setAuthor(BOOK_AUTHOR);
        result.setTitle(BOOK_TITLE);
        result.setPageCount(pageCount);


        Book book = new Book();
        book.setId(bookId);
        book.setAuthor(BOOK_AUTHOR);
        book.setTitle(BOOK_TITLE);
        book.setPageCount(pageCount);
        book.setPerson(person);


        Book savedBook = new Book();
        savedBook.setId(bookId);
        savedBook.setAuthor(BOOK_AUTHOR);
        savedBook.setTitle(BOOK_TITLE);
        savedBook.setPageCount(pageCount);
        savedBook.setPerson(person);


        // when
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        // then
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
        // given
        long pageCount = RANDOM.nextLong(1, 10000);
        long bookId = RANDOM.nextLong(1, 100000);
        long userId = RANDOM.nextLong(1, 100000);

        Person person = new Person();
        person.setId(userId);

        Book book = new Book();
        book.setAuthor(BOOK_AUTHOR);
        book.setTitle(BOOK_TITLE);
        book.setPageCount(pageCount);
        book.setPerson(person);

        BookDto result = new BookDto();
        result.setId(bookId);
        result.setUserId(userId);
        result.setAuthor(BOOK_AUTHOR);
        result.setTitle(BOOK_TITLE);
        result.setPageCount(pageCount);

        // when
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(result);

        // then
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
        // given
        long pageCount = RANDOM.nextLong(1, 10000);
        long bookId = RANDOM.nextLong(1, 100000);
        long userId = RANDOM.nextLong(1, 100000);

        Person person = new Person();
        person.setId(userId);

        List<Long> allBooksIdByUserId = new ArrayList<>();

        Book book = new Book();
        book.setAuthor(BOOK_AUTHOR);
        book.setTitle(BOOK_TITLE);
        book.setPageCount(pageCount);
        book.setPerson(person);

        BookDto bookDto = new BookDto();
        bookDto.setId(bookId);
        bookDto.setUserId(userId);
        bookDto.setAuthor(BOOK_AUTHOR);
        bookDto.setTitle(BOOK_TITLE);
        bookDto.setPageCount(pageCount);

        allBooksIdByUserId.add(bookId);

        // when
        when(bookRepository.findAllBooksIdByUserId(userId)).thenReturn(allBooksIdByUserId);
        when(bookMapper.bookToBookDto(book)).thenReturn(bookDto);

        // then
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
        // given
        long bookId = RANDOM.nextLong(1, 100000);

        // when
        bookService.deleteBookById(bookId);

        // then
        verify(bookRepository).deleteById(bookId);
    }

}
