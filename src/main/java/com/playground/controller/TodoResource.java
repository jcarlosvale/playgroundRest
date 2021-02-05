package com.playground.controller;

import com.playground.dto.Todo;
import com.playground.service.TodoHardCodedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="http://localhost:4200")
public class TodoResource {

    private final TodoHardCodedService todoService;

    @GetMapping(path = "/users/{username}/todos")
    public List<Todo> getAllTodos(@PathVariable String username) {
        return todoService.findAll();
    }

    @GetMapping(path = "/users/{username}/todos/{id}")
    public Todo getTodo(@PathVariable String username, @PathVariable long id) {
        return todoService.findById(id);
    }

    @DeleteMapping(path = "/users/{username}/todos/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String username, @PathVariable long id) {
        Todo todo = todoService.deleteById(id);
        if (todo != null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping(path = "/users/{username}/todos/{id}")
    public ResponseEntity<Todo> uodateTodo(@PathVariable String username, @PathVariable long id, @RequestBody Todo todo) {
        return ResponseEntity.ok(todoService.save(todo));
    }

    @PostMapping(path = "/users/{username}/todos")
    public ResponseEntity<Void> postTodo(@PathVariable String username, @RequestBody Todo todo) {

        Todo todoSaved = todoService.save(todo);

        URI uri =
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(todoSaved.getId())
                        .toUri();

        return ResponseEntity.created(uri).build();
    }
}
