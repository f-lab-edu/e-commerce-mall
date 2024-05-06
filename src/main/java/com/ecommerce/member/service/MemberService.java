package com.ecommerce.member.service;

import com.ecommerce.member.dto.SignupRequest;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.utils.SHA256Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

  private final MemberRepository memberRepository;

  public Long save(SignupRequest request) {
    return memberRepository
        .save(
            Member.builder()
                .email(request.getEmail())
                .password(SHA256Util.encrypt(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .build())
        .getId();
  }
}
