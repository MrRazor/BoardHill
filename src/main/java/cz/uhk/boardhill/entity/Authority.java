package cz.uhk.boardhill.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@IdClass(AuthorityId.class)
@Entity
@Table(name = "authorities")
public class Authority {

    @Id
    @Column(name = "authority", nullable = false)
    private String authority;

    @Id
    @JoinColumn(name = "username", nullable = false)
    private String username;

}