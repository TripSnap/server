package com.tripsnap.api.domain.entity;

import com.tripsnap.api.config.H2TestConfig;
import com.tripsnap.api.domain.entity.key.GroupMemberId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(H2TestConfig.class)
@Transactional
@DisplayName("GroupMember 엔티티 테스트")
class GroupMemberTest {

    @PersistenceContext
    private EntityManager em;

    private long member1Id, member2Id;
    private long groupId;


    @BeforeEach
    void init() {
        Member member1 = Member.builder()
                .email("te2st@naver.com")
                .password("EDIJG@#")
                .nickname("회원1").build();

        em.persist(member1);

        Member member2 = Member.builder()
                .email("te235st@naver.com")
                .password("EDIdhJG@#")
                .nickname("회원2").build();
        em.persist(member2);
        member1Id = member1.getId();
        member2Id = member2.getId();


        Group group = Group.builder().owner(member1).build();
        em.persist(group);
        groupId = group.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("복합 키 테스트")
    void key() {
        Map<Long, Member> memberMap = Map.of(
                member1Id, em.find(Member.class, member1Id),
                member2Id, em.find(Member.class, member2Id)
        );
        Group group = em.find(Group.class, groupId);

        Assertions.assertNotNull(memberMap.get(member1Id));
        Assertions.assertNotNull(memberMap.get(member2Id));


        // 그룹 멤버 데이터 만들기
        List<GroupMemberId> ids = List.of(
                GroupMemberId.builder().groupId(groupId).memberId(memberMap.get(member1Id).getId()).build(),
                GroupMemberId.builder().groupId(groupId).memberId(memberMap.get(member2Id).getId()).build()
        );


        Group finalGroup = group;
        List<GroupMember> members = ids.stream()
                .map(id -> GroupMember.builder().id(id).group(finalGroup).member(memberMap.get(id.getMemberId())).build())
                .collect(Collectors.toList());
        ReflectionTestUtils.setField(group,"owner",  memberMap.get(member1Id));
        ReflectionTestUtils.setField(group,"members",  members);

        em.flush();
        em.clear();


        // group -> member1, member2


        // case 1)
        // group 데이터 가져오기
        group = em.find(Group.class, groupId);

        Assertions.assertFalse(group.getMembers().isEmpty());
        for(GroupMember m : members) {
            Assertions.assertTrue(group.getMembers().stream().anyMatch(member -> member.getId().equals(m.getId())));
        }

        em.flush();
        em.clear();
    }
}