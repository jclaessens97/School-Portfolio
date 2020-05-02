package be.kdg.cluedobackend.dto;

import be.kdg.cluedobackend.model.chat.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private String playerName;
    private Integer playerId;
    private String content;
    private String date;

    public MessageDto(Message message) {
        if (message.getUser() != null){
            this.playerName = message.getUser().getUserName();
            this.date = dateToString(message.getTimestamp());
        }
        this.content = message.getContent();

    }


    public String dateToString(Date date){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(date);
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "playerName='" + playerName + '\'' +
                ", content='" + content + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
