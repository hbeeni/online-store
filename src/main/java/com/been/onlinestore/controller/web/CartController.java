package com.been.onlinestore.controller.web;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Positive;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.been.onlinestore.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/carts")
@Controller
public class CartController {

	private static final String COOKIE_NAME = "cart";
	private static final String COOKIE_PATH = "/";
	private static final String PRODUCT_PARTITION = "|";
	private static final String PRODUCT_PARTITION_REGEX = "\\|";
	private static final String PRODUCT_QUANTITY_PARTITION = ":";

	private final ProductService productService;

	@GetMapping
	public String getProductsInCart(@CookieValue(name = COOKIE_NAME, required = false) String cart, Model model) {
		log.info("cart = {}", cart);

		if (StringUtils.hasText(cart)) {
			Map<Long, Integer> productToQuantityMap = cookieValueToMap(cart);
			model.addAttribute("cart", productService.findCartOrderProductsForWeb(productToQuantityMap));
		}

		return "user/cart/cart";
	}

	@PostMapping("/increase")
	public String increaseProductToCart(
		@CookieValue(name = COOKIE_NAME, required = false) String cart,
		@RequestParam @Positive Long productId,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		log.info("cart = {}", cart);

		String newCart;

		if (StringUtils.hasText(cart)) {
			Map<Long, Integer> idToQuantityMap = cookieValueToMap(cart);

			Optional<Map.Entry<Long, Integer>> targetOptional = idToQuantityMap.entrySet().stream()
				.filter(entry -> entry.getKey().equals(productId))
				.findAny();

			if (targetOptional.isPresent()) {
				Map.Entry<Long, Integer> target = targetOptional.get();
				target.setValue(target.getValue() + 1);

				newCart = mapToCookieValue(idToQuantityMap);
			} else {
				newCart = cart + PRODUCT_PARTITION + productId + PRODUCT_QUANTITY_PARTITION + 1;
			}
		} else {
			newCart = productId + PRODUCT_QUANTITY_PARTITION + 1;
		}

		addCartCookie(response, newCart);
		log.info("after cart = {}", newCart);

		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}

	@PostMapping("/decrease")
	public String decreaseCartProduct(
		@CookieValue(name = COOKIE_NAME, required = false) String cart,
		@RequestParam @Positive Long productId,
		HttpServletResponse response
	) {
		log.info("before cart = {}", cart);

		if (StringUtils.hasText(cart)) {
			Map<Long, Integer> idToQuantityMap = cookieValueToMap(cart);
			String newCart = idToQuantityMap.entrySet().stream()
				.filter(entry -> !Objects.equals(entry.getKey(), productId))
				.map(entry -> entry.getKey() + PRODUCT_QUANTITY_PARTITION + entry.getValue())
				.collect(Collectors.joining(PRODUCT_PARTITION));
			newCart +=
				PRODUCT_PARTITION + productId + PRODUCT_QUANTITY_PARTITION + (idToQuantityMap.get(productId) - 1);

			log.info("after cart = {}", newCart);
			addCartCookie(response, newCart);
		}

		return "redirect:/carts";
	}

	@PostMapping("/delete")
	public String deleteCartProductAll(
		@CookieValue(name = COOKIE_NAME, required = false) String cart,
		@RequestParam @Positive Long productId,
		HttpServletResponse response
	) {
		log.info("before cart = {}", cart);

		if (StringUtils.hasText(cart)) {
			Map<Long, Integer> idToQuantityMap = cookieValueToMap(cart);
			String newCart = idToQuantityMap.entrySet().stream()
				.filter(entry -> !Objects.equals(entry.getKey(), productId))
				.map(entry -> entry.getKey() + PRODUCT_QUANTITY_PARTITION + entry.getValue())
				.collect(Collectors.joining(PRODUCT_PARTITION));

			log.info("after cart = {}", newCart);
			addCartCookie(response, newCart);
		}

		return "redirect:/carts";
	}

	/**
	 * @param cart 쿠키 값
	 * @return productId, quantity map
	 */
	private static Map<Long, Integer> cookieValueToMap(String cart) {
		return Arrays.stream(cart.split(PRODUCT_PARTITION_REGEX))
			.map(s -> s.split(PRODUCT_QUANTITY_PARTITION))
			.collect(Collectors.toMap(
					a -> Long.parseLong(a[0]),
					a -> Integer.parseInt(a[1]),
					Integer::sum
				)
			);
	}

	/**
	 * @param productToQuantityMap productId, quantity map
	 * @return 쿠키 값
	 */
	private static String mapToCookieValue(Map<Long, Integer> productToQuantityMap) {
		return productToQuantityMap.entrySet().stream()
			.map(entry -> entry.getKey() + PRODUCT_QUANTITY_PARTITION + entry.getValue())
			.collect(Collectors.joining(PRODUCT_PARTITION));
	}

	private static void addCartCookie(HttpServletResponse response, String cookieValue) {
		Cookie cookie = new Cookie(COOKIE_NAME, cookieValue);
		cookie.setMaxAge(7 * 24 * 60 * 60); //일주일
		cookie.setPath(COOKIE_PATH);
		response.addCookie(cookie);
	}
}
