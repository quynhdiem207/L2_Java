package com.globits.da.dto;

public class MyFirstApiDto {
    private String name;
    private String code;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public MyFirstApiDto() {}

    public MyFirstApiDto(String name, String code, int age) {
        this.name = name;
        this.code = code;
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format(
                "{name=\"%s\", code=\"%s\", age=\"%d\"}",
                name, code, age
        );
    }
}
