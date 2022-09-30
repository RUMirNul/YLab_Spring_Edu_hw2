package com.edu.ulab.app.validation;

import com.edu.ulab.app.dto.BookDto;

public class BookValidator {
    /**
     * Validations of BookDto fields.
     * Fields title and author checked for null and blank, pageCount checked for positive number.
     * @param bookDto object for validation.
     * @return boolean is valid object.
     */
    public static boolean isValidBookData(BookDto bookDto) {
        boolean isValidTitle = false;
        boolean isValidAuthor = false;
        boolean isValidPageCount = bookDto.getPageCount() > 0;

        if (bookDto.getTitle() != null && !bookDto.getTitle().isBlank()) {
            isValidTitle = true;
        }

        if (bookDto.getAuthor() != null && !bookDto.getAuthor().isBlank()) {
            isValidAuthor = true;
        }

        return isValidTitle && isValidAuthor && isValidPageCount;
    }
}
