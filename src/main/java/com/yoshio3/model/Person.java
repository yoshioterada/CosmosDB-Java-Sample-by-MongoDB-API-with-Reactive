package com.yoshio3.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Person {
    private String id;
    private String firstName;
    private String lastName;
    private int age;
}