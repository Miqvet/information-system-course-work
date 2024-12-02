package itmo.course.coursework.service;

import itmo.course.coursework.config.CacheConfig;
import itmo.course.coursework.domain.User;
import itmo.course.coursework.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = CacheConfig.USER_CACHE, key = "#email")
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println(email);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            new ArrayList<>()
        );
    }

    @CacheEvict(value = CacheConfig.USER_CACHE, key = "#email")
    public void evictUserFromCache(String email) {
    }

    @CachePut(value = CacheConfig.USER_CACHE, key = "#user.email")
    public UserDetails updateUser(User user) {
        return userRepository.save(user);
    }
} 