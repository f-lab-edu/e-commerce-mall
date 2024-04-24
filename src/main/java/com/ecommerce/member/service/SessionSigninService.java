package com.ecommerce.member.service;

import com.ecommerce.common.ResponseMessage;
import com.ecommerce.common.StatusEnum;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.utils.SHA256Util;
import com.ecommerce.utils.SessionUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionSigninService implements SigninService {

    private final MemberRepository memberRepository;
    private final HttpSession session;

    // 세션 로그인
    @Override
    public ResponseMessage signin(String email, String password) {
        // 이메일 체크
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        // 비밀번호 체크
        if(!member.checkPassword(SHA256Util.encrypt(password))) {
            throw new IllegalArgumentException("이메일 혹은 비밀번호가 일치하지 않습니다.");
        }

        // session
        SessionUtil.setMemberId(session, member.getId());

        // 응답 메시지 처리
        ResponseMessage message = new ResponseMessage();
        message.setStatus(StatusEnum.OK);
        message.setMessage("로그인 성공");
        message.setData(member.getId());
        return message;
    }

    @Override
    public ResponseMessage logout() {
        return null;
    }
}
