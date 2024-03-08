package com.mctoluene.productinformationmanagement.exception;

import com.mctoluene.productinformationmanagement.service.MessageSourceService;
import com.mctoluene.commons.exceptions.ConflictException;
import com.mctoluene.commons.exceptions.NotFoundException;
import com.mctoluene.commons.response.AppResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.FileNotFoundException;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionHandlers {

    private final MessageSourceService messageSourceService;

    @ExceptionHandler(UnProcessableEntityException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AppResponse handleUnProcessableException(final UnProcessableEntityException ex) {
        log.error("Error occured while processing request; {} ", ex.getMessage());

        return new AppResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
                ex.getMessage(), null, ex.getMessage());
    }

    @ExceptionHandler(DuplicateRecordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AppResponse handleDuplicateRecordException(final DuplicateRecordException ex) {
        log.error("Error occured while processing request; {} ", ex.getMessage());

        return new AppResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
                ex.getMessage(), null, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public AppResponse handleModelAlreadyExistException(final ConflictException ex) {
        log.error("Model Already Exist exception thrown; {} ", ex.getMessage());

        return new AppResponse(HttpStatus.CONFLICT.value(),
                messageSourceService.getMessageByKey("model.already.exists"),
                ex.getMessage(), null, ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public AppResponse handleNotFoundException(final NotFoundException ex) {
        log.error("Not found error: {} ", ex.getMessage());
        return new AppResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
                ex.getMessage(), null, ex.getMessage());
    }

    @ExceptionHandler(ModelNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public AppResponse handleModelNotFoundException(final ModelNotFoundException ex) {
        log.error("Not found error: {} ", ex.getMessage());
        return new AppResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
                ex.getMessage(), null, ex.getMessage());
    }

    @ExceptionHandler(ValidatorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AppResponse handleValidatorException(final ValidatorException ex) {
        log.error("Validation error: {} ", ex.getMessage());
        return new AppResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
                ex.getMessage(), null, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<AppResponse> handleException(MethodArgumentNotValidException e) {
        var errors = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        String message = messageSourceService.getMessageByKey("validation.error");
        var response = new AppResponse(HttpStatus.BAD_REQUEST.value(), message, message, null, errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(PageableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AppResponse handlePageableException(final PageableException ex) {
        log.error("Not found error: {} ", ex.getMessage());
        return new AppResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
                ex.getMessage(), null, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AppResponse handleIllegalArgumentException(final IllegalArgumentException ex) {
        log.error("Bad request error: {} ", ex.getMessage());
        return new AppResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
                ex.getMessage(), null, ex.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ResponseBody
    public AppResponse handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException ex) {
        log.error("Bad request error: {} ", ex.getMessage());
        return new AppResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), ex.getMessage(),
                ex.getMessage(), null,
                ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AppResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex) {
        log.error("Bad request error: {} ", ex.getMessage());
        return new AppResponse(HttpStatus.BAD_REQUEST.value(),
                messageSourceService.getMessageByKey("invalid.identifier"),
                messageSourceService.getMessageByKey("check.id"),
                null,
                messageSourceService.getMessageByKey("invalid.identifier"));
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public AppResponse handleFileNotFoundException(final FileNotFoundException ex) {
        log.error("Not found error: {} ", ex.getMessage());
        return new AppResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(),
                ex.getMessage(), null, ex.getMessage());
    }

    @ExceptionHandler(StockOneException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public AppResponse handleStockOneException(final StockOneException ex) {
        log.error("Stock One error: {} ", ex.getMessage());
        return new AppResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(),
                ex.getMessage(), null, ex.getMessage());
    }

}
