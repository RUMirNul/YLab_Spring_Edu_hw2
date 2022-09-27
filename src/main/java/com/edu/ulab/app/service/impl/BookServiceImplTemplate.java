package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.service.BookService;
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
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Create book by book dto.
     * Creating a book and saving it to a database with a unique identifier.
     *
     * @param bookDto data for create book.
     * @return BookDto book dto with ID.
     */
    @Override
    public BookDto createBook(BookDto bookDto) {
        log.info("Got create book by book DTO: {}", bookDto);
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getUserId());

                    return ps;
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Created book: {}", bookDto);

        return bookDto;
    }

    /**
     * Update book by book dto.
     * If the updated book is not in the database, then a new one is created.
     *
     * @param bookDto book dto for update.
     * @return BookDto updated or created book dto.
     */
    @Override
    public BookDto updateBook(BookDto bookDto) {
        log.info("Got update user by book DTO: {}", bookDto);
        final String UPDATE_SQL = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ?, USER_ID = ? WHERE ID = ?";
        final Long bookId = bookDto.getId();

        if (bookId != null && getBookById(bookId) != null) {
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement ps =
                                connection.prepareStatement(UPDATE_SQL);
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getUserId());
                        ps.setLong(5, bookDto.getId());

                        return ps;
                    });
            log.info("Updated book with id: {}", bookId);
            log.info("Updated book data: {}", bookDto);

            return bookDto;
        } else {
            BookDto createdBook = createBook(bookDto);
            log.info("No book with the required id was found. Therefore, a new book was created: {}", createdBook);

            return createdBook;
        }
    }

    /**
     * Getting a book by its ID from database.
     *
     * @param id book ID.
     * @return BookDto if the book is found in the database. null if the book not found in the database.
     */
    @Override
    public BookDto getBookById(Long id) {
        log.info("Wants get book by book id: {}", id);
        final String GET_SQL = "SELECT * FROM BOOK WHERE ID = ?";


        BookDto bookDto = null;
        List<BookDto> books = jdbcTemplate.query(GET_SQL,
                ps -> ps.setLong(1, id),
                (rs, rowNum) -> BookDto.builder()
                        .id(rs.getLong("ID"))
                        .userId(rs.getLong("USER_ID"))
                        .title(rs.getString("TITLE"))
                        .author(rs.getString("AUTHOR"))
                        .pageCount(rs.getInt("PAGE_COUNT"))
                        .build());

        if (!books.isEmpty()) {
            bookDto = books.get(0);
        }

        log.info("Received book: {}", bookDto);

        return bookDto;
    }

    /**
     * Deleting a book from the database by its ID.
     * If there is no book with this ID, then nothing happens.
     *
     * @param id book ID.
     */
    @Override
    public void deleteBookById(Long id) {
        log.info("Got delete book by book id: {}", id);
        final String DELETE_SQL = "DELETE FROM BOOK WHERE ID = ?";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(DELETE_SQL);
            ps.setLong(1, id);

            return ps;
        });

        log.info("Book was deleted with id: {}", id);
    }

    /**
     * Getting a list of IDs of books that belong to a user with userId.
     * SQL query gets a list of book IDs by userId.
     *
     * @param userId user id.
     * @return List<Long> list of user book ids.
     */
    @Override
    public List<Long> findAllBooksIdByUserId(Long userId) {
        log.info("Wants get all books by user id: {}", userId);
        final String GET_ALL_BOOKS_ID_BY_USER_ID_SQL = "SELECT ID FROM BOOK WHERE USER_ID = ?";

        List<Long> allBooksIdByUserId = jdbcTemplate.query(GET_ALL_BOOKS_ID_BY_USER_ID_SQL,
                ps -> ps.setLong(1, userId),
                (rs , rowNum) -> rs.getLong("ID"));

        log.info("Received all books by user id: {}", allBooksIdByUserId);
        return allBooksIdByUserId;
    }
}
