package com.perfectkode.bikri.auth.service.otp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String OTP_PREFIX = "OTP:";
    private static final long OTP_EXPIRATION_MINUTES = 5;

    @Async
    @Override
    public void sendOtp(String email) {
        String key = OTP_PREFIX + email;

        // Check if an unexpired OTP already exists in Redis
        String existingOtp = redisTemplate.opsForValue().get(key);
        String otpToSend;

        if (existingOtp != null) {
            // Keep resending the active OTP until it expires
            otpToSend = existingOtp;
            log.info("Active OTP found for {}. Resending existing OTP.", email);
        } else {
            // Generate a new 4-digit random OTP
            otpToSend = generate4DigitAlphaNumericOtp();
            // Store in Redis with 5 minutes TTL
            redisTemplate.opsForValue().set(key, otpToSend, OTP_EXPIRATION_MINUTES, TimeUnit.MINUTES);
            log.info("Generated new OTP for {}.", email);
        }

        // Mock email sending service for local dev environment ✉️
        mockSendEmail(email, otpToSend);
    }

    @Override
    public boolean verifyOtp(String email, String inputOtp) {
        String key = OTP_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp == null || !storedOtp.equals(inputOtp)) {
            return false;
        }

        // Delete OTP from Redis immediately once successfully verified
        redisTemplate.delete(key);
        return true;
    }

    private String generate4DigitAlphaNumericOtp() {
        SecureRandom random = new SecureRandom();

        // 1. Create an array of 4 characters
        char[] otp = new char[4];

        // 2. Fill first 3 positions with random digits
        for (int i = 0; i < 3; i++) {
            otp[i] = (char) ('0' + random.nextInt(10)); // '0' to '9'
        }

        // 3. Put one random uppercase letter in the 4th position
        otp[3] = (char) ('A' + random.nextInt(26)); // 'A' to 'Z'

        // 4. Shuffle the 4 characters so the letter can be anywhere
        for (int i = 0; i < otp.length; i++) {
            int j = random.nextInt(otp.length);
            // swap i and j
            char temp = otp[i];
            otp[i] = otp[j];
            otp[j] = temp;
        }

        return new String(otp);
    }

    private void mockSendEmail(String email, String otp) {
        log.info("=================================================");
        log.info("📧 MOCK EMAIL SERVICE 📧");
        log.info("To: {}", email);
        log.info("Subject: E-Commerce Email Verification Code");
        log.info("Body: Your OTP code is [ {} ]. Valid for 5 minutes.", otp);
        log.info("=================================================");
    }
}
