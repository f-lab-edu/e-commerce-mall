package com.ecommerce.member.service;

import com.ecommerce.member.dto.SignupRequest;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

  private final MemberRepository memberRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public Long save(SignupRequest request) {
    return memberRepository
        .save(
            Member.builder()
                .email(request.getEmail())
                .password(bCryptPasswordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .build())
        .getId();
  }
}
