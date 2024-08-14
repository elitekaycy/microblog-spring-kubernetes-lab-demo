package com.userservice.user.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.userservice.user.dto.AuthRequest;
import com.userservice.user.dto.TokenResponse;
import com.userservice.user.utils.JwtTokenProvider;

@RestController
@RequestMapping("/auth")
public class UserController {
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider jwtTokenProvider;

  public UserController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
    this.authenticationManager = authenticationManager;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(@RequestBody AuthRequest auth) {
    Authentication authenticate = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(auth.username(), auth.password()));
    String token = jwtTokenProvider.generateToken((UserDetails) authenticate.getPrincipal());
    return ResponseEntity.ok(new TokenResponse(token));
  }

  @GetMapping("/me")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<Map<String, String>> me(Principal principal) {
    Map<String, String> userdetail = new HashMap<>();
    userdetail.put("username", principal.getName());
    return ResponseEntity.ok(userdetail);
  }

  @GetMapping("/test")
  public String test() {
    return "test";
  }
}
