package pe.edu.upsjb.novamarket.presentation.order;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.upsjb.novamarket.application.DiscountCalculator;
import pe.edu.upsjb.novamarket.application.EventHistoryService;
import pe.edu.upsjb.novamarket.application.OrderConfirmationResult;
import pe.edu.upsjb.novamarket.application.OrderConfirmationService;
import pe.edu.upsjb.novamarket.application.OrderNotFoundException;
import pe.edu.upsjb.novamarket.application.OrderService;
import pe.edu.upsjb.novamarket.domain.model.ClientCategory;
import pe.edu.upsjb.novamarket.domain.model.Order;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final DiscountCalculator discountCalculator;
    private final OrderConfirmationService orderConfirmationService;
    private final EventHistoryService eventHistoryService;

    public OrderController(OrderService orderService,
                           DiscountCalculator discountCalculator,
                           OrderConfirmationService orderConfirmationService,
                           EventHistoryService eventHistoryService) {
        this.orderService = orderService;
        this.discountCalculator = discountCalculator;
        this.orderConfirmationService = orderConfirmationService;
        this.eventHistoryService = eventHistoryService;
    }

    @ModelAttribute("categories")
    public ClientCategory[] categories() {
        return ClientCategory.values();
    }

    @ModelAttribute("discountRates")
    public Map<ClientCategory, BigDecimal> discountRates() {
        return discountCalculator.availableRates();
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders/list";
    }

    @GetMapping("/new")
    public String showForm(Model model) {
        if (!model.containsAttribute("orderForm")) {
            model.addAttribute("orderForm", new OrderForm());
        }
        return "orders/form";
    }

    @GetMapping("/{orderId}")
    public String detail(@PathVariable long orderId, Model model) {
        Order order = orderService.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        model.addAttribute("order", order);
        model.addAttribute(
                "eventHistory",
                eventHistoryService.findByOrderId(orderId).orElse(null));
        return "orders/detail";
    }

    @PostMapping
    public String register(
            @Valid @ModelAttribute("orderForm") OrderForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "orders/form";
        }

        Order order = orderService.register(form.toCommand());
        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Pedido #" + order.id() + " registrado correctamente.");
        return "redirect:/orders";
    }

    @PostMapping("/{orderId}/confirm")
    public String confirm(@PathVariable long orderId,
                          RedirectAttributes redirectAttributes) {
        try {
            OrderConfirmationResult result = orderConfirmationService.confirm(orderId);
            if (result.newlyConfirmed()) {
                redirectAttributes.addFlashAttribute(
                        "successMessage",
                        "Pedido confirmado. Evento "
                                + result.publishedEvent().eventId()
                                + " procesado por "
                                + result.observerResults().size()
                                + " observadores.");
            } else {
                redirectAttributes.addFlashAttribute(
                        "warningMessage",
                        "El pedido ya estaba confirmado. No se generaron eventos ni acciones duplicadas.");
            }
        } catch (OrderNotFoundException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
            return "redirect:/orders";
        }
        return "redirect:/orders/" + orderId;
    }
}
