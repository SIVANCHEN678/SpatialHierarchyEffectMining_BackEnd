package com.edu.cqupt.shemining.controller;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
@Api(tags = "1文件")
@RequestMapping("/api/File")
@RestController
public class FileController {


    @Value("${file.path}")
    private String dirPath;


    private String optFileName;

    private String introFile;
    @RequestMapping("/getOptFile")//doc
    public String getOptFile() throws IOException {
        optFileName= "8.docx";
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(optFileName)
                .toUriString();
        return fileDownloadUri;

    }

    @RequestMapping("/getIntroductionFile")//doc
    public String getIntroductionFile() throws IOException {
        optFileName= "introduction.docx";
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(optFileName)
                .toUriString();
        return fileDownloadUri;

    }
    @RequestMapping( "/getIntroFile")//pdf

    public String getIntroFile() throws IOException {
        introFile= "8.pdf";
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(introFile)
                .toUriString();

        return fileDownloadUri;

    }

}
