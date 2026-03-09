package com.tarot.insight.domain.user.repository;

import com.tarot.insight.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일 중복 가입을 막기 위한 메서드
    boolean existsByEmail(String email);

    // 나중에 로그인할 때 이메일로 유저를 찾기 위한 메서드
    Optional<User> findByEmail(String email);
}