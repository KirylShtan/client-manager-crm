package clientapp.natadataservicemanagement.exception;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

@RestControllerAdvice

public class GlobalExceptionHandlerIssueDetail {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerIssueDetail.class);
    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<ProblemDetail>
    handleClientNotFound(ClientNotFoundException e){
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                e.getMessage()
        );
        problemDetail.setTitle("Client not Found!");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problemDetail);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail>
    handleGeneralError(Exception e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR
                );
        problemDetail.setTitle("Server Issue");
        problemDetail.setDetail("Unpredictable Error , ask administrator for help");
        logger.error("Unhandled exception", e);
        return  ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problemDetail);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationError(MethodArgumentNotValidException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation failed");
        problemDetail.setDetail(e.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", ")));
        return ResponseEntity.badRequest().body(problemDetail);
    }
    @ExceptionHandler(CustomFileNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleFindNotFindExceptionError(CustomFileNotFoundException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("File not found!");
        problemDetail.setDetail("Make sure you inputted right URL");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problemDetail);
    }
    @ExceptionHandler(FileReadException.class)
    public ResponseEntity<ProblemDetail> handleReadExceptionError(FileReadException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Read failed");
        problemDetail.setDetail("Unexpected error, ask administrator for help");
        return  ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problemDetail);
    }
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ProblemDetail> handleFileStorageException(FileStorageException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Storage error or file doesn't exists");
        problemDetail.setDetail("Unpredictable error, ask administrator for help");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(problemDetail);
    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFound(EntityNotFoundException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("Resource not found");
        problemDetail.setDetail(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleBadRequest(IllegalArgumentException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad Request");
        problemDetail.setDetail(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ProblemDetail> handleBadDateParse(DateTimeParseException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Bad Request with date");
        problemDetail.setDetail("Invalid date format. Expected yyyy-MM-dd");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthException(AuthenticationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("Authentication failed");
        pd.setDetail("Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(pd);
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Bad Request");
        pd.setDetail("Date cannot be null or empty");
        logger.warn("Date cannot be null or empty");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(pd);
    }
}
