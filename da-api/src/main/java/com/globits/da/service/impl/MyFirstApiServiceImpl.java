package com.globits.da.service.impl;

import com.globits.da.dto.MyFirstApiDto;
import com.globits.da.service.MyFirstApiService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MyFirstApiServiceImpl implements MyFirstApiService {
    @Override
    public String getMyFirstApi() {
        return "MyFirstApiService";
    }
}
