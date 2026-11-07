package pe.edu.upsjb.novamarket.presentation.dashboard;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.upsjb.novamarket.application.EventHistoryService;
import pe.edu.upsjb.novamarket.application.OrderService;

@Controller
public class DashboardController {

    private final OrderService orderService;
    private final EventHistoryService eventHistoryService;

    public DashboardController(OrderService orderService,
                               EventHistoryService eventHistoryService) {
        this.orderService = orderService;
        this.eventHistoryService = eventHistoryService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("summary", orderService.getDashboardSummary());
        model.addAttribute("eventSummary", eventHistoryService.getSummary());
        model.addAttribute("recentOrders", orderService.findRecent(5));
        return "dashboard";
    }
}
