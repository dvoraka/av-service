package dvoraka.avservice.rest

import com.fasterxml.jackson.databind.ObjectMapper
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.service.RestService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * AV controller test.
 */
class AVControllerSpec extends Specification {

    MockMvc mockMvc;


    def setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AVController()).build()
    }

    def "test info"() {
        setup:
        ResultActions response = mockMvc.perform(get("/"))

        expect:
        response
                .andExpect(status().isOk())
                .andExpect(content().string("AV service"))
    }

    def "test mesageStatus(String)"() {
        setup:
        MessageStatus messageStatus = MessageStatus.WAITING
        RestService service = Stub()
        service.messageStatus(_) >> messageStatus

        mockMvc = MockMvcBuilders.standaloneSetup(
                new AVController(restService: service)).build()

        String messageId = 'TID'
        ResultActions response = mockMvc.perform(get("/msg-status/${messageId}"))

        ObjectMapper mapper = new ObjectMapper()
        String expectedContent = mapper.writeValueAsString(messageStatus)

        expect:
        response
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent))
    }

    def "test mesageStatus(String, String)"() {
        setup:
        MessageStatus messageStatus = MessageStatus.WAITING
        RestService service = Stub()
        service.messageStatus(_, _) >> messageStatus

        mockMvc = MockMvcBuilders.standaloneSetup(
                new AVController(restService: service)).build()

        String messageId = 'TID'
        ResultActions response = mockMvc.perform(get("/msg-status/${messageId}"))

        ObjectMapper mapper = new ObjectMapper()
        String expectedContent = mapper.writeValueAsString(messageStatus)

        expect:
        response
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent))
    }

    def "test messageServiceId(String)"() {
        setup:
        String serviceId = "TEST-SERVICE"
        RestService service = Stub()
        service.messageServiceId(_) >> serviceId

        mockMvc = MockMvcBuilders.standaloneSetup(
                new AVController(restService: service)).build()

        String messageId = 'TID'
        ResultActions response = mockMvc.perform(get("/msg-service-id/${messageId}"))

        expect:
        response
                .andExpect(status().isOk())
                .andExpect(content().string(serviceId))
    }
}
