package cz.uhk.boardhill.dto;

import cz.uhk.boardhill.entity.Authority;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {

    private String username;

    private String password;

    private boolean enabled;

    private Set<Authority> authorities;

}
