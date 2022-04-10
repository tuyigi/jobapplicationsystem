package com.bkexercise.jobapplication.service;

import com.bkexercise.jobapplication.model.LoginDto;
import com.bkexercise.jobapplication.model.RegisterUserDto;
import com.bkexercise.jobapplication.model.ResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    ResponseEntity<ResponseDto> login(LoginDto loginDto);
    UserDetails loadUserByUsername(String username);
    ResponseEntity<ResponseDto> saveUser(RegisterUserDto registerUserDto);
}
