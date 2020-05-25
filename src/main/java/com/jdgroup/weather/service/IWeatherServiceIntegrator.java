package com.jdgroup.weather.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="weather", path="/cdo-web/api/v2", url="https://www.ncdc.noaa.gov")
public interface IWeatherServiceIntegrator {
	
	@GetMapping(value = "/locations")
	String getLocations(@RequestHeader String token);
	
	@GetMapping(value = "/stations/?limit={limit}")
	String getStations(@RequestHeader String token, @PathVariable("limit") String limit);

}
