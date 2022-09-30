package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.UserDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRowMapper implements RowMapper<UserDto> {
    @Override
    public UserDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UserDto.builder()
                .id(rs.getLong("ID"))
                .fullName(rs.getString("FULL_NAME"))
                .title(rs.getString("TITLE"))
                .age(rs.getInt("AGE"))
                .build();
    }
}
