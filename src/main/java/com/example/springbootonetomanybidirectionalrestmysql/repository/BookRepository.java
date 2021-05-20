package com.example.springbootonetomanybidirectionalrestmysql.repository;

import com.example.springbootonetomanybidirectionalrestmysql.jpa.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book,Integer> {
}
