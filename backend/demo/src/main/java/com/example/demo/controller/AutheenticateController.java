package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.JwtUtils;
import com.example.demo.model.JwtRequest;
import com.example.demo.model.JwtResponse;
import com.example.demo.service.UserDetailsServiceImpl;

@RestController
@CrossOrigin("*")
public class AutheenticateController {
	
	@Autowired
    private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@PostMapping("/generate-token")
    public ResponseEntity<?> generateToken(@RequestBody JwtRequest jwtRequest) throws Exception {
		try{
            authenticate(jwtRequest.getUserName(),jwtRequest.getPassword());
        } catch (Exception e) {
            throw new Exception("user not found");
        }
		 UserDetails userDetails = this.userDetailsServiceImpl.loadUserByUsername(jwtRequest.getUserName());
	     String token = this.jwtUtils.generateToken(userDetails);
	     return ResponseEntity.ok(new JwtResponse(token));
        
    }
	
	private void authenticate(String username, String password) throws Exception {
		try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
            

        } catch (DisabledException e) {
            throw new Exception("USER DISABLED" + e.getMessage());
        }
        catch (BadCredentialsException e){
            throw new Exception("Invalid Credentials "+ e.getMessage());
        }
    }

}
