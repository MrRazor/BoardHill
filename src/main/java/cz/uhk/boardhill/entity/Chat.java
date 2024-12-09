package cz.uhk.boardhill.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Set;

@Data
@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @OneToMany(mappedBy = "chat")
    private Set<Message> messages;

    @OneToMany(mappedBy = "chat")
    private Set<ChatUser> chatUsers;

}