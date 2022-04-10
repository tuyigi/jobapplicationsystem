package com.bkexercise.jobapplication.exception;


import com.bkexercise.jobapplication.model.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final String HTTP_STATUS = "status";
    private static final String MESSAGE = "message";


    /**
     * The Object mapper.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse response, AccessDeniedException e) throws IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> data = new HashMap<>();
        ResponseDto responseObjectDto = new ResponseDto();

        if (auth == null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            data.put(HTTP_STATUS, response.getStatus());
            data.put(MESSAGE, "Unauthorized");
            responseObjectDto.setStatus(401);
            responseObjectDto.setMessage("You're not logged in to access this resource");
            responseObjectDto.setData(data);
            response.getWriter().write(objectMapper.writeValueAsString(responseObjectDto));
        } else {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            data.put(HTTP_STATUS, response.getStatus());
            String message = "Access Denied for : " + auth.getAuthorities().toArray()[0].toString();
            data.put(MESSAGE, message);
            responseObjectDto.setStatus(401);
            responseObjectDto.setData(data);
            responseObjectDto.setMessage(message);
            response.getWriter().write(objectMapper.writeValueAsString(responseObjectDto));
        }
    }
}
