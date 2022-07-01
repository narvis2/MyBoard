package com.example.myboard.domain.member;

import com.example.myboard.domain.BaseTimeEntity;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class MemberEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String username;

    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String nickName;

    @Column(nullable = false, length = 30)
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void updatePassword(PasswordEncoder passwordEncoder, String password) {
        this.password = passwordEncoder.encode(password);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateNickname(String nickName) {
        this.nickName = nickName;
    }

    public void updateAge(int age) {
        this.age = age;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }
}

