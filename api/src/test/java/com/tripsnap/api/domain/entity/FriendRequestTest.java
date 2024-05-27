package com.tripsnap.api.domain.entity;

import com.tripsnap.api.config.H2TestConfig;
import com.tripsnap.api.domain.entity.key.MemberFriendId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
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
@DisplayName("FriendRequest 엔티티 테스트")
public class FriendRequestTest {

    @PersistenceContext
    private EntityManager em;

    private long member1Id, member2Id, member3Id;


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

        Member member3 = Member.builder()
                .email("te235s2t@naver.com")
                .password("E!DIdhJG@#")
                .nickname("회원3").build();
        em.persist(member3);

        member1Id = member1.getId();
        member2Id = member2.getId();
        member3Id = member3.getId();

        em.flush();
        em.clear();
    }

    @Test
    @DisplayName("복합 키 테스트")
    void key() {
        Map<Long, Member> memberMap = Map.of(
                member1Id, em.find(Member.class, member1Id),
                member2Id, em.find(Member.class, member2Id),
                member3Id, em.find(Member.class, member3Id)
        );

        Assertions.assertNotNull(memberMap.get(member1Id));
        Assertions.assertNotNull(memberMap.get(member2Id));
        Assertions.assertNotNull(memberMap.get(member3Id));


        // 친구 요청 데이터 만들기
        List<MemberFriendId> ids = List.of(
                MemberFriendId.builder().memberId(memberMap.get(member1Id).getId()).friendId(memberMap.get(member3Id).getId()).build(),
                MemberFriendId.builder().memberId(memberMap.get(member2Id).getId()).friendId(memberMap.get(member1Id).getId()).build()
        );

        List<FriendRequest> friends = ids.stream()
                .map(id -> FriendRequest.builder().id(id).member(memberMap.get(id.getFriendId())).build())
                .collect(Collectors.toList());
        for(FriendRequest f : friends) {
            em.persist(f);
        }

        em.flush();
        em.clear();


        // member1 -> member3
        // member2 -> member1


        // case 1)
        // member1 -> member3 데이터 가져오기
        MemberFriendId id = MemberFriendId.builder().memberId(member1Id).friendId(member3Id).build();
        FriendRequest friend = em.find(FriendRequest.class, id);

        // member1 를 기준으로 가져왔기 때문에 member3의 데이터가 들어와야 한다.
        Assertions.assertNotNull(friend);
        Assertions.assertEquals(friend.getMember().getId(), member3Id);

        em.flush();
        em.clear();


        // case 2)
        // member2 -> member1 데이터 가져오기
        id = MemberFriendId.builder().memberId(member2Id).friendId(member1Id).build();
        friend = em.find(FriendRequest.class, id);


        // member2를 기준으로 가져왔기 때문에 member1 의 데이터가 들어와야 한다.
        Assertions.assertNotNull(friend);
        Assertions.assertEquals(friend.getMember().getId(), member1Id);

        em.flush();
        em.clear();
    }
}