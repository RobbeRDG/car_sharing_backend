package be.kul.userservice.users.service;

import be.kul.userservice.users.entity.User;
import be.kul.userservice.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    public boolean existsUser(String userId) {
        return userRepository.existsById(userId);
    }
}
