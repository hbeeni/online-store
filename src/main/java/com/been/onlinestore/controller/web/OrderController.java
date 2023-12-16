package com.been.onlinestore.controller.web;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.been.onlinestore.controller.dto.OrderRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.service.AddressService;
import com.been.onlinestore.service.OrderService;
import com.been.onlinestore.service.ProductService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/orders")
@Controller
public class OrderController {

	private final AddressService addressService;
	private final ProductService productService;
	private final OrderService orderService;

	@GetMapping
	public String getOrders(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
		Model model
	) {
		model.addAttribute("orders", orderService.findOrdersByOrderer(principalDetails.id(), pageable));
		return "user/order/orders";
	}

	@GetMapping("/{orderId}")
	public String getOrder(
		@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long orderId, Model model
	) {
		model.addAttribute("order", orderService.findOrderByOrderer(orderId, principalDetails.id()));
		return "user/order/order";
	}

	@GetMapping("/new")
	public String addOrderForm(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@CookieValue(name = "order") String order,
		@ModelAttribute("order") OrderRequest request,
		Model model
	) {
		model.addAttribute("addresses", addressService.findAddresses(principalDetails.id()));
		model.addAttribute("orderInfo", productService.findCartOrderProductsForWeb(cookieValueToMap(order)));
		return "user/order/addForm";
	}

	@PostMapping("/new")
	public String order(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@CookieValue(name = "order") String order,
		@ModelAttribute("order") @Validated OrderRequest request,
		BindingResult bindingResult,
		HttpServletResponse response,
		Model model
	) {
		if (bindingResult.hasFieldErrors()) {
			model.addAttribute("addresses", addressService.findAddresses(principalDetails.id()));
			model.addAttribute("orderInfo", productService.findCartOrderProductsForWeb(cookieValueToMap(order)));
			return "user/order/addForm";
		}

		orderService.order(principalDetails.id(), request.toServiceRequest());
		expireCookie(response);
		return "redirect:/orders";
	}

	@PostMapping("/{orderId}/cancel")
	public String cancelOrder(
		@AuthenticationPrincipal PrincipalDetails principalDetails,
		@PathVariable Long orderId
	) {
		orderService.cancelOrder(orderId, principalDetails.id());
		return "redirect:/orders";
	}

	/**
	 * @param order 쿠키 값
	 * @return productId, quantity map
	 */
	private static Map<Long, Integer> cookieValueToMap(String order) {
		return Arrays.stream(order.split("\\|"))
			.map(s -> s.split(":"))
			.collect(Collectors.toMap(
					a -> Long.parseLong(a[0]),
					a -> Integer.parseInt(a[1])
				)
			);
	}

	private static void expireCookie(HttpServletResponse response) {
		Cookie cartCookie = new Cookie("cart", null);
		Cookie orderCookie = new Cookie("order", null);

		cartCookie.setMaxAge(0);
		cartCookie.setPath("/");
		orderCookie.setMaxAge(0);
		orderCookie.setPath("/");

		response.addCookie(cartCookie);
		response.addCookie(orderCookie);
	}
}
