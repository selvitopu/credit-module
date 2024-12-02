package com.ing.credit_module.controller;

import com.ing.credit_module.authentication.CreditUserDetailsService;
import com.ing.credit_module.authentication.Tokens;
import com.ing.credit_module.dto.UserDTO;
import com.ing.credit_module.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ing.credit_module.constants.ControllerConstants.USER_CONTROLLER;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping(USER_CONTROLLER)
public class UserController {

    private final IUserService userService;


    /**
     * Get user. Allowed only for Admin
     *
     * @param id user id
     * @return user
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@RequestParam Long id) {
        return ok(userService.findByUserId(id));
    }

    /**
     * Update user. Allowed only for Admin
     *
     * @param id      user id
     * @param userDTO updated user data
     * @return updated user data
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUserById(id, userDTO);
        return ok(updatedUser);
    }

    /**
     * Delete user
     *
     * @param id user id
     * @return boolean result
     */
    @DeleteMapping("/{id}")
    public ResponseEntity deleteUser(Authentication auth, @PathVariable Long id) {
        Long currentUserId = ((CreditUserDetailsService.CreditUserDetails) auth.getPrincipal()).getUser().getId();
        if (currentUserId.equals(id)) {
            return new ResponseEntity<>(
                    "It is impossible to delete the current user",
                    HttpStatus.BAD_REQUEST);
        }
        userService.deleteUser(id);
        return ResponseEntity.ok("Ok");
    }

    /**
     * Get current user
     *
     * @return current user data
     */
    @GetMapping("/current")
    public ResponseEntity<UserDTO> getCurrentUser() {
        return ok(userService.getCurrentUser());
    }

}
