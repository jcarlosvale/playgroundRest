package com.playground.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Todo {
    private long id;
    private String username;
    private String description;
    private Date targetDate;
    private boolean isDone;
}
