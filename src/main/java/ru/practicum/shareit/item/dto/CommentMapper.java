package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommentMapper {

    public Comment toCommentFromCommentCreateDto(CommentCreateDto commentCreateDto) {
        Comment comment = new Comment();
        comment.setText(commentCreateDto.getText());
        return comment;
    }

    public CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setText(comment.getText());
        commentDto.setId(comment.getId());
        commentDto.setCreated(comment.getCreated());
        commentDto.setAuthorName(comment.getAuthor().getName());
        return commentDto;
    }

    public List<CommentDto> toListCommentsDto(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            CommentDto commentDto = toCommentDto(comment);
            commentDtos.add(commentDto);
        }
        return commentDtos;
    }
}
