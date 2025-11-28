package com.chatwave.chatwave.service;
import com.chatwave.chatwave.model.User;
import com.chatwave.chatwave.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {

        if(!userRepository.existsByUsername(user.getUsername())){
            return userRepository.save(user);
        }
        else return null;

    }


    public User loginUser(String username, String password) {
        // 1. Find the user in MongoDB
        User foundUser = userRepository.findByUsername(username);

        // 2. If user doesn't exist, return null
        if (foundUser == null) {
            return null;
        }

        // 3. Check if passwords match

        if (foundUser.getPassword().equals(password)) {
            return foundUser;
        }

        // 4. Password wrong? Return null
        return null;
    }
}