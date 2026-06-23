package com.tnc.userManagement.service.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tnc.userManagement.service.constant.SecurityConstant;
import com.tnc.userManagement.service.model.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

/**
 * JWT Access Denied Handler for handling authorization failures
 */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                      AccessDeniedException accessDeniedException) throws IOException {
        
        HttpResponse httpResponse = new HttpResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN,
                HttpStatus.FORBIDDEN.getReasonPhrase().toUpperCase(),
                SecurityConstant.ACCESS_DENIED_MESSAGE
        );
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
