package cz.uhk.boardhill.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @OneToMany(mappedBy = "user")
    private Set<Authority> authorities;

    @OneToMany(mappedBy = "user")
    private Set<Message> messages;

    @OneToMany(mappedBy = "user")
    private Set<ChatUser> chatUsers;
}