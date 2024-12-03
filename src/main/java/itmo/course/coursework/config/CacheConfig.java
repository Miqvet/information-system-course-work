package itmo.course.coursework.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.concurrent.TimeUnit;


@Configuration
//@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {
//    public static final String USER_CACHE = "userCache";
//    public static final String TASK_CACHE = "taskCache";
//    public static final String GROUP_CACHE = "groupCache";
//
//    @Bean
//    public CacheManager cacheManager() {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager(USER_CACHE, TASK_CACHE, GROUP_CACHE);
//        cacheManager.setCaffeine(Caffeine.newBuilder()
//                .expireAfterWrite(10, TimeUnit.MINUTES)
//                .maximumSize(1000));
//        return cacheManager;
//    }
} 