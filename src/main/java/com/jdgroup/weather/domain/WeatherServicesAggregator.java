package com.jdgroup.weather.domain;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.jdgroup.weather.models.DataRequest;
import com.jdgroup.weather.service.IDataSetIntegrator;
import com.jdgroup.weather.service.IWeatherServiceIntegrator;

@Service
public class WeatherServicesAggregator
{

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherServicesAggregator.class);

    @Autowired
    org.cache2k.CacheManager cache2kManager;

    org.cache2k.Cache<String, String> stationCache;

    @Autowired
    IWeatherServiceIntegrator weatherServiceIntegrator;

    @Autowired
    IDataSetIntegrator dataSetIntegrator;

    @PostConstruct
    public void init()
    {
        stationCache = cache2kManager.getCache("StationList");
    }

    /*
     * This result will be kept in cache unless expired. 
     * No key has been mentioned. 
     * No manual caching.
     */
    @Cacheable(value = "LocationList")
    @Retryable(maxAttemptsExpression = "#{${max.retry.attempts.locations:2}}")
    public String getLocations(String weatherToken)
    {
        LOGGER.info("getLocations :: Calling back-end", "");
        return weatherServiceIntegrator.getLocations(weatherToken);
    }

    /*
     * This result is cached and retrieved manually based on key.
     */
    @Retryable(maxAttemptsExpression = "#{${max.retry.attempts.stations:2}}")
    public String getStationsWithLimit(String weatherToken, String limit)
    {
        String stations = null;
        if (stationCache.get(limit + "stations") != null)
        {
            LOGGER.info("getStationsWithLimit manual caching:: reading from cache", "");
            stations = (String)stationCache.get(limit + "stations");
        }
        else
        {
            LOGGER.info("getStationsWithLimit manual caching :: not in cache; calling back-end", "");
            stations = weatherServiceIntegrator.getStations(weatherToken, limit);
        }
        stationCache.put(limit + "stations", stations);
        return stations;
    }

    /*
     * This is cache2k along with Spring without manual caching. 
     * Key is used to cache data and retrieve. 
     * The attribute cacheManager is mentioned to refer to Spring 2k Cache Manager explicitly - since there are multiple cacheManager definitions in this project.
     * The attribute cacheManager needs an instance of org.springframework.CacheManager (net.sf.ehcache.CacheManager or org.cache2k.CacheManager can't be used).
     */
    @Cacheable(value = "globalHourly", key = "#dataRequest", cacheManager = "SpringCache2kCacheManager")
    @Retryable(maxAttemptsExpression = "#{${max.retry.attempts.dataset:2}}")
    public String getGlobalHourlyWithDatasetAndLimit(DataRequest dataRequest)
    {
        LOGGER.info("getGlobalHourlyWithDatasetAndLimit :: Calling back-end", "");
        return this.dataSetIntegrator.getDataWithDataSetAndLimit(dataRequest.getDataSet(), dataRequest.getDataTypes(), dataRequest.getLimit());
    }
    

}
