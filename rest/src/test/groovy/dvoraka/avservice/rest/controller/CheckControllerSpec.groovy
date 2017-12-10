package dvoraka.avservice.rest.controller

import com.fasterxml.jackson.databind.ObjectMapper
import dvoraka.avservice.common.data.AvMessage
import dvoraka.avservice.common.util.Utils
import dvoraka.avservice.rest.service.RestService
import org.springframework.http.MediaType
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Ignore
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class CheckControllerSpec extends Specification {

    MockMvc mockMvc
    RestService service

    String base = CheckController.MAPPING


    def setup() {
        service = Stub()
        mockMvc = MockMvcBuilders.standaloneSetup(new CheckController(service))
                .build()
    }

    def "test about"() {
        setup:
            ResultActions response = mockMvc.perform(
                    get(base + "/about"))

        expect:
            response
                    .andExpect(status().isOk())
                    .andExpect(content().string("AV checking"))
    }

    def "check AV message"() {
        setup:
            AvMessage message = Utils.genMessage()

            ObjectMapper mapper = new ObjectMapper()
            String content = mapper.writeValueAsString(message)

            ResultActions response = mockMvc.perform(
                    post(base + "/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))

        expect:
            response
                    .andExpect(status().isAccepted())
    }

    @Ignore("broken")
    def "check AVMessage without ID"() {
        setup:
            AvMessage message = Utils.genMessage()
            ReflectionTestUtils.setField(message, "id", null, null)

            ObjectMapper mapper = new ObjectMapper()
            String content = mapper.writeValueAsString(message)

            ResultActions response = mockMvc.perform(
                    post(base + "/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(content))

        expect: "error response"
            response
                    .andExpect(status().is4xxClientError())
    }
}
