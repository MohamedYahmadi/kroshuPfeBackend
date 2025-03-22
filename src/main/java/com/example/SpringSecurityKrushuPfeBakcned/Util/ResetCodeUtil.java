package com.example.SpringSecurityKrushuPfeBakcned.Util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ResetCodeUtil {
    private final PasswordEncoder passwordEncoder;

    public ResetCodeUtil(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public String encryptResetCode(String code) {
        return passwordEncoder.encode(code);
    }

    public boolean verifyResetCode(String rawCode, String encryptedCode) {
        return passwordEncoder.matches(rawCode, encryptedCode);
    }
}
