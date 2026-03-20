package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.Course;
import org.example.dto.CourseAction;
import org.example.dto.CourseEvent;
import org.example.service.CourseService;
import org.example.service.SqsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CourseService courseService;

    @Autowired
    private SqsService sqsService;

    @PostMapping
    public ResponseEntity<String> addCourse(@Valid @RequestBody Course course) throws Exception{
        sqsService.sendMessage(objectMapper.writeValueAsString(new CourseEvent(CourseAction.CREATE, course)));
        return ResponseEntity.accepted().body("Course created successfully");
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable String id) {
        return courseService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateCourse(@PathVariable String id, @Valid @RequestBody Course course) throws Exception{
        course.setId(id);
        sqsService.sendMessage(objectMapper.writeValueAsString(new CourseEvent(CourseAction.UPDATE, course)));
        return ResponseEntity.accepted().body("Course updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable String id) throws Exception{
        Course course = new Course();
        course.setId(id);
        sqsService.sendMessage(objectMapper.writeValueAsString(new CourseEvent(CourseAction.DELETE, course)));
        return ResponseEntity.accepted().body("Course deleted successfully");
    }
}
