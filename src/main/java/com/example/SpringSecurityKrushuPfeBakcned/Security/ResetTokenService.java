package com.example.SpringSecurityKrushuPfeBakcned.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import java.util.Date;

import java.util.HashMap;

@Service
public class ResetTokenService {

    private final String SECRET_KEY = "0e/6UyIQ04ILL/yVUPwLs8PKK1U/DKECWF8GyF9YekwKi7k28xw2+6tHBYhlDoriCoDnyTDqeuLgj69lcQ6M4TPG4ZI1fWsSoXGzPClwPWBNewfgo+4W5kHs0uM+CEJDBgzHYN3Lja79iI/3Opk4vVsRqtgR1Gxn5r9JTEe0kpzE2mme59xnbexbbI46PuaU0FxZTTNYJQsdA7uvCObCJAemgabRNxXSIx7nuumNWGfSTDyflUAw+7BAcKcMCIC9Y7Vpew1gFN1DflMftFY9eP/4dqkg/Q6ANCD4sPcY+3ii6XDI6ypOaJ9eD9EFq2smlnJJhCAJGwEruw+58q8DgM7RpEzfLDX94LxsM7jmHWE=\n"; // Use the same key as in JwtService
    private final long EXPIRATION_TIME = 10 * 60 * 1000; // 10 minutes

    // Generate a reset token
    public String generateResetToken(String email) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(email) // Set the user's email as the subject
                .setIssuedAt(new Date()) // Set the issue time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Set expiration time
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Sign the token
                .compact(); // Convert to a compact string
    }

    // Verify the reset token
    public String verifyResetToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY) // Verify the token using the secret key
                    .parseClaimsJws(token) // Parse the token
                    .getBody(); // Get the claims (e.g., email and expiration)

            // Check if the token has expired
            if (claims.getExpiration().before(new Date())) {
                throw new RuntimeException("Token has expired");
            }

            return claims.getSubject(); // Return the user's email
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired token: " + e.getMessage());
        }
    }
}
