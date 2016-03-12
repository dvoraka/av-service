package dvoraka.avservice.service;

import dvoraka.avservice.data.MessageStatus;

/**
 * REST service.
 */
public interface RestService {

    MessageStatus messageStatus(String id);
}
