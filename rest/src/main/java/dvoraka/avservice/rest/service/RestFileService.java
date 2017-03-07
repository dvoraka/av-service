package dvoraka.avservice.rest.service;

import dvoraka.avservice.server.service.RemoteFileService;
import org.springframework.validation.annotation.Validated;

/**
 * REST file service interface.
 */
@Validated
public interface RestFileService extends RemoteFileService {
}
