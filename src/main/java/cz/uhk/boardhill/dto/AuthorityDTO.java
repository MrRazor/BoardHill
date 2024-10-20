package cz.uhk.boardhill.dto;

import cz.uhk.boardhill.entity.User;
import lombok.Data;

@Data
public class AuthorityDTO {

    private Long id;

    private String authority;

    private User user;

}
