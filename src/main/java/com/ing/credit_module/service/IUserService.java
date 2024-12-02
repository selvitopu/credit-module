package com.ing.credit_module.service;

import com.ing.credit_module.dto.SignUpDTO;
import com.ing.credit_module.dto.UserDTO;
import com.ing.credit_module.exception.UserAlreadyExistsException;
import com.ing.credit_module.exception.UserNotFoundException;
import com.ing.credit_module.model.User;

public interface IUserService {

    User findByUsername(String username) throws UserNotFoundException;

    User register(SignUpDTO signUpDTO) throws UserAlreadyExistsException;

    void saveUser(User user);

    UserDTO findByUserId(Long id);

    void deleteUser(Long id);

    User getSignedUser();

    UserDTO getCurrentUser();

    UserDTO updateUserById(Long id, UserDTO userDTO);
}
