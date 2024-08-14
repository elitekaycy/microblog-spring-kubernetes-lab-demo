package com.userservice.user.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenProvider {

  private final JwtEncoder jwtEncoder;

  public JwtTokenProvider(JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  public String generateToken(UserDetails user) {
    Instant now = Instant.now();

    String scope = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

    /**
     * Map<String, String> extraClaims = Collections.emptyMap();
     * 
     * extraClaims.put("scope", scope);
     * extraClaims.put("email", user.getUsername());
     * extraClaims.put("username", user.getName());
     * 
     **/

    JwtClaimsSet claims = JwtClaimsSet
        .builder()
        .issuedAt(now)
        .expiresAt(now.plus(1, ChronoUnit.HOURS))
        .subject(user.getUsername())
        .claim("username", user.getUsername())
        .claim("scope", scope)
        .build();

    var encoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS512).build(), claims);
    return this.jwtEncoder.encode((JwtEncoderParameters) encoderParameters).getTokenValue();
  }

}
