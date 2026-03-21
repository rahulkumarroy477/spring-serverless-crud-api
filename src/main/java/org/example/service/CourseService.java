package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private static final String COURSES_KEY = "courses.json";

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ObjectMapper mapper;

    private List<Course> loadFromS3() {
        try {
            byte[] data = s3Service.downloadFile(COURSES_KEY);
            return mapper.readValue(data, new TypeReference<List<Course>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Course> getAll() {
        return loadFromS3();
    }

    public Optional<Course> getById(String id) {
        return loadFromS3().stream().filter(c -> id.equals(c.getId())).findFirst();
    }
}
