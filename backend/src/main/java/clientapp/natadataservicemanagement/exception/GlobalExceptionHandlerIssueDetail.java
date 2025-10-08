package clientapp.natadataservicemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice

public class GlobalExceptionHandlerIssueDetail {
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
}
