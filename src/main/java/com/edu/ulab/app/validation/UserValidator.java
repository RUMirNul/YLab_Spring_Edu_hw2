package com.edu.ulab.app.validation;

import com.edu.ulab.app.dto.UserDto;
public class UserValidator {

    /**
     * Validations of BookDto fields.
     * Fields fullName and title checked for null and blank, age checked for positive number not exceeding 120.
     * @param userDto object for validation.
     * @return boolean is valid object.
     */
    public static boolean isValidUserData(UserDto userDto) {
        boolean isValidFullName = false;
        boolean isValidTitle = false;
        boolean isValidAge = false;

        if (userDto.getFullName() != null && !userDto.getFullName().isBlank()) {
            isValidFullName = true;
        }

        if (userDto.getTitle() != null && !userDto.getTitle().isBlank()) {
            isValidTitle = true;
        }

        if (userDto.getAge() > 0 && userDto.getAge() < 120) {
            isValidAge = true;
        }

        return isValidFullName && isValidTitle && isValidAge;
    }
}
