package studying.altasofttest.services;

import studying.altasofttest.dto.SubscriberDto;

public interface SubscriberService {
    SubscriberDto getSubscriber(Long id);
    SubscriberDto createSubscriber(SubscriberDto subscriberDto);
    void deleteSubscriber(Long id);
    SubscriberDto editSubscriber(SubscriberDto subscriberDto);
}
