package com.mctoluene.productinformationmanagement.configuration;

// package com.mctoluene.productinformationmanagement.configuration;
//
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.cache.Cache;
// import org.springframework.cache.annotation.CachingConfigurerSupport;
// import org.springframework.cache.annotation.EnableCaching;
// import org.springframework.cache.interceptor.CacheErrorHandler;
// import org.springframework.context.annotation.Configuration;
//
// @Slf4j
// @EnableCaching
// @Configuration
// public class RedisCacheConfig extends CachingConfigurerSupport {
// @Override
// public CacheErrorHandler errorHandler() {
// return new CacheErrorHandler() {
// @Override
// public void handleCacheGetError(RuntimeException exception, Cache cache,
// Object key) {
// log.info("Failure getting from cache: " + cache.getName() + ", exception: " +
// exception.toString());
// log.info("Cache skipped - database invoked: " + cache.getName() + ",
// exception: " + exception.toString());
// }
//
// @Override
// public void handleCachePutError(RuntimeException exception, Cache cache,
// Object key, Object value) {
// log.info("Failure putting into cache: " + cache.getName() + ", exception: " +
// exception.toString());
// }
//
// @Override
// public void handleCacheEvictError(RuntimeException exception, Cache cache,
// Object key) {
// log.info("Failure evicting from cache: " + cache.getName() + ", exception: "
// + exception.toString());
// }
//
// @Override
// public void handleCacheClearError(RuntimeException exception, Cache cache) {
// log.info("Failure clearing cache: " + cache.getName() + ", exception: " +
// exception.toString());
// }
// };
// }
//
// }
