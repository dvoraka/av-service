package dvoraka.avservice.service;

import dvoraka.avservice.MessageStatus;

/**
 * REST service.
 */
public interface RestService {

    MessageStatus messageStatus(String id);
}
