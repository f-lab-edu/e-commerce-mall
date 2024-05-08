package com.ecommerce.member.service;

import com.ecommerce.member.dto.SignupRequest;
import com.ecommerce.member.entity.Member;
import com.ecommerce.member.repository.MemberRepository;
import com.ecommerce.utils.SHA256Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    public void setUp() {
        memberRepository.deleteAll();
    }

    @DisplayName("회원가입에 성공한다.")
    @Test
    void save() {
        // given
        final SignupRequest request = SignupRequest.builder()
                .email("member1@test.com")
                .password("f_lab16881577")
                .name("테스트1")
                .phone("01011111111")
                .build();

        // when
        memberService.save(request);

        // then
        List<Member> members = memberRepository.findAll();

        assertThat(members.size()).isEqualTo(1);
        assertThat(members.get(0).getEmail()).isEqualTo(request.getEmail());
        assertThat(members.get(0).getPassword()).isEqualTo(SHA256Util.encrypt(request.getPassword()));
        assertThat(members.get(0).getName()).isEqualTo(request.getName());
        assertThat(members.get(0).getPhone()).isEqualTo(request.getPhone());
    }

    @DisplayName("이메일이 중복되면 회원가입에 실패한다.")
    @Test
    void saveWithDuplicateEmail() {
        // given
        final String url = "/members";
        final SignupRequest request1 = SignupRequest.builder()
                .email("member1@test.com")
                .password("f_lab16881577")
                .name("테스트1")
                .phone("01011111111")
                .build();

        final SignupRequest request2 = SignupRequest.builder()
                .email("member1@test.com")
                .password("f_lab16881577")
                .name("테스트2")
                .phone("01011111111")
                .build();

        memberService.save(request1);

        // when - then
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> memberService.save(request2));
    }
}