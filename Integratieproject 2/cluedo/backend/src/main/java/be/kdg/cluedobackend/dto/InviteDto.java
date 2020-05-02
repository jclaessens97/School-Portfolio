package be.kdg.cluedobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InviteDto {
    private int cluedoId;
    private String invitedUser;
    private String inviter;
}
