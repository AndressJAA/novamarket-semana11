package pe.edu.upsjb.novamarket.presentation.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderFormViewTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rendersStrategyRatesForTheJavascriptPreview() throws Exception {
        mockMvc.perform(get("/orders/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("value=\"NEW\"")))
                .andExpect(content().string(containsString("data-rate=\"0.05\"")))
                .andExpect(content().string(containsString("value=\"FREQUENT\"")))
                .andExpect(content().string(containsString("data-rate=\"0.10\"")))
                .andExpect(content().string(containsString("value=\"UNIDENTIFIED\"")))
                .andExpect(content().string(containsString("data-rate=\"0\"")));
    }
}
