package com.ecommerce.member.dto;

import com.ecommerce.member.entity.Member;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
@Slf4j
public class MemberDetails implements UserDetails {

  private final Member member;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    String roles = member.getRole();
    log.debug("role: " + roles);
    for (String role : roles.split(", ")) {
      authorities.add(new SimpleGrantedAuthority(role));
    }
    log.debug("authorities: " + authorities);
    return authorities;
  }

  @Override
  public String getPassword() {
    return member.getPassword();
  }

  @Override
  public String getUsername() {
    return member.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
