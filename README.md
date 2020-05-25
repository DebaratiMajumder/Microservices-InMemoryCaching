# Microservices-InMemoryCaching
Uses and benefits of different types of caching - ehcache, cache2k - along with Spring Caching

When we call some back-end services to retrieve some data, it can be beneficial to cache the response based on some keys to save the expensive back-end calls - particularly when the data is not changing frequently. We can definitely customize how long we want to keep the data in cache, expiration policy through setting some properties. Any indicator or the request object itself can be used as the key of a cache.

There are different types of caching among which use of ehcache and cache2k has been demonstrated here. Spring has an annotation @Cacheable using which we don't need to do any kind of explicit caching of data. We can mention key too for this. However, there is an example of explicit caching too. 

Multiple cacheManagers have been implemented here but any of them could be used to write applications. Normally distributed systems use different types of cache because of which application sometimes might not be able to understand which type of cacheManager would have the required definition for a particular cache. To resolve this, "cacheManager" property of @Cacheable can be used to indicate the exact cacheManager which contains the definition of a particular cache. 

Below are the key features which have been used in this project:

1. @Cacheable
2. EhCache
3. Cache2k
4. Spring Cache2k
5. Explicit caching

I have used some APIs of NCDC Data Access APIs for back-end calls which need a token. Please note that the value of the property below in /src/main/resources/application.properties won't actually work because I don't want to share my token and I have changed the value of the property below:

weather.token=

Anyone who wants to test NCDC weather APIs can go to the link below, provide his/her email address, receive the token in email and use that for the property above:

https://www.ncdc.noaa.gov/cdo-web/token
