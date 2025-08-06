package com.emolink.emolink.service;

import com.emolink.emolink.DTO.CustomUserDetails;
import com.emolink.emolink.entity.Member;
import com.emolink.emolink.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

        Member memberData = memberRepository.findByMemberId(memberId);

        if(memberData != null) {

            return new CustomUserDetails(memberData);
        }


        return null;
    }
}
