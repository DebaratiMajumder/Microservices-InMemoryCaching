package com.jdgroup.weather.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jdgroup.weather.domain.WeatherServicesAggregator;
import com.jdgroup.weather.models.DataRequest;

@RestController
@RequestMapping("/weather")
public class WeatherServicesController
{
    @Value("${weather.token}")
    private String weatherToken;

    @Autowired
    WeatherServicesAggregator weatherServicesAggregator;

    @GetMapping(value = "/locations")
    public String getLocations()
    {
        return weatherServicesAggregator.getLocations(weatherToken);
    }

    @GetMapping(value = "/stations")
    public String getStations(@RequestParam(name = "limit") Optional<String> limit)
    {
        String records = limit.isPresent() ? limit.get() : null;
        return weatherServicesAggregator.getStationsWithLimit(weatherToken, records);
    }

    @GetMapping(value = "/globalHourlyData")
    public String getStations(@RequestParam(name = "limit") Integer limit)
    {
        DataRequest dataRequest = DataRequest.builder().dataSet("global-hourly").dataTypes("TMP").limit(limit).build();
        return weatherServicesAggregator.getGlobalHourlyWithDatasetAndLimit(dataRequest);
    }

}
