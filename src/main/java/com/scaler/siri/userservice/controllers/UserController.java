package com.scaler.siri.userservice.controllers;

import com.scaler.siri.userservice.dto.*;
import com.scaler.siri.userservice.dto.ResponseStatus;
import com.scaler.siri.userservice.exception.InvalidTokenException;
import com.scaler.siri.userservice.models.Token;
import com.scaler.siri.userservice.models.User;
import com.scaler.siri.userservice.services.UserService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {
    //login, signup, validate token, logout
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO){
        LoginResponseDTO responseDTO = new LoginResponseDTO();
        try{
            Token token = userService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());
            responseDTO.setToken(token.getValue());
            responseDTO.setResponseStatus(ResponseStatus.SUCCESS);
        }
        catch(Exception e){
            responseDTO.setResponseStatus(ResponseStatus.FAILURE);
        }
        return responseDTO;
    }

    @PostMapping("/signup")
    public UserDTO signup(@RequestBody SignupRequestDTO requestDTO){
        User user = userService.Signup(requestDTO.getName(), requestDTO.getEmail(), requestDTO.getPassword());

        //we can add below code in UserDTO class
//        UserDTO responseDTO = new UserDTO();
//        responseDTO.setName(user.getName());
//        responseDTO.setEmail(user.getEmail());
//        responseDTO.setRoles(user.getRoles());

        return UserDTO.fromUser(user);
    }

    @PatchMapping("/logout")
    public void logout(@RequestBody LogoutRequestDTO requestDTO) throws InvalidTokenException {
            userService.logout(requestDTO.getToken());
    }

    @GetMapping("/validate/{token}")
    public UserDTO validateToken(@PathVariable("token") String token){
        User user = userService.validateToken(token);
        return  UserDTO.fromUser(user);
        //return UserDTO.fromUser(userService.validateToken(token));
        //return null;
    }

}
