package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private static final String COURSES_KEY = "courses.json";

    @Autowired
    private S3Service s3Service;

    private final ObjectMapper mapper = new ObjectMapper();
    private List<Course> courses = new ArrayList<>();

    @PostConstruct
    public void init() {
        try {
            byte[] data = s3Service.downloadFile(COURSES_KEY);
            courses = mapper.readValue(data, new TypeReference<List<Course>>() {});
        } catch (Exception e) {
            courses = new ArrayList<>();
        }
    }

    public Course add(Course course) {
        courses.add(course);
        saveToS3();
        return course;
    }

    public List<Course> getAll() {
        return courses;
    }

    public Optional<Course> getById(String id) {
        return courses.stream().filter(c -> id.equals(c.getId())).findFirst();
    }

    public Optional<Course> update(String id, Course updated) {
        Optional<Course> result = getById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setPrice(updated.getPrice());
            return existing;
        });
        result.ifPresent(c -> saveToS3());
        return result;
    }

    public boolean delete(String id) {
        boolean removed = courses.removeIf(c -> id.equals(c.getId()));
        if (removed) saveToS3();
        return removed;
    }

    private void saveToS3() {
        try {
            byte[] json = mapper.writeValueAsBytes(courses);
            s3Service.uploadFile(COURSES_KEY, json, "application/json");
        } catch (Exception e) {
            throw new RuntimeException("Failed to save courses to S3", e);
        }
    }
}
