package com.edu.cqupt.shemining.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    public String fileUpload(MultipartFile file) throws IOException;
}
