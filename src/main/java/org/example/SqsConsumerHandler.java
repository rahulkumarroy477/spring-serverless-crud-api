package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.Course;
import org.example.dto.CourseEvent;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SqsConsumerHandler implements RequestHandler<SQSEvent, Void> {

    private static final String COURSES_KEY = "courses.json";
    private final ObjectMapper mapper = new ObjectMapper();
    private final String bucket = System.getenv("S3_BUCKET");
    private final S3Client s3 = S3Client.builder()
            .region(Region.of(System.getenv("S3_REGION")))
            .build();

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        context.getLogger().log("Received " + event.getRecords().size() + " messages from SQS");
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            try {
                CourseEvent courseEvent = mapper.readValue(msg.getBody(), CourseEvent.class);
                context.getLogger().log("Processing " + courseEvent.getAction() + " event");
                List<Course> courses = loadCourses();
                context.getLogger().log("Loaded " + courses.size() + " existing courses from S3");

                switch (courseEvent.getAction()) {
                    case CREATE -> {
                        courseEvent.getCourse().setId(UUID.randomUUID().toString());
                        courses.add(courseEvent.getCourse());
                        context.getLogger().log("Created course: " + courseEvent.getCourse().getId());
                    }
                    case UPDATE -> courses.stream()
                            .filter(c -> courseEvent.getCourse().getId().equals(c.getId()))
                            .findFirst()
                            .ifPresentOrElse(
                                c -> {
                                    c.setName(courseEvent.getCourse().getName());
                                    c.setPrice(courseEvent.getCourse().getPrice());
                                    context.getLogger().log("Updated course: " + c.getId());
                                },
                                () -> context.getLogger().log("Course not found for update: " + courseEvent.getCourse().getId())
                            );
                    case DELETE -> {
                        boolean removed = courses.removeIf(c -> courseEvent.getCourse().getId().equals(c.getId()));
                        context.getLogger().log(removed ? "Deleted course: " + courseEvent.getCourse().getId() : "Course not found for delete: " + courseEvent.getCourse().getId());
                    }
                }

                saveCourses(courses);
                context.getLogger().log("Saved " + courses.size() + " courses to S3");
            } catch (Exception e) {
                context.getLogger().log("Error processing message: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private List<Course> loadCourses() {
        try {
            byte[] data = s3.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucket).key(COURSES_KEY).build()).asByteArray();
            return new ArrayList<>(mapper.readValue(data, new TypeReference<>() {}));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveCourses(List<Course> courses) {
        try {
            s3.putObject(PutObjectRequest.builder()
                    .bucket(bucket).key(COURSES_KEY).contentType("application/json")
                    .build(), RequestBody.fromBytes(mapper.writeValueAsBytes(courses)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to save courses", e);
        }
    }
}
