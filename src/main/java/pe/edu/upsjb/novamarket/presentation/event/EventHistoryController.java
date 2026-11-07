package pe.edu.upsjb.novamarket.presentation.event;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.upsjb.novamarket.application.EventHistoryService;

@Controller
@RequestMapping("/events")
public class EventHistoryController {

    private final EventHistoryService eventHistoryService;

    public EventHistoryController(EventHistoryService eventHistoryService) {
        this.eventHistoryService = eventHistoryService;
    }

    @GetMapping
    public String history(Model model) {
        model.addAttribute("history", eventHistoryService.findAll());
        model.addAttribute("summary", eventHistoryService.getSummary());
        return "events/history";
    }
}
