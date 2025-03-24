package com.capstone.bszip.auth;

import com.capstone.bszip.auth.blackList.BlackList;
import com.capstone.bszip.auth.blackList.BlackListRepository;
import com.capstone.bszip.auth.refreshToken.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final BlackListRepository blackListRepository;
    private final RefreshTokenService refreshTokenService;

    public void login(String email, String refreshToken) {
        refreshTokenService.saveRefreshToken(email, refreshToken);
    }
    @Transactional
    public void logout (String refreshToken,String accessToken, Date expirationDate){
        //refreshtoken 삭제
        refreshTokenService.deleteRefreshToken(refreshToken);

        //blacklist 저장
        BlackList blackList = new BlackList(accessToken, expirationDate);
        blackListRepository.save(blackList);
    }
    @Transactional
    public boolean isTokenBlackList(String token){
        return blackListRepository.findById(token).isPresent();
    }
}
