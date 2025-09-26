package studying.altasofttest.services.implementations;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import studying.altasofttest.dto.SubscriberDto;
import studying.altasofttest.exceptions.SubscriberNotFoundException;
import studying.altasofttest.exceptions.InvalidDataException;
import studying.altasofttest.factories.SubscriberFactory;
import studying.altasofttest.models.Subscriber;
import studying.altasofttest.repositories.SubscriberRepository;
import studying.altasofttest.services.SubscriberService;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final SubscriberFactory subscriberFactory;

    public SubscriberServiceImpl(SubscriberRepository subscriberRepository, SubscriberFactory subscriberFactory) {
        this.subscriberRepository = subscriberRepository;
        this.subscriberFactory = subscriberFactory;
    }

    @Override
    public SubscriberDto getSubscriber(Long id) {
        Subscriber subscriber = subscriberRepository.findById(id).orElseThrow(() -> new SubscriberNotFoundException(id));
        return subscriberFactory.toDto(subscriber);
    }

    @Transactional
    @Override
    public SubscriberDto createSubscriber(SubscriberDto subscriberDto) {
        Subscriber subscriber = subscriberFactory.toEntity(subscriberDto);
        Subscriber savedSubscriber = subscriberRepository.save(subscriber);
        return subscriberFactory.toDto(savedSubscriber);
    }

    @Transactional
    @Override
    public void deleteSubscriber(Long id) {
        try {
            subscriberRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex){
            throw new SubscriberNotFoundException(id);
        }
    }

    @Transactional
    @Override
    public SubscriberDto editSubscriber(SubscriberDto subscriberDto) {
        Subscriber subscriber = subscriberRepository.findById(subscriberDto.getId())
                .orElseThrow(() -> new SubscriberNotFoundException(subscriberDto.getId()));
        checkValidationAndEditSubscriber(subscriberDto, subscriber);
        subscriberRepository.save(subscriber);
        return subscriberFactory.toDto(subscriber);
    }


    private void checkValidationAndEditSubscriber(SubscriberDto subscriberDto, Subscriber subscriber){
        Map<String, String> errors = new LinkedHashMap<>();
        if (subscriberDto.getEmail() != null){
            if (subscriberDto.getEmail().isEmpty()){
                errors.put("email", "Email must not be empty");
            } else if (!EMAIL_PATTERN.matcher(subscriberDto.getEmail()).matches()){
                errors.put("email", "Invalid email format");
            }
            else subscriber.setEmail(subscriberDto.getEmail());
        }

        if (subscriberDto.getFullName() != null){
            if (subscriberDto.getFullName().isEmpty()){
                errors.put("fullName", "FullName must not be empty");
            } else subscriber.setFullName(subscriberDto.getFullName());
        }

        if (subscriberDto.getCity() != null){
            if (subscriberDto.getCity().isEmpty()){
                subscriber.setCity("Не важно");
            }
            subscriber.setCity(subscriberDto.getCity());
        }
        if (subscriberDto.getDesiredPosition() != null){
            if (subscriberDto.getDesiredPosition().isEmpty()) {
                errors.put("desiredPosition", "Desired position must not be empty");
            } else {
                subscriber.setDesiredPosition(subscriberDto.getDesiredPosition());
            }
        }
        if (!errors.isEmpty()){
            throw new InvalidDataException(errors);
        }
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
            Pattern.CASE_INSENSITIVE
    );
}
