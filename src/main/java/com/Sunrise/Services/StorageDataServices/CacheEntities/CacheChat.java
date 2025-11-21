package com.Sunrise.Services.StorageDataServices.CacheEntities;

import com.Sunrise.Entities.Chat;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@lombok.Getter
@lombok.Setter
public class CacheChat extends Chat {

    private Map<Long, CacheChatMember> chatMembers = new ConcurrentHashMap<>(); // userId -> CacheChatMember

    public CacheChat(Long id, String name, Long createdBy, Boolean isGroup){
        super();
        this.id = id;
        this.name = name;
        this.createdBy = createdBy;
        this.isGroup = isGroup;
    }
    public CacheChat(Chat chat) {
        super();
        this.setId(chat.getId());
        this.setName(chat.getName());
        this.setCreatedBy(chat.getCreatedBy());
        this.setIsGroup(chat.getIsGroup());
        this.setCreatedAt(chat.getCreatedAt());
        this.setIsDeleted(chat.getIsDeleted());
    }

    // Вспомогательные методы
    public void addMember(Long userId, Boolean isAdmin) {
        var member = new CacheChatMember(userId, isAdmin);
        chatMembers.put(userId, member);
    }
    public void removeMember(Long userId) {
        chatMembers.remove(userId);
    }

    public boolean hasMember(Long userId) {
        return chatMembers.containsKey(userId);
    }
    public int getMemberCount() {
        return chatMembers.size();
    }

    public Boolean isAdmin(Long userId) {
        CacheChatMember member = chatMembers.get(userId);
        return member != null ? member.getIsAdmin() : false;
    }
    public Set<Long> getMemberIds() {
        return chatMembers.keySet();
    }
    public Set<Long> getAdminIds() {
        return chatMembers.entrySet().stream()
                .filter(entry -> entry.getValue().getIsAdmin())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public void setAdminRights(Long userId, Boolean isAdmin) {
        CacheChatMember member = chatMembers.get(userId);
        if (member != null) {
            member.setIsAdmin(isAdmin);
        }
    }

    // Новые полезные методы
    public CacheChatMember getMemberInfo(Long userId) {
        return chatMembers.get(userId);
    }
    public LocalDateTime getMemberJoinedAt(Long userId) {
        CacheChatMember member = chatMembers.get(userId);
        return member != null ? member.getJoinedAt() : null;
    }
    public void markMemberAsDeleted(Long userId) {
        CacheChatMember member = chatMembers.get(userId);
        if (member != null) {
            member.setIsDeleted(true);
        }
    }
    public void restoreMember(Long userId) {
        CacheChatMember member = chatMembers.get(userId);
        if (member != null) {
            member.setIsDeleted(false);
        }
    }
    public Set<Long> getActiveMemberIds() {
        return chatMembers.entrySet().stream()
                .filter(entry -> !entry.getValue().getIsDeleted())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
