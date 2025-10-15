package com.template21.frame.exception;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.exception.BizException;
import com.template21.frame.constant.NumberConstants;
import com.template21.frame.constant.StrConstants;
import com.template21.frame.response.ResponseBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 全局异常处理
 *
 * @author qmf
 */
@Slf4j
@RestControllerAdvice
@Order
public class GlobalExceptionHandler {

    public static final String SYSTEM_ERROR = "SYSTEM_ERROR";

    @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
    public Response handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("不支持的请求方式!", e);
        return ResponseBuilder.failure(SYSTEM_ERROR, e.getMethod() + " Request not supported");
    }

    @ExceptionHandler(value = {BizException.class})
    public Response handleBizException(BizException e) {
        log.warn("业务异常：", e);
        if ("BIZ_ERROR".equals(e.getErrCode())) {
            return ResponseBuilder.failure(SYSTEM_ERROR, e.getMessage());
        }
        return ResponseBuilder.failure(e.getErrCode(), e.getMessage());
    }

    @ExceptionHandler(value = {UnsupportedOperationException.class})
    public Response handleUnsupportedOperationException(UnsupportedOperationException e) {
        log.error("系统发生异常", e);
        return ResponseBuilder.failure(SYSTEM_ERROR, e.getMessage());
    }

    @ExceptionHandler(value = {Exception.class})
    public Response handleException(Exception e) {
        log.error("系统发生异常", e);
        return ResponseBuilder.failure(SYSTEM_ERROR, e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Response constraintViolationException(ConstraintViolationException ex) throws IOException {
        List<Pair<String, String>> errors = CollUtil.newArrayList();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fieldPath = violation.getPropertyPath().toString();
            String fieldName = getLastFieldName(fieldPath);
            String message = violation.getMessage();
            errors.add(Pair.of(fieldName, message));
        }
        Class<?> beanClass = ex.getConstraintViolations().iterator().next().getRootBean().getClass();
        Field[] declaredFields = beanClass.getDeclaredFields();
        List<String> fieldOrder = Arrays.stream(declaredFields).map(Field::getName).toList();
        List<Pair<String, String>> sortedErrors = errors.stream().sorted((e1, e2) -> {
            int result = fieldOrder.indexOf(e1.getKey()) - fieldOrder.indexOf(e2.getKey());
            if (result == NumberConstants.ZERO) {
                return e1.getValue().compareTo(e2.getValue());
            }
            return result;
        }).toList();
        Pair<String, String> firstError = sortedErrors.getFirst();
        return ResponseBuilder.failure(SYSTEM_ERROR, "[" + firstError.getKey() + "]" + StrConstants.SPACE + firstError.getValue());
    }

    private String getLastFieldName(String propertyPath) {
        if (propertyPath == null || propertyPath.isEmpty()) {
            return propertyPath;
        }
        String[] parts = propertyPath.split("\\.");
        return parts[parts.length - 1];
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response methodArgumentNotValidException(MethodArgumentNotValidException ex) throws IOException {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Field[] declaredFields = Objects.requireNonNull(ex.getBindingResult().getTarget()).getClass().getDeclaredFields();
        List<String> fieldOrder = Arrays.stream(declaredFields).map(Field::getName).toList();
        List<FieldError> sortedErrors = fieldErrors.stream().sorted((e1, e2) -> {
            int result = fieldOrder.indexOf(e1.getField()) - fieldOrder.indexOf(e2.getField());
            if (result == NumberConstants.ZERO) {
                return Objects.requireNonNull(e1.getDefaultMessage()).compareTo(Objects.requireNonNull(e2.getDefaultMessage()));
            }
            return result;
        }).toList();
        FieldError firstError = sortedErrors.getFirst();
        String field = firstError.getField();
        String message = firstError.getDefaultMessage();
        return ResponseBuilder.failure(SYSTEM_ERROR, "[" + field + "]" + StrConstants.SPACE + message);
    }
}
