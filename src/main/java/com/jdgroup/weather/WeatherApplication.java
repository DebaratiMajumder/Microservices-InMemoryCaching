package com.jdgroup.weather;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.cache2k.configuration.Cache2kConfiguration;
import org.cache2k.extra.spring.SpringCache2kCacheManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.jdgroup.weather.domain.WeatherServicesAggregator;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

@SpringBootApplication
@EnableFeignClients
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class })
@EnableCaching
public class WeatherApplication
{

    @Value("${spring.application.name}")
    private String appName;

    public static void main(String[] args)
    {
        SpringApplication.run(WeatherApplication.class, args);
    }

    @Bean
    public WeatherServicesAggregator weatherServicesAggregator()
    {
        return new WeatherServicesAggregator();
    }
    
    /*
     * Since there are multiple types of cache manager beans defined in this project, one has to be marked as Primary.
     */
    
    @Bean("ehCacheManager")
    @Primary
    public org.springframework.cache.CacheManager ehCacheManager(@Value("${spring.cache.ehcache.maxEntriesLocalHeap:100}") int maxEntriesLocalHeap,
            @Value("${spring.cache.ehcache.timeToIdleSeconds:100}") int timeToIdleSeconds,
            @Value("${spring.cache.ehcache.timeToLiveSeconds:100}") int timeToLiveSeconds,
            @Value("${spring.cache.ehcache.names:LocationList}") String[] cacheNames)
    {
        org.springframework.cache.ehcache.EhCacheCacheManager cacheManager = new org.springframework.cache.ehcache.EhCacheCacheManager();
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        Arrays.asList(cacheNames).forEach(name -> {
            CacheConfiguration cacheConfiguration = new CacheConfiguration();
            cacheConfiguration.name(name);
            cacheConfiguration.maxEntriesLocalHeap(maxEntriesLocalHeap);
            cacheConfiguration.eternal(false);
            cacheConfiguration.timeToIdleSeconds(timeToIdleSeconds);
            cacheConfiguration.timeToLiveSeconds(timeToLiveSeconds);
            cacheConfiguration.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU);
            config.addCache(cacheConfiguration);
        });
        cacheManager.setCacheManager(CacheManager.create(config));
        return cacheManager;
    }
    
    
    @Bean("cache2kManager")
    public org.cache2k.CacheManager cache2kManager(@Value("${spring.cache2k.names:StationList}") String[] cacheNames)
    {
        org.cache2k.CacheManager cacheManager = org.cache2k.CacheManager.getInstance("CacheManager");
        Arrays.asList(cacheNames).forEach(name -> {
            Cache2kConfiguration<String, String> cache2kConfiguration = new Cache2kConfiguration<>();
            cache2kConfiguration.setEntryCapacity(2000);
            cache2kConfiguration.setEternal(false);
            cache2kConfiguration.setExpireAfterWrite(60000l);
            cache2kConfiguration.setName(name);
            cacheManager.createCache(cache2kConfiguration);
        });
        return cacheManager;
    }
    
    /*
     * Apart from expireAfterWrite, none of the other default properties of cache2k has been modified.
     * The default configuration can be checked from the properties of org.cache2k.configuration.Cache2kConfiguration
     */
    @Bean("SpringCache2kCacheManager")
    public org.springframework.cache.CacheManager springCache2kCacheManager(@Value("${spring.springcache2k.names:globalHourly,dailySummary}") String[] cacheNames)
    {
        SpringCache2kCacheManager cacheManager = new SpringCache2kCacheManager();
        Arrays.asList(cacheNames).forEach(name -> {
            cacheManager.addCaches(cache -> cache.name(name).expireAfterWrite(180, TimeUnit.SECONDS));
        });
        return cacheManager;
    }
    

}
