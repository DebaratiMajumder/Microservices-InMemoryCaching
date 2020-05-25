package com.jdgroup.weather.controller;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.jdgroup.weather.exceptions.ServiceException;
import com.jdgroup.weather.models.ErrorResponse;
import com.netflix.hystrix.exception.HystrixBadRequestException;

@ControllerAdvice
public class WeatherServicesExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WeatherServicesExceptionHandler.class);
	
	ObjectReader objectReader;
	
	@PostConstruct
	public void init()
	{
		objectReader = new ObjectMapper().readerFor(ErrorResponse.class);
	}
	
    @ExceptionHandler(value = { ServiceException.class, HystrixBadRequestException.class })
    public ResponseEntity<ErrorResponse> serviceExceptionHandler(HttpServletRequest req, Exception e)
    {
        ServiceException serviceException = null;
        if (e instanceof HystrixBadRequestException)
        {
            ErrorResponse errorResponse = new ErrorResponse();
            try
            {
            	errorResponse.setMoreInfo(e.getMessage());
            	errorResponse.setType("Failure");
            	errorResponse.setCode("-1");
            	errorResponse.setDetails("Bad Request");
                serviceException = new ServiceException(errorResponse, HttpStatus.BAD_REQUEST);
            }
            catch (Exception ioeException)
            {
                LOGGER.error("Exception: ", ioeException);
                errorResponse.setCode("FATAL");
                errorResponse.setDetails("FATAL");
                errorResponse.setType("FATAL");
                serviceException = new ServiceException(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        else
        {
            serviceException = (ServiceException) e;
        }
        return new ResponseEntity<>(serviceException.getErrorResponse(), serviceException.getStatus());
    }
    
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(HttpServletRequest req, Exception exception)
    {
        MethodArgumentNotValidException e = (MethodArgumentNotValidException) exception;
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode("BAD_REQUEST");
        errorResponse.setType("INVALID");
        errorResponse.setDetails("BAD_REQUEST");
        String error = errorResponse != null ? errorResponse.toString() : null;
        String detailedErrorMessage = e.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst().orElse(exception.getMessage());
        LOGGER.error("Error and error message: ", "{} | {}", error, detailedErrorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
