package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    /**
     * Create book by book dto.
     * Creating a book and saving it to a database with a unique identifier.
     * @param bookDto data for create book.
     * @return BookDto book dto with ID.
     */
    @Override
    public BookDto createBook(BookDto bookDto) {
        log.info("Got create book by book DTO: {}", bookDto);

        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book DTO to book: {}", book);

        Book savedBook = bookRepository.save(book);
        log.info("Created book: {}", savedBook);

        BookDto returnedBookDto = bookMapper.bookToBookDto(savedBook);
        log.info("Mapped book to book DTO: {}", returnedBookDto);

        return returnedBookDto;
    }

    /**
     * Update book by book dto.
     * If the updated book is not in the database, then a new one is created.
     * @param bookDto book dto for update.
     * @return BookDto updated or created book dto.
     */
    @Override
    public BookDto updateBook(BookDto bookDto) {
        log.info("Got update book by book DTO: {}", bookDto);

        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book DTO to book: {}", book);

        Book updatedBook = bookRepository.save(book);
        log.info("Updated book: {}", updatedBook);

        BookDto returnedBookDto = bookMapper.bookToBookDto(updatedBook);
        log.info("Mapped book to book DTO: {}", returnedBookDto);

        return returnedBookDto;
    }

    /**
     * Getting a book by its ID from database.
     * @param id book ID.
     * @return BookDto if the book is found in the database. null if the book not found in the database.
     */
    @Override
    public BookDto getBookById(Long id) {
        log.info("Wants get book by book id: {}", id);

        Book receivedBook = bookRepository.findById(id).orElse(null);
        log.info("Received book: {}", receivedBook);

        BookDto returnedBookDto = bookMapper.bookToBookDto(receivedBook);
        log.info("Mapped book to book DTO: {}", returnedBookDto);

        return returnedBookDto;
    }

    /**
     * Deleting a book from the database by its ID.
     * If there is no book with this ID, then nothing happens.
     * @param id book ID.
     */
    @Override
    public void deleteBookById(Long id) {
        log.info("Got delete book by book id: {}", id);
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            log.info("Book was deleted with id: {}", id);
        }
    }

    /**
     * Getting a list of IDs of books that belong to a user with userId.
     * @param userId user id.
     * @return List<Long> list of user book ids.
     */
    //TODO исправить
    @Override
    public List<Long> findAllBooksIdByUserId(Long userId) {
        log.info("Wants get all books by user id: {}", userId);

        List<Long> allBooksIdByUserId = bookRepository.findAllBooksIdByUserId(userId);
        log.info("Received all books by user id: {}", allBooksIdByUserId);

        return allBooksIdByUserId;
    }

}
