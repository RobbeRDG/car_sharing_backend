package be.kul.userservice.users.service;

import be.kul.userservice.users.entity.User;
import be.kul.userservice.users.repository.UserRepository;
import be.kul.userservice.utils.exceptions.AlreadyExistsException;
import be.kul.userservice.utils.exceptions.DoesntExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        if (userRepository.existsById(user.getId())) {
            logger.warn("User with id: '" + user.getId() + "' already exists");
            throw new AlreadyExistsException("User with id: '" + user.getId() + "' already exists");
        }

        User savedUser = userRepository.save(user);
        logger.info("User with id '" + user.getId() + "' created");
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    public boolean existsUser(String userId) {
        return userRepository.existsById(userId);
    }

    public User updateUser(User user) {
        if (userRepository.existsById(user.getId())) {
            User savedUser = userRepository.save(user);
            logger.info("User with id '" + user.getId() + "' updated");
            return userRepository.save(user);
        } else {
            logger.warn("User with id: '" + user.getId() + "' doesn't exists");
            throw new DoesntExistException("User with id: '" + user.getId() + "' doesn't exists");
        }


    }
}
