package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.interfaces.NotificationTaskService;
import pro.sky.telegrambot.model.NotificationTask;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private static final Pattern PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    private final NotificationTaskService notificationTaskService;

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

            /*
            Generate respond with greeting message via direct command "/start"
             */
            generateGreetingMessage(updates);

            /*
            Create new DB record of the notification task from the message according to the pattern rules
             */
            createNewDataBaseRecordOfNotificationTask(updates);

            /*
            Execute notification tasks according to the schedule time in DB records
             */
            executeNotificationTaskOnScheduledTime();
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /*
    This method generates respond with greeting message via direct command "/start"
    */
    private void generateGreetingMessage(List<Update> updates) {
        updates
                .stream()
                .map(Update::message)
                .filter(message -> message.text().equals("/start"))
                .map(Message::chat)
                .map(Chat::id)
                .forEach(chat -> {
                    SendMessage message = new SendMessage(chat, "You are welcome to the I'm watching you chat room!");
                    SendResponse response = telegramBot.execute(message);
                });
    }

    /*
    This method creates new DB record of the notification task from the message according to the pattern rules
    */
    private void createNewDataBaseRecordOfNotificationTask(List<Update> updates) {
        updates
                .stream()
                .map(Update::message)
                .filter(message -> (checkMessageToPattern(message.text()) == true))
                .forEach(message -> notificationTaskService.createNotificationTask(createNotificationTaskEntityFromTheMessageText(message)));
    }

    /*
    Execute notification tasks according to the schedule time in DB records
     */
    @Scheduled(cron = "0 0/1 * * * *")
    private void executeNotificationTaskOnScheduledTime() {
        if (!notificationTaskService
                .getNotificationTasksByLocalDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)).isEmpty()) {
            notificationTaskService
                    .getNotificationTasksByLocalDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES))
                    .stream()
                    .forEach(notificationTask -> {
                        SendMessage message = new SendMessage(notificationTask.getIdChat(), notificationTask.getText());
                        SendResponse response = telegramBot.execute(message);
                    });
        }
    }

    /*
    This method allows to check if the message matches to the pattern
     */
    private boolean checkMessageToPattern(String text) {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            return false;
        }
        return true;
    }

    /*
    This method allows to extract notification task entity from the message
     */
    private NotificationTask createNotificationTaskEntityFromTheMessageText(Message message) {
        String text = message.text();
        Matcher matcher = PATTERN.matcher(text);
        String date = null;
        String task = null;
        if (matcher.matches()) {
            date = matcher.group(1);
            task = matcher.group(3);
        }
        LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        NotificationTask notificationTask = new NotificationTask();
        notificationTask.setNotifyDateTime(localDateTime);
        notificationTask.setText(task);
        notificationTask.setIdChat(message.chat().id());
        return notificationTask;
    }
}
