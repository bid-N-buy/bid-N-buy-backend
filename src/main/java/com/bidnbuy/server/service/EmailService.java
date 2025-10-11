package com.bidnbuy.server.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    //메일 템플릿 적용
    private MimeMessage createMessage(String to, String ePw) throws MessagingException, UnsupportedEncodingException {
        log.info("보내는 대상 : " + to);
        log.info("인증 번호 : " + ePw);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8"); // MimeMessageHelper 사용

        helper.setTo(to);
        helper.setSubject("BidNBuy 회원가입 인증 코드: "); // 메일 제목

        String msg = "";
        msg += "<h1 style=\"font-size: 30px; padding-right: 30px; padding-left: 30px;\">이메일 주소 확인</h1>";
        msg += "<p style=\"font-size: 17px; padding-right: 30px; padding-left: 30px;\">아래 인증 코드를 회원가입 화면에서 입력해주세요.</p>";
        msg += "<div style=\"padding-right: 30px; padding-left: 30px; margin: 32px 0 40px;\"><table style=\"border-collapse: collapse; border: 0; background-color: #ecdef5; height: 70px; table-layout: fixed; word-wrap: break-word; border-radius: 6px;\"><tbody><tr><td style=\"text-align: center; vertical-align: middle; font-size: 30px;\">";
        msg += ePw; // 인증 코드 삽입
        msg += "</td></tr></tbody></table></div>";

        // HTML 내용 설정 (true 설정으로 HTML 활성화)
        helper.setText(msg, true);

        // 보내는 사람 설정
        helper.setFrom(new InternetAddress(fromEmail, "prac_Admin"));

        return message;
    }


    //2차 인증 코드 생성
    public String createVerificationCode(){
        //랜덤 6 자리 숫자 생성하기
        int code = (int)(Math.random()*899999)+ 100000;
        return String.valueOf(code);
    }

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        try {
            // 1. HTML 메일 메시지 생성
            MimeMessage message = createMessage(toEmail, verificationCode);

            // 2. 메일 전송
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("인증 이메일 전송 중 오류 발생: {}", e.getMessage());
            // 예외 처리 로직 추가 (예: RuntimeException throw)
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

//    //이메일 전송하기
//    public void sendVerificationEmail(String toEmail, String subject, String text){
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        //application.properties에 설정한 username이 발신자
//        message.setFrom(fromEmail);
//        message.setTo(toEmail); //수신자
//        message.setSubject(subject); //제목
//        message.setText(text);//본문 내용
//
//        mailSender.send(message);
//    }

    //임시 비밀번호 이메일 발송
//    public void sendTempPasswordEmail(String toEmail, String tempPassword){
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        message.setFrom(fromEmail);
//        message.setTo(toEmail);
//        message.setSubject("[BID-n-BUY] 비밀번호 재설정 임시 비밀번호");
//        String text = String.format(
//            "요청하신 임시 비밀번호는 **%s** 입니다.\n\n" +
//            "임시 비밀번호는 10분간 유효합니다.", tempPassword
//        );
//        message.setText(text);
//        mailSender.send(message);
//    }
    public void sendTempPasswordEmail(String toEmail, String tempPassword){
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(new InternetAddress(fromEmail, "BID-n-BUY 관리자"));
            helper.setTo(toEmail);
            helper.setSubject("[BID-n-BUY] 비밀번호 재설정 임시 비밀번호");

            // HTML 내용으로 변경
            String htmlContent = String.format(
                    "<p style=\"font-size: 17px;\">요청하신 임시 비밀번호는 아래와 같습니다.</p>" +
                            "<h2 style=\"color: #d9534f; font-weight: bold;\">**%s**</h2>" + // 임시 비밀번호 강조
                            "<p style=\"font-size: 14px; color: gray;\">임시 비밀번호는 10분간 유효합니다.</p>", tempPassword
            );

            helper.setText(htmlContent, true); // true로 HTML 활성화

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("임시 비밀번호 이메일 전송 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("임시 비밀번호 이메일 전송 실패", e);
        }
    }
}
