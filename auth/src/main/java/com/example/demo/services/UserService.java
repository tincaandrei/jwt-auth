package com.example.demo.services;

import com.example.demo.dtos.UserDTO;
import com.example.demo.dtos.builders.UserBuilder;
import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDTO> findUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }
}
