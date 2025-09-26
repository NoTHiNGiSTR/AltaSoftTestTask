package studying.altasofttest.factories;

import org.springframework.stereotype.Component;
import studying.altasofttest.dto.SubscriberDto;
import studying.altasofttest.models.Subscriber;

@Component
public class SubscriberFactory {


    public SubscriberDto toDto(Subscriber subscriber){
        return SubscriberDto.builder()
                .id(subscriber.getId())
                .email(subscriber.getEmail())
                .fullName(subscriber.getFullName())
                .city(subscriber.getCity())
                .desiredPosition(subscriber.getDesiredPosition())
                .build();
    }

    public Subscriber toEntity(SubscriberDto subscriberDto){
        return Subscriber.builder()
                .email(subscriberDto.getEmail())
                .fullName(subscriberDto.getFullName())
                .city(subscriberDto.getCity() == null || subscriberDto.getCity().isEmpty()
                        ? "Не важно"
                        : subscriberDto.getCity())
                .desiredPosition(subscriberDto.getDesiredPosition())
                .build();
    }


}
