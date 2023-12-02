package com.been.onlinestore.controller.seller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.service.OrderService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/seller/orders")
@RestController
public class SellerOrderApiController {

	private final OrderService orderService;

	@GetMapping
	public ResponseEntity<?> getOrders(
			@AuthenticationPrincipal PrincipalDetails principalDetails,
			@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ResponseEntity.ok(
				ApiResponse.pagination(orderService.findOrdersBySeller(principalDetails.id(), pageable)));
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<?> getOrder(
			@AuthenticationPrincipal PrincipalDetails principalDetails,
			@PathVariable Long orderId
	) {
		return ResponseEntity.ok(ApiResponse.success(orderService.findOrderBySeller(orderId, principalDetails.id())));
	}
}
