package com.example.springbootonetomanybidirectionalrestmysql.api;

import com.example.springbootonetomanybidirectionalrestmysql.jpa.Book;
import com.example.springbootonetomanybidirectionalrestmysql.jpa.Library;
import com.example.springbootonetomanybidirectionalrestmysql.jpa.dto.BookDto;
import com.example.springbootonetomanybidirectionalrestmysql.jpa.mapper.BookMapper;
import com.example.springbootonetomanybidirectionalrestmysql.repository.BookRepository;
import com.example.springbootonetomanybidirectionalrestmysql.repository.LibraryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

//@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookRepository bookRepository;
    private final LibraryRepository libraryRepository;
    @Autowired
    private BookMapper bookMapper;

    @Autowired
    public BookController(BookRepository bookRepository, LibraryRepository libraryRepository) {
        this.bookRepository = bookRepository;
        this.libraryRepository = libraryRepository;
    }

    @GetMapping
    public ResponseEntity<Page<Book>> getAllBook(Pageable pageable){
        return ResponseEntity.ok(bookRepository.findAll(pageable));
    }
    @GetMapping("/{id}")
    public BookDto getById(@PathVariable Integer id)
    {
        BookDto optionalBook = bookMapper.toBookDTO(bookRepository.findById(id).get());
       return optionalBook;
    }
    @PostMapping
    public ResponseEntity<Book> create(@Valid @RequestBody Book book){
        Optional<Library> optionalLibrary = libraryRepository.findById(book.getLibrary().getId());
        if(!optionalLibrary.isPresent()){
            return ResponseEntity.unprocessableEntity().build();
        }
        book.setLibrary(optionalLibrary.get());
        Book bookSaved = bookRepository.save(book);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(bookSaved.getId()).toUri();
        return ResponseEntity.created(location).body(bookSaved);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Integer id, @Valid @RequestBody Book book){
        Optional<Library> optionalLibrary = libraryRepository.findById(book.getLibrary().getId());
        if(!optionalLibrary.isPresent()){
            return ResponseEntity.unprocessableEntity().build();
        }
        Optional<Book> optionalBook = bookRepository.findById(id);
        if(!optionalBook.isPresent()){
            return ResponseEntity.unprocessableEntity().build();
        }
        book.setLibrary(optionalLibrary.get());
        book.setId(optionalBook.get().getId());
        bookRepository.save(book);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        Book optionalBook = bookRepository.findById(id).get();
        if(optionalBook!=null){
            bookRepository.deleteById(id);

            return new ResponseEntity<Book>(optionalBook,HttpStatus.OK);
        }
        return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.NOT_FOUND);
    }



}
