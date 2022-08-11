package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class NotificationTask {
    @Id
    @GeneratedValue
    private Long idTask;
    private LocalDateTime notifyDateTime;
    private String text;
    private Long idChat;

    public NotificationTask() {

    }

    public Long getIdTask() {
        return idTask;
    }

    public void setIdTask(Long idTask) {
        this.idTask = idTask;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getNotifyDateTime() {
        return notifyDateTime;
    }

    public void setNotifyDateTime(LocalDateTime notifyDateTime) {
        this.notifyDateTime = notifyDateTime;
    }

    public Long getIdChat() {
        return idChat;
    }

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return idTask.equals(that.idTask) && text.equals(that.text) && notifyDateTime.equals(that.notifyDateTime) && idChat.equals(that.idChat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTask, text, notifyDateTime, idChat);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "idTask=" + idTask +
                ", text='" + text + '\'' +
                ", notifyDateTime=" + notifyDateTime +
                ", idChat=" + idChat +
                '}';
    }
}
