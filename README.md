# Spring Serverless CRUD API

A serverless REST API built with Spring Boot 4, deployed on AWS Lambda via API Gateway using [`aws-serverless-java-container`](https://github.com/aws/serverless-java-container).

## Features

- Course CRUD API with S3-backed persistence
- S3 file operations (upload, download, list, delete)
- Deployed as a single Lambda function behind API Gateway

## API Endpoints
https://pcpoprrxvj.execute-api.ap-south-1.amazonaws.com/dev
### Courses (`/courses`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/courses` | Create a course |
| GET | `/courses` | List all courses |
| GET | `/courses/{id}` | Get course by ID |
| PUT | `/courses/{id}` | Update a course |
| DELETE | `/courses/{id}` | Delete a course |

### S3 (`/s3`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/s3/upload/{key}` | Upload a file |
| GET | `/s3/download/{key}` | Download a file |
| GET | `/s3/list?prefix=` | List files |
| DELETE | `/s3/delete/{key}` | Delete a file |

## Pre-requisites

- [AWS CLI](https://aws.amazon.com/cli/)
- [SAM CLI](https://github.com/awslabs/aws-sam-cli)
- [Maven](https://maven.apache.org/)
- Java 17+

## Configuration

S3 settings in `src/main/resources/application.properties`:

```properties
aws.s3.bucket=${S3_BUCKET:your-bucket-name}
aws.s3.region=${S3_REGION:ap-south-1}
```

These are overridden by Lambda environment variables defined in `template.yml`.

## Build & Deploy

```bash
# Build
sam build

# Deploy
sam deploy --guided
```

## Testing Locally

```bash
sam local start-api
curl http://127.0.0.1:3000/ping
```

## Example Requests

```bash
# Create a course
curl -X POST https://<api-id>.execute-api.ap-south-1.amazonaws.com/dev/courses \
  -H "Content-Type: application/json" \
  -d '{"id": 1, "name": "Spring Boot", "price": 29.99}'

# Get all courses
curl https://<api-id>.execute-api.ap-south-1.amazonaws.com/dev/courses

# Upload a file to S3
curl -X POST https://<api-id>.execute-api.ap-south-1.amazonaws.com/dev/s3/upload/test.txt \
  -H "Content-Type: application/octet-stream" \
  --data-binary @/path/to/file.txt

# List S3 files
curl https://<api-id>.execute-api.ap-south-1.amazonaws.com/dev/s3/list
```

## Tech Stack

- Spring Boot 4
- AWS Lambda + API Gateway (SAM)
- AWS S3 (SDK v2)
- Jackson for JSON serialization
- Lombok
