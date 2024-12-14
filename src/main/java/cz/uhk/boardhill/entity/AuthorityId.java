package cz.uhk.boardhill.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class AuthorityId implements Serializable {
    private String authority;
    private String username;
}
