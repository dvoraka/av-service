package dvoraka.avservice.rest.controller

import dvoraka.avservice.stats.StatsService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


class StatsControllerSpec extends Specification {

    MockMvc mockMvc
    StatsService service

    String base = StatsController.MAPPING


    def setup() {
        service = Mock()
        mockMvc = MockMvcBuilders.standaloneSetup(new StatsController(service))
                .build()
    }

    def "test about"() {
        when:
            ResultActions resultActions = mockMvc.perform(
                    get(base + "/about"))

        then:
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().string("AV statistics"))
    }

    def "today count"() {
        given:
            long count = 99
            service.todayCount() >> count

        when:
            ResultActions resultActions = mockMvc.perform(
                    get(base + "/today"))

        then:
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(content().string(count.toString()))
    }
}
