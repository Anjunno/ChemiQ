package com.chemiq.service;

import com.chemiq.DTO.CustomUserDetails;
import com.chemiq.entity.Member;
import com.chemiq.repository.MemberRepository;
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

    // 해당하는 memberId에 맞는 Member 찾아서 CustomUserDetails에 전달
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {

        Optional<Member> memberData = memberRepository.findByMemberId(memberId);

        if(memberData.isPresent()) {
            Member data = memberData.get();
            return new CustomUserDetails(data);
        }


        return null;
    }
}
