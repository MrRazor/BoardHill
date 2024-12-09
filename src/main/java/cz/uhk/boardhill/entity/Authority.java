package cz.uhk.boardhill.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "authorities")
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "authority", nullable = false)
    private String authority;

    @ManyToOne
    @JoinColumn(name = "username", nullable = false)
    private User user;

}