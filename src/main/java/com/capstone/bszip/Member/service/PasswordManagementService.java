package com.capstone.bszip.Member.service;

import com.capstone.bszip.Member.domain.Member;
import com.capstone.bszip.Member.service.dto.EmailMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordManagementService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    private final MemberService memberService;


    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=";

    // 임시 비밀번호 생성된 거 적용하고 이메일을 보냄
    public String sendMail(EmailMessage emailMessage, String type) {

        String authNum = createCode();

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        if(type.equals("password")) {
            memberService.setTempPassword(emailMessage.getTo(), authNum);
        }

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailMessage.getTo()); // 메일 수신자
            mimeMessageHelper.setSubject(emailMessage.getSubject()); // 메일 제목
            mimeMessageHelper.setText(setContext(authNum, type), true); // 메일 본문 내용, HTML 여부
            mailSender.send(mimeMessage);

            log.info("Successfully sent email to " + emailMessage.getTo());

            return authNum;
        }catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    // 임시 비밀번호 생성
    public String createCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(17);

        for(int i = 0; i < 17; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
    // 타임리프를 통한 html에 보낼 거임
    public String setContext(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(type, context);
    }

    public boolean isItTempPassword(Member member){
        return member.getTempPassword() != 0;
    }


}

