package com.bkexercise.jobapplication.controller;


import com.bkexercise.jobapplication.model.LoginDto;
import com.bkexercise.jobapplication.model.ResponseDto;
import com.bkexercise.jobapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

    @Autowired
    UserService userService;

    @PostMapping("/authenticate")
    private ResponseEntity<ResponseDto>authenticate(@RequestBody LoginDto loginDto){
        try{
            return userService.login(loginDto);
        }catch (Exception e){
            return new ResponseEntity<>(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }
    }

}
