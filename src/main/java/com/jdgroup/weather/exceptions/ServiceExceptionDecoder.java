package com.jdgroup.weather.exceptions;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.hystrix.exception.HystrixBadRequestException;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

public class ServiceExceptionDecoder implements ErrorDecoder 

{
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceExceptionDecoder.class);
    
	private ErrorDecoder delegate = new ErrorDecoder.Default();

	@Override
	public Exception decode(String methodKey, Response response) 
	{
        System.out.println("URL formed is: " + response.request().url());
        LOGGER.error("URL formed is: ", response.request().url());
		if (response.status() == 400)
		{
			try {
				byte[] bodyData = Util.toByteArray(response.body().asInputStream());
				String responseBody = new String(bodyData);
				System.out.println("Exception from ServiceExceptionDecoder with status: " + response.status() + 
						" and response: " + responseBody);
				LOGGER.error("Exception from ServiceExceptionDecoder with status: " + response.status() + 
                        " and response: " + responseBody);
				return new HystrixBadRequestException(responseBody);
			} catch (IOException e) {
				System.out.println("Exception from ServiceExceptionDecoder with status: " + response.status() + 
						" and exception: " + e);
				LOGGER.error("Exception from ServiceExceptionDecoder with status: " + response.status() + 
                        " and exception: " + e);
				return new HystrixBadRequestException(e.getMessage());
			}
			
		}
		return delegate.decode(methodKey, response);
	}
	
	

}
