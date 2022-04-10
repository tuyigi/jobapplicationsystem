package com.bkexercise.jobapplication.service;

import com.bkexercise.jobapplication.dao.UserRepo;
import com.bkexercise.jobapplication.domain.User;
import com.bkexercise.jobapplication.exception.HandleException;
import com.bkexercise.jobapplication.model.LoginDto;
import com.bkexercise.jobapplication.model.RegisterUserDto;
import com.bkexercise.jobapplication.model.ResponseDto;
import com.bkexercise.jobapplication.model.loginResponseDto;
import com.bkexercise.jobapplication.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    UserRepo userRepo;

//    public UserServiceImpl(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }

    @Override
    public ResponseEntity<ResponseDto> login(LoginDto loginDto) {

        try {

            // Do the authentication
            authenticate(loginDto.getUsername(), loginDto.getPassword());

            final UserDetails userDetails =loadUserByUsername(loginDto.getUsername());
            final String token = jwtUtil.generateToken(userDetails);

            //Extract user information
            User loggedInUser = userRepo.findByUsername(loginDto.getUsername()).get();
            return new ResponseEntity<>(new ResponseDto(HttpStatus.OK,"User logged in successful",new loginResponseDto(loggedInUser.getId(),token,loggedInUser.getFirstName(),loggedInUser.getLastName(),loggedInUser.getEmail())), HttpStatus.OK);
        }catch (Exception exception){
            throw new HandleException(exception.getMessage());
        }
    }

    private void authenticate(String username, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new HandleException("Invalid users");
        } catch (BadCredentialsException e) {
            throw new HandleException("Invalid credentials");
        }
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> exist=userRepo.findByUsername(username);
        if(!exist.isPresent()){
            throw new UsernameNotFoundException("user does not exist");
        }
        User user=new User();
        user=exist.get();
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),new ArrayList<>());
    }

    @Override
    public ResponseEntity<ResponseDto> saveUser(RegisterUserDto registerUserDto) {
        try{
            // check if there is any parameter missed
            if(registerUserDto.getEmail().isEmpty()||registerUserDto.getUsername().isEmpty()||registerUserDto.getFirst_name().isEmpty()||registerUserDto.getLast_name().isEmpty()||registerUserDto.getPassword().isEmpty()){
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"Some parameter missed"),HttpStatus.BAD_REQUEST);
            }

            // check if username is not already use by other
            Optional<User> exists=userRepo.findByUsername(registerUserDto.getUsername());
            if(exists.isPresent()){
                return new ResponseEntity<>(new ResponseDto(HttpStatus.BAD_REQUEST,"Username already used by other"),HttpStatus.BAD_REQUEST);
            }

            // then we save a new user
            User user=new User();
            user.setEmail(registerUserDto.getEmail());
            user.setFirstName(registerUserDto.getFirst_name());
            user.setLastName(registerUserDto.getLast_name());
            user.setUsername(registerUserDto.getUsername());
            user.setPassword(passwordEncoder.encode(registerUserDto.getPassword()));
            userRepo.save(user);
            return new ResponseEntity<>(new ResponseDto(HttpStatus.CREATED,"User registered successful"),HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>(new ResponseDto(e),HttpStatus.BAD_REQUEST);
        }
    }
}
