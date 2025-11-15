package com.Sunrise.DTO.Requests;

@lombok.Getter
@lombok.Setter
@lombok.AllArgsConstructor
public class CreateGroupChatRequest {
    private String chatName;
    private java.util.List<Long> memberIds;
}
