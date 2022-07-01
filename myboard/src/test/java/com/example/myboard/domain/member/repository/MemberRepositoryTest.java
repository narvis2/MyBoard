package com.example.myboard.domain.member.repository;

import com.example.myboard.domain.member.MemberEntity;
import com.example.myboard.domain.member.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @AfterEach
    private void after() {
        em.clear();
    }

    private void clear() {
        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("회원저장 성공")
    public void success_user_save() throws Exception {
        var memberEntity = MemberEntity
                .builder()
                .username("username")
                .password("123456789")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        var saveMemberEntity = memberRepository.save(memberEntity);

        var findMemberEntity = memberRepository.findById(saveMemberEntity.getId()).orElseThrow(RuntimeException::new);

        assertThat(findMemberEntity).isSameAs(saveMemberEntity);
        assertThat(findMemberEntity).isSameAs(memberEntity);
    }

    @Test
    @DisplayName("아이디 없이 회원가입시 오류")
    public void no_id_when_registering_as_a_member() throws Exception {
        var memberEntity = MemberEntity
                .builder()
                .password("123456789")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        assertThrows(Exception.class, () -> memberRepository.save(memberEntity));
    }

    @Test
    @DisplayName("회원가입시 이름이 없는 오류")
    public void no_name_when_signing_up() throws Exception {
        var memberEntity = MemberEntity
                .builder()
                .username("username")
                .password("123456789")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        assertThrows(Exception.class, () -> memberRepository.save(memberEntity));
    }

    @Test
    @DisplayName("닉네임 없이 회원가입시 오류")
    public void no_nickname_when_signing_up() throws Exception {
        var memberEntity = MemberEntity
                .builder()
                .name("Member1")
                .username("username")
                .password("123456789")
                .role(Role.USER)
                .age(22)
                .build();

        assertThrows(Exception.class, () -> memberRepository.save(memberEntity));
    }

    @Test
    @DisplayName("나이가 없이 회원가입 오류")
    public void no_age_when_signing_up() throws Exception {
        var memberEntity = MemberEntity
                .builder()
                .username("username")
                .password("123456789")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .build();

        assertThrows(Exception.class, () -> memberRepository.save(memberEntity));
    }

    @Test
    @DisplayName("중복 아이디 회원가입 오류")
    public void duplicate_id_signing_up() throws Exception {
        var memberEntity1 = MemberEntity
                .builder()
                .username("username")
                .password("123456789")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        var memberEntity2 = MemberEntity
                .builder()
                .username("username")
                .password("987654321")
                .name("Member2")
                .nickName("NickName2")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(memberEntity1);
        clear();

        assertThrows(Exception.class, () -> memberRepository.save(memberEntity2));
    }

    @Test
    @DisplayName("회원정보 수정 성공")
    public void success_update_user() throws Exception {
        var memberEntity = MemberEntity
                .builder()
                .username("username")
                .password("123456789")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(memberEntity);
        clear();

        var updatePassword = "updatePassword";
        var updateName = "updateName";
        var updateNickName = "updateNickName";
        int updateAge = 33;

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        var findMemberEntity = memberRepository.findById(memberEntity.getId()).orElseThrow(Exception::new);
        findMemberEntity.updateAge(updateAge);
        findMemberEntity.updateNickname(updateNickName);
        findMemberEntity.updateName(updateName);
        findMemberEntity.updatePassword(passwordEncoder, updatePassword);
        em.flush();

        var findUpdateMemberEntity = memberRepository.findById(findMemberEntity.getId()).orElseThrow(Exception::new);

        assertThat(findUpdateMemberEntity).isSameAs(findMemberEntity);
        assertThat(passwordEncoder.matches(updatePassword, findUpdateMemberEntity.getPassword())).isTrue();
        assertThat(findUpdateMemberEntity.getName()).isEqualTo(updateName);
        assertThat(findUpdateMemberEntity.getName()).isNotEqualTo(memberEntity.getName());
    }

    @Test
    @DisplayName("회원 삭제 성공")
    public void success_delete_user() {
        var memberEntity = MemberEntity
                .builder()
                .username("username")
                .password("123456789")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(memberEntity);
        clear();

        memberRepository.delete(memberEntity);
        clear();

        assertThrows(Exception.class, () -> memberRepository.findById(memberEntity.getId()).orElseThrow(Exception::new));
    }

    @Test
    @DisplayName("중복 닉네임 검사 함수 정상 작동 체크")
    public void exist_by_username_is_success() {
        var username = "username";
        var memberEntity = MemberEntity
                .builder()
                .username("username")
                .password("123456789")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(memberEntity);
        clear();

        assertThat(memberRepository.existsByUsername(username)).isTrue();
        assertThat(memberRepository.existsByUsername(username + "123")).isFalse();
    }

    @Test
    @DisplayName("findByUsername 정상작동 테스트")
    public void find_by_username_is_success() {
        var username = "username";
        var memberEntity = MemberEntity
                .builder()
                .username("username")
                .password("123456789")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(memberEntity);
        clear();

        assertThat(memberRepository.findByUsername(username).get().getUsername()).isEqualTo(memberEntity.getUsername());
        assertThat(memberRepository.findByUsername(username).get().getName()).isEqualTo(memberEntity.getName());
        assertThat(memberRepository.findByUsername(username).get().getId()).isEqualTo(memberEntity.getId());
        assertThrows(Exception.class,
                () -> memberRepository.findByUsername(username + "123")
                        .orElseThrow(Exception::new)
        );
    }

    @Test
    @DisplayName("회원가입시 생성시간 등록 테스트")
    public void create_time_is_success() throws Exception {
        var memberEntity = MemberEntity
                .builder()
                .username("username")
                .password("123456789")
                .name("Member1")
                .nickName("NickName1")
                .role(Role.USER)
                .age(22)
                .build();

        memberRepository.save(memberEntity);
        clear();

        var findMemberEntity = memberRepository.findById(memberEntity.getId()).orElseThrow(Exception::new);

        assertThat(findMemberEntity.getCreatedDate()).isNotNull();
        assertThat(findMemberEntity.getLastModifiedDate()).isNotNull();
    }
}