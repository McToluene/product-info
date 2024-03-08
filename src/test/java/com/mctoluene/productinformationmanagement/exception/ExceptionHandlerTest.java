package com.mctoluene.productinformationmanagement.exception;

import com.mctoluene.productinformationmanagement.exception.ExceptionHandlers;
import com.mctoluene.productinformationmanagement.exception.ModelNotFoundException;
import com.mctoluene.productinformationmanagement.exception.PageableException;
import com.mctoluene.productinformationmanagement.exception.StockOneException;
import com.mctoluene.productinformationmanagement.exception.UnProcessableEntityException;
import com.mctoluene.productinformationmanagement.exception.ValidatorException;
import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.commons.exceptions.ConflictException;
import com.mctoluene.commons.exceptions.NotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.FileNotFoundException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class ExceptionHandlerTest {

    ExceptionHandlers exceptionHandlers;

    @Mock
    private MessageSourceService messageSourceService;

    AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        exceptionHandlers = new ExceptionHandlers(messageSourceService);
    }

    @Test
    void handleUnProcessableException() {
        String errorMessage = "an error occurred while saving products";
        var appResponse = exceptionHandlers
                .handleUnProcessableException(new UnProcessableEntityException(errorMessage));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(400);
        assertThat(appResponse.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void handleUnProcessableExceptionTest() {
        String errorMessage = "an error occurred while saving products";
        var appResponse = exceptionHandlers.handleUnProcessableException(
                new UnProcessableEntityException(errorMessage, new Exception(errorMessage)));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(400);
        assertThat(appResponse.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void handleModelAlreadyExistException() {
        String errorMessage = "model already exists";
        when(messageSourceService.getMessageByKey("model.already.exists"))
                .thenReturn(errorMessage);
        var appResponse = exceptionHandlers.handleModelAlreadyExistException(new ConflictException(errorMessage));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(appResponse.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void handleNotFoundException() {
        String errorMessage = "product not found";
        var appResponse = exceptionHandlers.handleNotFoundException(new NotFoundException(errorMessage));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(appResponse.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void handleModelNotFoundException() {
        String errorMessage = "Model not found";
        var appResponse = exceptionHandlers.handleModelNotFoundException(new ModelNotFoundException(errorMessage));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(appResponse.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void handleModelNotFoundExceptionTest() {
        String errorMessage = "Model not found";
        var appResponse = exceptionHandlers
                .handleModelNotFoundException(new ModelNotFoundException(errorMessage, new Exception("test")));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(appResponse.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void handleValidatorException() {
        String errorMessage = "Invalid agent phone number";
        var appResponse = exceptionHandlers.handleValidatorException(new ValidatorException(errorMessage));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(appResponse.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void handleValidatorExceptionTest() {
        String errorMessage = "Invalid agent phone number";
        var appResponse = exceptionHandlers
                .handleValidatorException(new ValidatorException(errorMessage, new Exception("invalid")));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(appResponse.getMessage()).isEqualTo(errorMessage);
    }

    @Test
    void handleIllegalArgumentException() {
        var appResponse = exceptionHandlers
                .handleIllegalArgumentException(new IllegalArgumentException("invalid argument"));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(appResponse).isNotNull();
    }

    @Test
    void handlePageableException() {
        var appResponse = exceptionHandlers.handlePageableException(new PageableException("page not found"));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handlePageableExceptionTest() {
        var appResponse = exceptionHandlers
                .handlePageableException(new PageableException("page not found", new Exception("ex")));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleHttpRequestMethodNotSupportedException() {
        String errorMessage = "METHOD_NOT_ALLOWED";
        var appResponse = exceptionHandlers
                .handleHttpRequestMethodNotSupportedException(new HttpRequestMethodNotSupportedException(errorMessage));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @Test
    void handleMethodArgumentTypeMismatchException() {
        var appResponse = exceptionHandlers.handleMethodArgumentTypeMismatchException(
                new MethodArgumentTypeMismatchException(null, null, "test", null, null));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleFileNotFoundException() {
        var appResponse = exceptionHandlers.handleFileNotFoundException(new FileNotFoundException("file not found"));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void handleStockOneException() {
        var appResponse = exceptionHandlers.handleStockOneException(new StockOneException(
                "Quantity to be cancelled is greater than existing quantity", new Exception("test")));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleStockOneExceptionTest() {
        var appResponse = exceptionHandlers.handleStockOneException(
                new StockOneException("Quantity to be cancelled is greater than existing quantity"));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void handleException() {
        var appResponse = exceptionHandlers.handleException(new MethodArgumentNotValidException(null,
                new BeanPropertyBindingResult(null, "null values not allowed")));
        assertThat(appResponse).isNotNull();
        assertThat(appResponse.getStatusCodeValue()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(appResponse.getBody()).isNotNull();
    }

}
