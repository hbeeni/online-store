package com.been.onlinestore.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.CartProductRequest;
import com.been.onlinestore.service.ProductService;
import com.been.onlinestore.service.response.CartResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/carts")
@RestController
public class CartApiController {

	private static final String COOKIE_NAME = "cart";
	private static final String COOKIE_PATH = "/api/";
	private static final String PRODUCT_PARTITION = "|";
	private static final String PRODUCT_PARTITION_REGEX = "\\|";
	private static final String PRODUCT_QUANTITY_PARTITION = ":";

	private final ProductService productService;

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

	@GetMapping
	public ResponseEntity<ApiResponse<List<CartResponse>>> getProductsInCart(
			@CookieValue(name = COOKIE_NAME, required = false) String cart) {
		log.info("cart = {}", cart);

		if (StringUtils.hasText(cart)) {
			Map<Long, Integer> productToQuantityMap = cookieValueToMap(cart);
			return ResponseEntity.ok(ApiResponse.success(productService.findProductsInCart(productToQuantityMap)));
		}

		return ResponseEntity.ok(ApiResponse.success(List.of()));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> addProductToCart(
			@CookieValue(name = COOKIE_NAME, required = false) String cart,
			@RequestBody @Validated CartProductRequest.Create request,
			HttpServletResponse response
	) {
		log.info("cart = {}", cart);

		StringBuilder newCart = new StringBuilder();

		if (StringUtils.hasText(cart)) {
			newCart.append(cart).append(PRODUCT_PARTITION);
		}

		newCart.append(request.productId()).append(PRODUCT_QUANTITY_PARTITION).append(request.productQuantity());
		addCartCookie(response, newCart.toString());

		log.info("after cart = {}", newCart);

		return ResponseEntity.ok(ApiResponse.success());
	}

	@PutMapping("/products/{cartProductId}")
	public ResponseEntity<ApiResponse<Void>> updateProductInCart(
			@CookieValue(name = COOKIE_NAME, required = false) String cart,
			@PathVariable Long cartProductId,
			@RequestBody @Validated CartProductRequest.Update request,
			HttpServletResponse response
	) {
		log.info("before cart = {}", cart);

		if (StringUtils.hasText(cart)) {
			Map<Long, Integer> idToQuantityMap = cookieValueToMap(cart);

			Optional<Map.Entry<Long, Integer>> targetOptional = idToQuantityMap.entrySet().stream()
					.filter(entry -> entry.getKey().equals(cartProductId))
					.findAny();

			if (targetOptional.isPresent()) {
				Map.Entry<Long, Integer> target = targetOptional.get();
				target.setValue(request.productQuantity());

				String newCart = mapToCookieValue(idToQuantityMap);
				addCartCookie(response, newCart);

				log.info("after cart = {}", newCart);
			}
		}

		return ResponseEntity.ok(ApiResponse.success());
	}

	@DeleteMapping
	public ResponseEntity<ApiResponse<Void>> deleteCart(HttpServletResponse response) {
		Cookie cookie = new Cookie(COOKIE_NAME, null);
		cookie.setMaxAge(0);
		cookie.setPath(COOKIE_PATH);
		response.addCookie(cookie);
		return ResponseEntity.ok(ApiResponse.success());
	}

	@DeleteMapping("/products")
	public ResponseEntity<ApiResponse<Void>> deleteCartProducts(
			@CookieValue(name = COOKIE_NAME, required = false) String cart,
			@RequestParam Set<Long> ids,
			HttpServletResponse response
	) {
		log.info("before cart = {}", cart);

		if (StringUtils.hasText(cart)) {
			Map<Long, Integer> idToQuantityMap = cookieValueToMap(cart);
			String newCart = idToQuantityMap.entrySet().stream()
					.filter(entry -> !ids.contains(entry.getKey()))
					.map(entry -> entry.getKey() + PRODUCT_QUANTITY_PARTITION + entry.getValue())
					.collect(Collectors.joining(PRODUCT_PARTITION));

			log.info("after cart = {}", newCart);
			addCartCookie(response, newCart);
		}

		return ResponseEntity.ok(ApiResponse.success());
	}
}
