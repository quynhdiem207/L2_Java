package com.globits.da.rest;

import com.globits.da.dto.MyFirstApiDto;
import com.globits.da.service.MyFirstApiService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/api/myFirstApi")
public class RestMyFirstApiController {
    @Autowired
    MyFirstApiService myFirstApiService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> getMyFirstApi() {
        String result = myFirstApiService.getMyFirstApi();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MyFirstApiDto> getMyFirstApi(
            String name,
            String code,
            int age
    ) {
        MyFirstApiDto dto = new MyFirstApiDto(name, code, age);
        return new ResponseEntity<MyFirstApiDto>(dto, HttpStatus.OK);
    }

    @PostMapping("/requestbody")
    public ResponseEntity<MyFirstApiDto> getMyFirstApi(@RequestBody MyFirstApiDto dto) {
        return new ResponseEntity<MyFirstApiDto>(dto, HttpStatus.OK);
    }

    @PostMapping("/params")
    public ResponseEntity<MyFirstApiDto> getMyFirstApiParam(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "age", required = false) String age
    ) {
        MyFirstApiDto dto = new MyFirstApiDto(name, code, 0);
        if (age != null) {
            dto.setAge(Integer.parseInt(age));
        }
        return new ResponseEntity<MyFirstApiDto>(dto, HttpStatus.OK);
    }

    @PostMapping("/path/{code}")
    public ResponseEntity<MyFirstApiDto> getMyFirstApiPath(
            @PathVariable String code,
            String name,
            String age
    ) {
        MyFirstApiDto dto = new MyFirstApiDto(name, code, 0);
        if (age != null) {
            dto.setAge(Integer.parseInt(age));
        }
        return new ResponseEntity<MyFirstApiDto>(dto, HttpStatus.OK);
    }

    @PostMapping("/file")
    public ResponseEntity<String> getMyFirstApiFile(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            String result = new String(file.getBytes());
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
