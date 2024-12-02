package com.ing.credit_module.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.credit_module.config.UserContextHolder;
import com.ing.credit_module.dto.SignUpDTO;
import com.ing.credit_module.dto.UserDTO;
import com.ing.credit_module.exception.PasswordsDontMatchException;
import com.ing.credit_module.exception.UserAlreadyExistsException;
import com.ing.credit_module.exception.UserAlreadyExistsHttpException;
import com.ing.credit_module.exception.UserNotFoundException;
import com.ing.credit_module.exception.UserNotFoundHttpException;
import com.ing.credit_module.model.User;
import com.ing.credit_module.repository.IUserRepository;
import com.ing.credit_module.service.IRoleService;
import com.ing.credit_module.service.IUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

import static com.ing.credit_module.constants.ServiceConstants.USER_SERVICE;

@Service(value = USER_SERVICE)
public class UserService implements IUserService {

    private IUserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ObjectMapper objectMapper;
    private IRoleService roleService;

    @Lazy
    @Autowired
    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder, ObjectMapper objectMapper, IRoleService roleService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.roleService = roleService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public User findByUsername(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Transactional
    @Override
    public User register(SignUpDTO signUpDTO) throws UserAlreadyExistsException {
        if (!signUpDTO.getPassword().equals(signUpDTO.getConfirmPassword())) {
            throw new PasswordsDontMatchException();
        }

        String username = signUpDTO.getUsername();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException(username);
        }

        User user = signUpUser(signUpDTO);
        user = userRepository.save(user);

        return user;
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public UserDTO findByUserId(Long id) {
        User existingUser = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundHttpException("User not found", HttpStatus.NOT_FOUND)
        );

        return objectMapper.convertValue(existingUser, UserDTO.class);
    }

    @Transactional
    public UserDTO updateUserById(Long userId, UserDTO userDTO) {
        try {
            return updateUser(userId, userDTO);
        } catch (UserAlreadyExistsException exception) {
            throw new UserAlreadyExistsHttpException();
        }
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new UserNotFoundHttpException("User not found", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public User getSignedUser() {
        UserDTO userDTO = getCurrentUser();

        try {
            return findByUsername(userDTO.getUsername());
        } catch (UserNotFoundException ue) {
            ue.printStackTrace();
        }
        return null;
    }

    public UserDTO getCurrentUser() {
        return objectMapper.convertValue(UserContextHolder.getUser(), UserDTO.class);
    }


    private UserDTO updateUser(Long id, UserDTO userDTO) throws UserAlreadyExistsException {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundHttpException(
                "User not found", HttpStatus.NOT_FOUND));

        String username = userDTO.getUsername();
        if (!existingUser.getUsername().equals(username) && userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException(username);
        }
        userRepository.save(existingUser);
        return objectMapper.convertValue(existingUser,UserDTO .class);
    }


    private User signUpUser(SignUpDTO signUpDTO) {
        User user = new User();

        user.setUsername(signUpDTO.getUsername());
        user.setName(signUpDTO.getName());
        user.setSurname(signUpDTO.getSurname());
        String encodedPassword = encodePassword(signUpDTO.getPassword());
        user.setPasswordHash(encodedPassword);
        user.setCreditLimit(signUpDTO.getCreditLimit());
        user.setRoles(new HashSet<>(Collections.singletonList(roleService.getDefaultRole())));

        return user;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }


}
