package com.forge_mind.forge_mind.Service;

import com.forge_mind.forge_mind.Repository.UserRepository;
import com.forge_mind.forge_mind.database.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    //to return all the users:

    public List<User> allUsers(){
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }




}
