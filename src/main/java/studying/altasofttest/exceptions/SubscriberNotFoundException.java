package studying.altasofttest.exceptions;

public class SubscriberNotFoundException extends RuntimeException {
    public SubscriberNotFoundException(Long id){
        super("Subscriber with id=" + id + " not found");
    }
}
