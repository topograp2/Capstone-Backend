package com.capstone.bszip.auth.refreshToken;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@Data
@RedisHash(value="refreshtoken",timeToLive = 1000 * 60 * 60 * 24 * 7)
public class RefreshToken {
    @Id
    private Long id;

    private String email;

    private String refreshToken;
}
