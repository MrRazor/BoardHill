package cz.uhk.boardhill.dto;

import cz.uhk.boardhill.entity.*;
import org.mapstruct.Mapper;

@Mapper
public interface ChatMapper {

    ChatDTO chatToChatDTO(Chat chat);
    Chat chatDTOToChat(ChatDTO chatDTO);

    MessageDTO messageToMessageDTO(Message message);
    Message messageDTOToMessage(MessageDTO messageDTO);

    ChatUserDTO chatUserToChatUserDTO(ChatUser chatUser);
    ChatUser chatUserDTOToChatUser(ChatUserDTO chatUserDTO);

    UserDTO userToUserDTO(User user);
    User userDTOToUser(User userDTO);

    AuthorityDTO authorityToAuthorityDTO(Authority authority);
    Authority authorityDTOToAuthority(AuthorityDTO authorityDTO);
}