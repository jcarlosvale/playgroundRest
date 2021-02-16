package com.playground.service;

import com.playground.dto.Todo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class TodoHardCodedService {
    private static List<Todo> todos = new ArrayList<>();
    private static int idCounter = 0;

    @Value("classpath:queries/feature-list-retrieve-all-by-client-id.sql")
    private Resource resource;

    static {
        todos.add(new Todo(++idCounter, "in28minutess", "Learn to Dance", new Date(), false));
        todos.add(new Todo(++idCounter, "in28minutess", "Learn about Microservices", new Date(), false));
        todos.add(new Todo(++idCounter, "in28minutess", "Learn about Angular", new Date(), false));
    }

    public List<Todo> findAll(){
        return todos;
    }

    public Todo save(Todo todo) {
        if(todo.getId() <= 0) {
            todo.setId(++idCounter);
        } else {
            deleteById(todo.getId());
        }
        todos.add(todo);
        return todo;
    }
    public Todo deleteById(long id) {
        Todo todo = findById(id);
        if (todo == null){
            return null;
        }
        if (todos.remove(todo)) {
            return todo;
        }
        return null;
    }

    public Todo findById(long id) {
        for (Todo todo : todos) {
            if (todo.getId() == id) {
                return todo;
            }
        }
        return null;
    }

    public String getSQL() {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
