package com.bkexercise.jobapplication.controller;

import com.bkexercise.jobapplication.model.RegisterUserDto;
import com.bkexercise.jobapplication.model.ResponseDto;
import com.bkexercise.jobapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping
    private ResponseEntity<ResponseDto> registerUser(@RequestBody RegisterUserDto registerUserDto){
        try{
            return userService.saveUser(registerUserDto);
        }catch (Exception e){
            return new ResponseEntity<>(new ResponseDto(e), HttpStatus.BAD_REQUEST);
        }
    }
}
