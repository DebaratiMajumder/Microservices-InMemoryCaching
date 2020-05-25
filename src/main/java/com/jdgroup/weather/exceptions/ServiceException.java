package com.jdgroup.weather.exceptions;

import org.springframework.http.HttpStatus;

import com.jdgroup.weather.models.ErrorResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final HttpStatus status;
	
	private final ErrorResponse errorResponse;
	
	public ServiceException (ErrorResponse errorResponse, HttpStatus status)
	{
		super();
		this.errorResponse = errorResponse;
		this.status = status;
	}

}
