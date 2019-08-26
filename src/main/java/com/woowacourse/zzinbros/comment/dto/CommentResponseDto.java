package com.woowacourse.zzinbros.comment.dto;

import com.woowacourse.zzinbros.comment.domain.Comment;

import java.time.LocalDateTime;

public class CommentResponseDto {
    Long commentId;
    Long authorId;
    String authorName;
    String contents;
    LocalDateTime createdDateTime;
    LocalDateTime updatedDateTime;

    public CommentResponseDto(Comment comment) {
        commentId = comment.getId();
        authorId = comment.getAuthor().getId();
        authorName = comment.getAuthor().getName();
        contents = comment.getContents();
        createdDateTime = comment.getCreatedDateTime();
        updatedDateTime = comment.getUpdatedDateTime();
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public LocalDateTime getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
