package com.onboarding.smart_onboarding.service;

import com.onboarding.smart_onboarding.model.User;
import com.onboarding.smart_onboarding.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}