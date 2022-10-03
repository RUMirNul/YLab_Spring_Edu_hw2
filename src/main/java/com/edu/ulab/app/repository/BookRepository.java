package com.edu.ulab.app.repository;

import com.edu.ulab.app.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("select b.id from Book b where b.person.id = :userId")
    List<Long> findAllBooksIdByUserId(long userId);
}
