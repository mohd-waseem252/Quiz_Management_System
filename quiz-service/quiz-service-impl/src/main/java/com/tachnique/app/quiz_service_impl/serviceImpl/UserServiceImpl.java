package com.tachnique.app.quiz_service_impl.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tachnique.app.dto.UserDto;
import com.tachnique.app.service.UserService;
import com.tachnique.app.quiz_service_impl.entity.UserEntity;
import com.tachnique.app.quiz_service_impl.mapper.UserMapper;
import com.tachnique.app.quiz_service_impl.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        // enforce unique username
        userRepository.findByUsername(userDto.getUsername()).ifPresent(u -> {
            throw new IllegalArgumentException("Username already exists");
        });
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        UserEntity saved = userRepository.save(UserMapper.toEntity(userDto));
        return UserMapper.toDto(saved);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto login(UserDto credentials) {
        var userOpt = userRepository.findByUsername(credentials.getUsername());
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        var user = userOpt.get();
        if (!passwordEncoder.matches(credentials.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return UserMapper.toDto(user);
    }
}
