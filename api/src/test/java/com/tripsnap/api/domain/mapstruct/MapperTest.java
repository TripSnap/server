package com.tripsnap.api.domain.mapstruct;

import com.tripsnap.api.domain.dto.GroupDTO;
import com.tripsnap.api.domain.dto.JoinDTO;
import com.tripsnap.api.domain.dto.MemberDTO;
import com.tripsnap.api.domain.dto.NotificationDTO;
import com.tripsnap.api.domain.entity.Group;
import com.tripsnap.api.domain.entity.Member;
import com.tripsnap.api.domain.entity.Notification;
import com.tripsnap.api.domain.entity.TemporaryMember;
import org.junit.jupiter.api.*;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


@DisplayName("MapStruct Mapper 테스트")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MapperTest {

    PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
    MemberMapper memberMapper;
    GroupMapper groupMapper;
    NotificationMapper notificationMapper;

    @BeforeAll
    void init() {
        memberMapper = new MemberMapperImpl();
        groupMapper = new GroupMapperImpl();
        notificationMapper = new NotificationMapperImpl();

        ReflectionTestUtils.setField(memberMapper, "passwordEncoder", passwordEncoder);
    }

    @Test
    @DisplayName("dto -> entity 변환(기본)")
    void dtoToEntity() {
        JoinDTO dto = new JoinDTO("test_email@naver.com", "paskdng@#!dk", "닉네임2");
        TemporaryMember temporaryMember = memberMapper.toTemporaryMember(dto);

        Assertions.assertEquals(dto.email(), temporaryMember.getEmail());
        Assertions.assertEquals(dto.password(), temporaryMember.getPassword());
        Assertions.assertEquals(dto.nickname(), temporaryMember.getNickname());
        Assertions.assertNull(temporaryMember.getId());
        Assertions.assertNull(temporaryMember.getToken());
    }

    @Test
    @DisplayName("entity list -> dto list 변환 (eager mapping 포함)")
    void entityToDtoWithMapping() {

        // entity list 생성
        Member member1 = Member.builder().id(1L).email("test1@navr.com").nickname("nick1").password("fdhlij").build();
        Member member2 = Member.builder().id(2L).email("test2@navr.com").nickname("nick2").password("fdhlij").build();
        List<Member> members = List.of(member1, member2);

        Group group1 = Group.builder()
                .id(11L).title("그룹1").owner(member1)
                .build();
        Group group2 = Group.builder()
                .id(12L).title("그룹2").owner(member2)
                .build();

        ReflectionTestUtils.setField(group1, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(group2, "createdAt", LocalDateTime.now());
        List<Group> groups = List.of(group1, group2);


        // DTO list로 변환
        String viewerEmail = member1.getEmail();
        List<GroupDTO> dtoList = groupMapper.toDTOList(groups, viewerEmail);

        Assertions.assertEquals(groups.size(), dtoList.size());

        for(GroupDTO groupDTO : dtoList) {
            // group entity -> groupDTO 검사
            Optional<Group> optGroup = groups.stream().filter(g -> g.getId().equals(groupDTO.id())).findFirst();
            Group group = optGroup.get();
            Assertions.assertAll(
                    () -> Assertions.assertTrue(optGroup.isPresent()),
                    () -> Assertions.assertEquals(group.getId(), groupDTO.id()),
                    () -> Assertions.assertEquals(
                            group.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")),
                            groupDTO.createdAt()
                    ),
                    () -> Assertions.assertEquals(groupDTO.isOwner(), viewerEmail.equals(group.getOwner().getEmail()))
            );

            // group entity의 optMember entity -> memberDTO 검사
            Optional<Member> optMember = members.stream()
                    .filter(m -> m.getEmail().equals(groupDTO.owner().email()))
                    .findFirst();
            Member member = optMember.get();
            MemberDTO memberDTO = groupDTO.owner();
            Assertions.assertAll(
                    () -> Assertions.assertTrue(optMember.isPresent()),
                    () -> Assertions.assertEquals(memberDTO.email(), member.getEmail())
            );

        }
    }

    @DisplayName("date format 검사")
    @Test
    void dateFormatTest() {
        Member member = Member.builder().build();
        LocalDateTime now = LocalDateTime.now();
        ReflectionTestUtils.setField(member,"createdAt", now);
        MemberDTO memberDTO = memberMapper.toMemberDTO(member);
        Assertions.assertEquals(memberDTO.joinDate(), now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
    }

    @DisplayName("@Mapping expression에서 boolean 타입으로 변환 테스트")
    @Test
    void mappingAnnotationExpressionTest() {
        // 리스트 아닐 때
        Notification notBroadCast = Notification.builder().memberId(1L).build();
        Notification broadCast = Notification.builder().memberId(null).build();

        NotificationDTO notBroadCastDTO = notificationMapper.toNotificationDTO(notBroadCast);
        NotificationDTO broadCastDTO = notificationMapper.toNotificationDTO(broadCast);

        Assertions.assertFalse(notBroadCastDTO.isBroadCast());
        Assertions.assertTrue(broadCastDTO.isBroadCast());

        // 리스트일 때
        List<Notification> notifications = List.of(notBroadCast, broadCast);
        List<NotificationDTO> dtoList = notificationMapper.toDTOList(notifications);

        Assertions.assertFalse(dtoList.get(0).isBroadCast());
        Assertions.assertTrue(dtoList.get(1).isBroadCast());
    }

    @DisplayName("@Mapping expression boolean 테스트")
    @Test
    void mappingAnnotationExpressionTest2() {
        List<Member> list = List.of(Member.builder().build(), Member.builder().build());
        List<MemberDTO> dtoList = memberMapper.toWatingMemberDTOList(list);
        dtoList.forEach(m -> {
            Assertions.assertTrue(m.isWaiting());
        });

        Member member = Member.builder().build();
        MemberDTO waitingMemberDTO = memberMapper.toWaitingMemberDTO(member);
        Assertions.assertTrue(waitingMemberDTO.isWaiting());
    }
}