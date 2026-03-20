package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseEvent {
    private CourseAction action;  // "CREATE", "UPDATE", "DELETE"
    private Course course;
}
