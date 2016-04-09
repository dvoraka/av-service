package dvoraka.avservice.rest

import com.fasterxml.jackson.databind.ObjectMapper
import dvoraka.avservice.common.Utils
import dvoraka.avservice.common.data.AVMessage
import dvoraka.avservice.common.data.MessageStatus
import dvoraka.avservice.service.RestService
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
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
        ResultActions response = mockMvc.perform(
                get("/"))

        expect:
        response
                .andExpect(status().isOk())
                .andExpect(content().string("AV service"))
    }

    def "test mesageStatus(String)"() {
        setup:
        MessageStatus messageStatus = MessageStatus.WAITING
        String messageId = 'TID'
        RestService service = Stub()
        service.messageStatus(messageId) >> messageStatus

        mockMvc = MockMvcBuilders.standaloneSetup(
                new AVController(restService: service)).build()

        ResultActions response = mockMvc.perform(
                get("/msg-status/${messageId}"))

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
        String messageId = 'TID'
        String serviceId = "SID"
        RestService service = Stub()
        service.messageStatus(messageId, serviceId) >> messageStatus

        mockMvc = MockMvcBuilders.standaloneSetup(
                new AVController(restService: service)).build()

        ResultActions response = mockMvc.perform(
                get("/msg-status/${messageId}/${serviceId}"))

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
        ResultActions response = mockMvc.perform(
                get("/msg-service-id/${messageId}"))

        expect:
        response
                .andExpect(status().isOk())
                .andExpect(content().string(serviceId))
    }

    def "test messageCheck(AVMessage)"() {
        setup:
        AVMessage message = Utils.genNormalMessage()
        RestService service = Mock()

        mockMvc = MockMvcBuilders.standaloneSetup(
                new AVController(restService: service)).build()

        ObjectMapper mapper = new ObjectMapper()
        String content = mapper.writeValueAsString(message)

        ResultActions response = mockMvc.perform(
                post("/msg-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))

        expect:
        response
                .andExpect(status().isAccepted())
    }

    def "test getResponse(String)"() {
        setup:
        String messageId = "TEST-ID"
        AVMessage responseMsg = Utils.genNormalMessage()
        RestService service = Stub()
        service.getResponse(messageId) >> responseMsg

        mockMvc = MockMvcBuilders.standaloneSetup(
                new AVController(restService: service)).build()

        ResultActions response = mockMvc.perform(
                get("/get-response/${messageId}"))

        ObjectMapper mapper = new ObjectMapper()
        String expectedContent = mapper.writeValueAsString(responseMsg)

        expect:
        response
                .andExpect(status().isOk())
                .andExpect(content().string(expectedContent))
    }
}
