package com.jdgroup.weather.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "datasets", path = "/access/services/search/v1", url = "https://www.ncei.noaa.gov")
public interface IDataSetIntegrator
{
    @GetMapping(value = "/datasets/?limit={limit}")
    String getDatasetWithLimit(@PathVariable("limit") Integer limit);

    @GetMapping(value = "/data?dataset={dataset}&dataTypes={dataTypes}&limit={limit}")
    String getDataWithDataSetAndLimit(@PathVariable("dataset") String dataset, @PathVariable("dataTypes") String dataTypes,
            @PathVariable("limit") Integer limit);

}
