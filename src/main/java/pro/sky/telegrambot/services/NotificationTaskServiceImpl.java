package pro.sky.telegrambot.services;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.interfaces.NotificationTaskService;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class NotificationTaskServiceImpl implements NotificationTaskService {
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskServiceImpl(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Override
    public NotificationTask createNotificationTask(NotificationTask notificationTask) {
        if (notificationTask.equals(null)) {
            throw new IllegalArgumentException("Argument is null");
        }
        return notificationTaskRepository.save(notificationTask);
    }

    @Override
    public Collection<NotificationTask> getNotificationTasksByLocalDateTime(LocalDateTime localDateTime) {
        return notificationTaskRepository.findNotificationTasksByNotifyDateTime(localDateTime);
    }
}
