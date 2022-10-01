package com.edu.ulab.app.facade;

import com.edu.ulab.app.constant.ErrorMessageTextConstants;
import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.InvalidRequestDataException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.validation.BookValidator;
import com.edu.ulab.app.validation.UserValidator;
import com.edu.ulab.app.web.request.BookRequest;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImpl userService;
    private final BookServiceImpl bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImpl userService,
                          BookServiceImpl bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    /**
     * Create user and his book from UserBookRequest.
     * The user and his books are created based on the request and stored in a database with a unique identifier.
     * @param userBookRequest request with data of user and his books.
     * @return UserBookResponse user id and his books id.
     * @throws InvalidRequestDataException if incorrect or null of user or book data from userBookRequest.
     */
    @Transactional
    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);

        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);
        if (userDto == null) throw new InvalidRequestDataException(ErrorMessageTextConstants.USER_CAN_NOT_BE_NULL);
        if (!UserValidator.isValidUserData(userDto)) throw new InvalidRequestDataException(ErrorMessageTextConstants.INCORRECT_USER_DATA);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<BookRequest> bookRequest = userBookRequest.getBookRequests();
        if (bookRequest == null) throw new InvalidRequestDataException(ErrorMessageTextConstants.BOOK_LIST_CAN_NOT_BE_NULL);

        List<Long> bookIdList = bookRequest
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .filter(BookValidator::isValidBookData)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("Mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    /**
     * Update user and his book from UserBookRequest.
     * The user and his books are update based on the request and update in a database.
     * if a user with such an identifier is not in the database,
     * then a new user and his books with unique identifiers are created based on the request.
     * @param userBookRequest request with data of user and his books.
     * @param userId user ID for updating information about him and his books.
     * @return UserBookResponse user ID and his books ID.
     * @throws InvalidRequestDataException if incorrect or null of user or book data from userBookRequest.
     */
    @Transactional
    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest, Long userId) {
        log.info("Got user book update request: {}, userId = {}", userBookRequest, userId);
        if (userId == null) throw new InvalidRequestDataException(ErrorMessageTextConstants.USER_CAN_NOT_BE_NULL);

        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);
        if (userDto == null) throw new InvalidRequestDataException(ErrorMessageTextConstants.USER_CAN_NOT_BE_NULL);
        if (!UserValidator.isValidUserData(userDto)) throw new InvalidRequestDataException(ErrorMessageTextConstants.INCORRECT_USER_DATA);

        List<BookRequest> bookRequest = userBookRequest.getBookRequests();
        if (bookRequest == null) throw new InvalidRequestDataException(ErrorMessageTextConstants.BOOK_LIST_CAN_NOT_BE_NULL);

        userDto.setId(userId);
        UserDto updatedUser = userService.updateUser(userDto);
        log.info("Updated user: {}", updatedUser);

        List<Long> forDeleteBooksIdByUserId = bookService.findAllBooksIdByUserId(updatedUser.getId());
        log.info("Received all books for delete by user id: {}", forDeleteBooksIdByUserId);
        forDeleteBooksIdByUserId.stream()
                .filter(Objects::nonNull)
                .forEach(bookService::deleteBookById);

        bookRequest.stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .filter(BookValidator::isValidBookData)
                .peek(bookDto -> bookDto.setUserId(updatedUser.getId()))
                .peek(mappedBookDto -> log.info("Mapped book: {}", mappedBookDto))
                .forEach(bookService::createBook);

        List<Long> allBooksIdByUserId = bookService.findAllBooksIdByUserId(updatedUser.getId());
        log.info("Received all books by user id: {}", allBooksIdByUserId);

        return UserBookResponse.builder()
                .userId(updatedUser.getId())
                .booksIdList(allBooksIdByUserId)
                .build();
    }

    /**
     * Getting a user and his books by ID.
     * Getting from database the user ID and the list of ID's of his books upon request of the user ID.
     * @param userId user ID for getting information about him.
     * @return UserBookResponse user ID and his books ID.
     * @throws InvalidRequestDataException if incorrect or null of user ID.
     * @throws NotFoundException if the user with this ID is not in the database.
     */
    @Transactional
    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Got user book get request with userId: {}", userId);
        if (userId == null) throw new InvalidRequestDataException(ErrorMessageTextConstants.USER_ID_CAN_NOT_BE_NULL);

        UserDto user = userService.getUserById(userId);
        log.info("Got user: {}", user);
        if (user == null) throw new NotFoundException("No have user with id: " + userId);

        List<Long> allBooksIdByUserId = bookService.findAllBooksIdByUserId(user.getId());
        log.info("Received all books by user id: {}", allBooksIdByUserId);

        return UserBookResponse.builder()
                .userId(user.getId())
                .booksIdList(allBooksIdByUserId)
                .build();
    }

    /**
     * Deleting a user and his list of books
     * Deleting a user and his list of books from the database by user ID.
     * If there is no user with this ID, then nothing happens.
     * @param userId user ID for deleting information about him.
     */
    @Transactional
    public void deleteUserWithBooks(Long userId) {
        log.info("Got user book delete request with user id: {}", userId);

        if (userId != null) {
            List<Long> allBooksIdByUserId = bookService.findAllBooksIdByUserId(userId);
            log.info("Book id list for delete: {}", allBooksIdByUserId);

            userService.deleteUserById(userId);
            log.info("Deleted user with id: {}", userId);

            if (allBooksIdByUserId != null && !allBooksIdByUserId.isEmpty()) {
                allBooksIdByUserId.stream()
                        .filter(Objects::nonNull)
                        .forEach(bookService::deleteBookById);
            }
        }

    }
}
