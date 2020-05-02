package be.kdg.cluedobackend.integration;

import be.kdg.cluedobackend.helpers.MockSecurityContext;
import be.kdg.cluedobackend.model.users.Friend;
import be.kdg.cluedobackend.model.users.FriendType;
import be.kdg.cluedobackend.model.users.Role;
import be.kdg.cluedobackend.model.users.User;
import be.kdg.cluedobackend.repository.FriendRepository;
import be.kdg.cluedobackend.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@Transactional
public class UserApiControllerTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    FriendRepository friendRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    User user1;
    User user2;
    User user3;
    User user4;
    Friend friend1_3;
    Friend friend2_3;
    Friend friend3_4;

    @Before
    public void SetUp() {
        user1 = new User(UUID.randomUUID(), "Test_1", List.of(Role.USER));
        user2 = new User(UUID.randomUUID(), "Test_2", List.of(Role.USER));
        user3 = new User(UUID.randomUUID(), "Test_3", List.of(Role.USER));
        userRepository.saveAndFlush(user1);
        userRepository.saveAndFlush(user2);
        userRepository.saveAndFlush(user3);

        friend1_3 = new Friend(user1, user3);
        friend2_3 = new Friend(user2, user3);
        friend2_3.setFriendType(FriendType.PENDING);
        friend3_4 = new Friend(user3, user4);
        friend3_4.setFriendType(FriendType.CONFIRMED);
        friendRepository.saveAndFlush(friend1_3);
        friendRepository.saveAndFlush(friend2_3);
        friendRepository.saveAndFlush(friend3_4);
    }

    @Test
    public void testAddFriend() throws Exception {
        MockSecurityContext.mockNormalUser(user1.getUserId());
        MvcResult result = mockMvc.perform(
                post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("friendUserName", user2.getUserName())
        ).andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        Assert.assertTrue(Boolean.parseBoolean(response));

        Optional<Friend> optionalFriend = friendRepository.findByAsking_UserIdAndResponding_UserName(user1.getUserId(), user2.getUserName());
        Assert.assertTrue(optionalFriend.isPresent());
        Assert.assertSame(optionalFriend.get().getFriendType(), FriendType.PENDING);
    }

    @Test
    public void testAddMyselfAsFriend() throws Exception {
        MockSecurityContext.mockNormalUser(user1.getUserId());
        MvcResult result = mockMvc.perform(
                post("/api/users/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("friendUserName", user1.getUserName())
        ).andExpect(status().is(400))
                .andReturn();
    }

    @Test
    public void testConfirmFriend() throws Exception {
        MockSecurityContext.mockNormalUser(user1.getUserId());
        MvcResult result = mockMvc.perform(
                put("/api/users/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("friendUserName", user3.getUserName())
        ).andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        Assert.assertTrue(Boolean.parseBoolean(response));

        Optional<Friend> optionalFriend = friendRepository.findByAsking_UserIdAndResponding_UserName(user1.getUserId(), user3.getUserName());
        Assert.assertTrue(optionalFriend.isPresent());
        Assert.assertSame(optionalFriend.get().getFriendType(), FriendType.CONFIRMED);
    }

    @Test
    public void testBlockFriend() throws Exception {
        MockSecurityContext.mockNormalUser(user1.getUserId());
        MvcResult result = mockMvc.perform(
                put("/api/users/block")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("friendUserName", user3.getUserName())
        ).andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        Assert.assertTrue(Boolean.parseBoolean(response));

        Optional<Friend> optionalFriend = friendRepository.findByAsking_UserIdAndResponding_UserName(user1.getUserId(), user3.getUserName());
        Assert.assertTrue(optionalFriend.isPresent());
        Assert.assertSame(optionalFriend.get().getFriendType(), FriendType.BLOCKED);
    }

    @Test
    public void testGetFriends() throws Exception {
        MockSecurityContext.mockNormalUser(user3.getUserId());
        MvcResult result = mockMvc.perform(
                get("/api/users/friends")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        List<User> returnedFriends = Arrays.asList(objectMapper.readValue(response, User[].class));
        Assert.assertTrue(returnedFriends.contains(user4));
    }

    @Test
    public void testGetPendingFriends() throws Exception {
        MockSecurityContext.mockNormalUser(user3.getUserId());
        MvcResult result = mockMvc.perform(
                get("/api/users/pending")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        List<User> returnedFriends = Arrays.asList(objectMapper.readValue(response, User[].class));
        Assert.assertTrue(returnedFriends.contains(friend2_3.getAsking()));
    }

    @Test
    public void testGetPendingFriendsFilter() throws Exception {
        MockSecurityContext.mockNormalUser(user2.getUserId());
        MvcResult result = mockMvc.perform(
                get("/api/users/pending")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        Assert.assertNotNull(response);
        List<Friend> returnedFriends = Arrays.asList(objectMapper.readValue(response, Friend[].class));
        Assert.assertEquals(0, returnedFriends.size());
    }
}
