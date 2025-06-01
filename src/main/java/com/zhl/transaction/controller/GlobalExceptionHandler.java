package com.zhl.transaction.controller;

import com.zhl.transaction.controller.vo.ApiResponse;
import com.zhl.transaction.exception.TransactionException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionException.class)
    public ApiResponse handleTransactionNotFoundException(TransactionException exception){
        return ApiResponse.fail(exception.getMessage());
    }

    // 处理 @Validated 校验不通过抛出的 MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse methodArgumentNotValidException(MethodArgumentNotValidException exception,HttpServletRequest request){
        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder errorMsg = new StringBuilder();
        String acceptHeader = request.getHeader("Accept");

        for (FieldError error : fieldErrors) {
            errorMsg.append(error.getField()).append(":").append(error.getDefaultMessage()).append(";");
        }

        return ApiResponse.fail(errorMsg.toString());
    }


    // 处理 @Validated 校验不通过抛出的 ConstraintViolationException（方法参数校验）
    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResponse handleConstraintViolation(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        StringBuilder errorMsg = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            errorMsg.append(constraintViolation.getPropertyPath()).append(":").append(constraintViolation.getMessage()).append(";");

        }
        return ApiResponse.fail(errorMsg.toString());
    }
}
