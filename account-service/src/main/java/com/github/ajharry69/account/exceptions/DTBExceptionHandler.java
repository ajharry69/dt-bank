package com.github.ajharry69.account.exceptions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class DTBExceptionHandler {
    private final ObjectMapper objectMapper;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Problem> handleException(Exception exception, HttpServletRequest request) {
        log.error(exception.getMessage(), exception);

        Map<String, Object> payload = new HashMap<>();
        payload.put("timestamp", Instant.now());
        payload.put("errorCode", "SERVER_ERROR");

        Problem.ExtendedProblem<Map<String, Object>> problem = Problem.create()
                .withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .withProperties(payload)
                .withDetail(exception.getMessage())
                .withInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(problem);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Problem> handle(FeignException exception) {
        log.info("Processing FeignException: {}", exception.getMessage());
        HttpStatus httpStatus = HttpStatus.valueOf(exception.status());

        Map<String, Object> payload = new HashMap<>();
        payload.put("timestamp", Instant.now());
        payload.put("errorCode", "INTERNAL_SERVICE_ERROR");

        if (exception.responseBody().isPresent()) {
            ByteBuffer buffer = exception.responseBody().get();
            String jsonString = StandardCharsets.UTF_8.decode(buffer).toString();
            try {
                payload = objectMapper.readValue(jsonString, new TypeReference<>() {
                });
            } catch (IOException e) {
                log.warn("Failed to parse FeignException response body: {}", jsonString, e);
            }
        }
        log.info("Processed FeignException: {}", exception.getMessage());

        Problem.ExtendedProblem<Map<String, Object>> problem = Problem.create()
                .withStatus(httpStatus)
                .withProperties(payload);
        return ResponseEntity.status(httpStatus)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(problem);
    }

    @ResponseBody
    @ExceptionHandler(DTBException.class)
    public ResponseEntity<Problem> handle(DTBException exception, HttpServletRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("timestamp", Instant.now());
        payload.put("errorCode", exception.getErrorCode());

        Problem.ExtendedProblem<Map<String, Object>> problem = Problem.create()
                .withStatus(exception.getHttpStatus())
                .withProperties(payload)
                .withDetail(exception.getMessage())
                .withInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(exception.getHttpStatus())
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(problem);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Problem> handle(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, List<FieldError>> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldError::getField));

        Map<String, Object> payload = new HashMap<>();
        payload.put("timestamp", Instant.now());
        payload.put("errorCode", "VALIDATION_ERROR");
        payload.put("fieldErrors", fieldErrors);

        Problem.ExtendedProblem<Map<String, Object>> problem = Problem.create()
                .withStatus(HttpStatus.BAD_REQUEST)
                .withProperties(payload)
                .withDetail(exception.getMessage())
                .withInstance(URI.create(request.getRequestURI()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(problem);
    }
}
