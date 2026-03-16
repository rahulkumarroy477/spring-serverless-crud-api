package org.example.controller;

import org.example.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/upload/{key}")
    public ResponseEntity<String> upload(@PathVariable String key, @RequestBody byte[] content) {
        return ResponseEntity.ok(s3Service.uploadFile(key, content, "application/octet-stream"));
    }

    @GetMapping("/download/{key}")
    public ResponseEntity<byte[]> download(@PathVariable String key) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + key)
                .body(s3Service.downloadFile(key));
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> list(@RequestParam(defaultValue = "") String prefix) {
        return ResponseEntity.ok(s3Service.listFiles(prefix));
    }

    @DeleteMapping("/delete/{key}")
    public ResponseEntity<String> delete(@PathVariable String key) {
        return ResponseEntity.ok(s3Service.deleteFile(key));
    }
}
