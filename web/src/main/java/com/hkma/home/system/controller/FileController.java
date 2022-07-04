package com.hkma.home.system.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileController extends HttpServlet {
	@GetMapping("/getFile")
	ResponseEntity<Resource> getFile() throws IOException {
		File file = new File("C:\\linebot\\jdk-8u291-windows-x64.exe");
		
		HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename="+file.getName());
        //header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        //header.add("Pragma", "no-cache");
        //header.add("Expires", "0");
        
        //Path path = Paths.get(file.getAbsolutePath());
        //ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		
		return ResponseEntity.ok()
		        .headers(header)
		        .contentType(MediaType.parseMediaType("application/octet-stream"))
		        .contentLength(file.length())
		        .body(resource);
    }
}
