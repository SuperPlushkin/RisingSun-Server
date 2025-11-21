package com.Sunrise.Services.StorageDataServices.CacheEntities;

import java.time.LocalDateTime;

@lombok.Setter
@lombok.Getter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class CacheChatMember {
    private Long id;
    private LocalDateTime joinedAt = LocalDateTime.now();
    private Boolean isAdmin = false;
    private Boolean isDeleted = false;

    public CacheChatMember(Long id, Boolean isAdmin) {
        this.id = id;
        this.isAdmin = isAdmin;
        this.joinedAt = LocalDateTime.now();
        this.isDeleted = false;
    }
}
