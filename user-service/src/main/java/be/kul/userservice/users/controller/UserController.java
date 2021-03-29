package be.kul.userservice.users.controller;

import be.kul.userservice.users.entity.User;
import be.kul.userservice.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path="/user-service")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(path="/users")
    public @ResponseBody User registerUser (@AuthenticationPrincipal Jwt principal, @RequestBody User user){
        String userId = principal.getClaimAsString("sub");
        user.setId(userId);

        return userService.registerUser(user);
    }

    @PutMapping(path="/users")
    public @ResponseBody User updateUser (@AuthenticationPrincipal Jwt principal, @RequestBody User user){
        String userId = principal.getClaimAsString("sub");
        user.setId(userId);

        return userService.updateUser(user);
    }

    @GetMapping(path="/users")
    public @ResponseBody User getUserByToken (@AuthenticationPrincipal Jwt principal){
        String userId = principal.getClaimAsString("sub");

        return userService.getUserById(userId);
    }


    @GetMapping(path="/users/get-id")
    public @ResponseBody String test(@AuthenticationPrincipal Jwt principal) {
        return principal.getClaimAsString("sub");
    }
}