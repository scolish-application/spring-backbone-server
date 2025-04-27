package com.github.simaodiazz.schola.backbone.server.security.data.model;

import com.github.simaodiazz.schola.backbone.server.database.entity.EntitySuperclass;
import com.github.simaodiazz.schola.backbone.server.security.data.converter.AuthorityCollectionConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Entity
@RedisHash
@NoArgsConstructor
@AllArgsConstructor
@Data
public final class User extends EntitySuperclass implements UserDetails {

    @Column(length = 16, nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // This converter is required to compact all authorities in one string
    // Is the best way to simplicity without N:M relationship
    @Column
    @Convert(converter = AuthorityCollectionConverter.class)
    private Collection<? extends GrantedAuthority> authorities;

    public User(long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(id);
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }
}
