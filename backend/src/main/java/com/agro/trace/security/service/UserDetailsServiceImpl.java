package com.agro.trace.security.service;

import com.agro.trace.users.domain.User;
import com.agro.trace.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String uuid) throws UsernameNotFoundException {
        User user = userRepository.findByUuidWithRoles(uuid)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + uuid));

        return new org.springframework.security.core.userdetails.User(
                user.getUuid(),
                user.getPinHash(),
                user.isActive(),
                true,
                true,
                !user.isPinLocked(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toSet())
        );
    }
}