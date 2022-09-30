package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.BookDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class BookRowMapper implements RowMapper<BookDto> {
    @Override
    public BookDto mapRow(ResultSet rs, int rowNum) throws SQLException {
        return BookDto.builder()
                .id(rs.getLong("ID"))
                .userId(rs.getLong("USER_ID"))
                .title(rs.getString("TITLE"))
                .author(rs.getString("AUTHOR"))
                .pageCount(rs.getInt("PAGE_COUNT"))
                .build();
    }
}
