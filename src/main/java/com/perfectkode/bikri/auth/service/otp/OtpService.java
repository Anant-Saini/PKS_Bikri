package com.perfectkode.bikri.auth.service.otp;

public interface OtpService {
    void sendOtp(String email);
    boolean verifyOtp(String email, String inputOtp);
}
