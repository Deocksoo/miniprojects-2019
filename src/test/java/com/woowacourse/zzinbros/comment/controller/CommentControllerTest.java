package com.woowacourse.zzinbros.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woowacourse.zzinbros.BaseTest;
import com.woowacourse.zzinbros.comment.domain.Comment;
import com.woowacourse.zzinbros.comment.dto.CommentRequestDto;
import com.woowacourse.zzinbros.comment.service.CommentService;
import com.woowacourse.zzinbros.post.domain.Post;
import com.woowacourse.zzinbros.post.service.PostService;
import com.woowacourse.zzinbros.user.domain.User;
import com.woowacourse.zzinbros.user.domain.repository.UserRepository;
import com.woowacourse.zzinbros.user.dto.UserResponseDto;
import com.woowacourse.zzinbros.user.service.UserService;
import com.woowacourse.zzinbros.user.web.support.UserArgumentResolver;
import com.woowacourse.zzinbros.user.web.support.UserSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.swing.tree.ExpandVetoException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class CommentControllerTest extends BaseTest {
    private static final Long MOCK_ID = 1L;
    private static final String LOGIN_USER = "loggedInUser";
    private static final String MAPPING_PATH = "/comments";
    private static final String MOCK_CONTENTS = "contents";

    private User mockUser = new User("name", "email@example.net", "12QWas!@");
    private Post mockPost = new Post(MOCK_CONTENTS, mockUser);
    private Comment mockComment = new Comment(mockUser, mockPost, MOCK_CONTENTS);
    private String commentRequestDto;
    private UserResponseDto mockUserDto = new UserResponseDto(MOCK_ID, mockUser.getName(), mockUser.getEmail());

    private MockMvc mockMvc;

    @Autowired
    CommentController commentController;

    @MockBean
    CommentService commentService;

    @MockBean
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    PostService postService;

    @MockBean
    UserSession userSession;

    @BeforeAll
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(commentController)
                .setCustomArgumentResolvers(new UserArgumentResolver())
                .alwaysDo(print())
                .build();

        final CommentRequestDto dto = new CommentRequestDto();
        dto.setPostId(MOCK_ID);
        dto.setCommentId(MOCK_ID);
        dto.setContents(MOCK_CONTENTS);
        commentRequestDto = new ObjectMapper().writeValueAsString(dto);
    }

    @Test
    void get_mapping() throws Exception {
        final List<Comment> comments = new ArrayList<>(Arrays.asList(
                new Comment(mockUser, mockPost, "comment1"),
                new Comment(mockUser, mockPost, "comment2"),
                new Comment(mockUser, mockPost, "comment3")
        ));

        given(postService.read(MOCK_ID)).willReturn(mockPost);
        given(commentService.findByPost(mockPost)).willReturn(comments);

        mockMvc.perform(get(MAPPING_PATH + "/by-post/" + MOCK_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].contents", is("comment1")))
                .andExpect(jsonPath("$.[1].contents", is("comment2")))
                .andExpect(jsonPath("$.[2].contents", is("comment3")));
    }

    @Test
    void add_mapping() throws Exception {
        given(userRepository.findById(MOCK_ID)).willReturn(Optional.ofNullable(mockUser));
        given(userService.findUserById(MOCK_ID)).willReturn(mockUser);
        given(postService.read(MOCK_ID)).willReturn(mockPost);
        given(commentService.add(mockUser, mockPost, MOCK_CONTENTS)).willReturn(mockComment);

        mockMvc.perform(post(MAPPING_PATH)
                .content(commentRequestDto)
                .sessionAttr(LOGIN_USER, mockUserDto)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void edit_mapping() throws Exception {
        given(commentService.update(MOCK_ID, MOCK_CONTENTS, mockUser)).willReturn(mockComment);

        mockMvc.perform(put(MAPPING_PATH)
                .content(commentRequestDto)
                .sessionAttr(LOGIN_USER, mockUserDto)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void delete_mapping() throws Exception {
        mockMvc.perform(delete(MAPPING_PATH)
                .content(commentRequestDto)
                .sessionAttr(LOGIN_USER, mockUserDto)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));
    }
}