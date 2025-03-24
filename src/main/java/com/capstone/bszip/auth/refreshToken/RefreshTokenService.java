package com.capstone.bszip.auth.refreshToken;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String,String> redisTemplate;

    //저장
    public void saveRefreshToken(String email, String refreshToken){
        redisTemplate.opsForValue().set("refreshtoken:"+refreshToken,email,7, TimeUnit.DAYS);
    }
    //존재 여부 확인
    public String getEmailByRefreshToken(String refreshToken){
        return redisTemplate.opsForValue().get("refreshtoken:" + refreshToken);
    }
    //삭제 - 로그아웃시
    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete("refreshtoken:" + refreshToken);
    }
}
