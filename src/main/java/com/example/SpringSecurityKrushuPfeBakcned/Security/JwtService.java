package com.example.SpringSecurityKrushuPfeBakcned.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;

@Component
public class JwtService {
    private final String key ="0e/6UyIQ04ILL/yVUPwLs8PKK1U/DKECWF8GyF9YekwKi7k28xw2+6tHBYhlDoriCoDnyTDqeuLgj69lcQ6M4TPG4ZI1fWsSoXGzPClwPWBNewfgo+4W5kHs0uM+CEJDBgzHYN3Lja79iI/3Opk4vVsRqtgR1Gxn5r9JTEe0kpzE2mme59xnbexbbI46PuaU0FxZTTNYJQsdA7uvCObCJAemgabRNxXSIx7nuumNWGfSTDyflUAw+7BAcKcMCIC9Y7Vpew1gFN1DflMftFY9eP/4dqkg/Q6ANCD4sPcY+3ii6XDI6ypOaJ9eD9EFq2smlnJJhCAJGwEruw+58q8DgM7RpEzfLDX94LxsM7jmHWE=\n";

    public String createToken(String email){
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(email)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256,key)
                .compact();
    }
    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Boolean validateToken(String token,String email){
    return email.equals(extractEmail(token));
    }



}
