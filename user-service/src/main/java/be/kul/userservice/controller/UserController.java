package be.kul.userservice.controller;

import be.kul.userservice.entity.User;
import be.kul.userservice.service.UserService;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/users")
public class UserController {
    //logger setup
    Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping(path="/registerUser")
    public @ResponseBody User registerUser (@RequestBody User user){
        log.info("User registered with id" + user.getId());
        return userService.registerUser(user);
    }


    @RequestMapping(path="/test")
    public @ResponseBody String test() {
        public String resource(@AuthenticationPrincipal Jwt jwt) {
    }
}