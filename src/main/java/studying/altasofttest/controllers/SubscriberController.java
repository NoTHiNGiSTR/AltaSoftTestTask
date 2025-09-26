package studying.altasofttest.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studying.altasofttest.dto.SubscriberDto;
import studying.altasofttest.services.SubscriberService;

import javax.validation.Valid;

@RestController
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping("/subscriber/{id}")
    public ResponseEntity<SubscriberDto> getSubscriber(@PathVariable("id") Long id){
        SubscriberDto applicant = subscriberService.getSubscriber(id);
        return ResponseEntity.ok().body(applicant);
    }

    @PutMapping("/subscriber")
    public ResponseEntity<SubscriberDto> createSubscriber(@Valid @RequestBody SubscriberDto subscriberDto){
        SubscriberDto subscriber = subscriberService.createSubscriber(subscriberDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriber);
    }

    @PatchMapping("/subscriber")
    public ResponseEntity<SubscriberDto> editSubscriber(@RequestBody SubscriberDto subscriberDto){
        return ResponseEntity.ok().body(subscriberService.editSubscriber(subscriberDto));
    }

    @DeleteMapping("/subscriber/{id}")
    public ResponseEntity<Void> deleteSubscriber(@PathVariable("id") Long id){
        subscriberService.deleteSubscriber(id);
        return ResponseEntity.noContent().build();
    }
}
