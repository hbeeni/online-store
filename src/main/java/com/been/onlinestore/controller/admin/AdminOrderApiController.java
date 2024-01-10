package com.been.onlinestore.controller.admin;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.been.onlinestore.common.ApiResponse;
import com.been.onlinestore.repository.querydsl.order.OrderSearchCondition;
import com.been.onlinestore.service.admin.AdminOrderService;
import com.been.onlinestore.service.dto.response.OrderResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/admin/orders")
@RestController
public class AdminOrderApiController {

	private final AdminOrderService adminOrderService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(
		@ModelAttribute @Validated OrderSearchCondition cond,
		@PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return ResponseEntity.ok(ApiResponse.pagination(adminOrderService.findOrders(cond, pageable)));
	}

	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long orderId) {
		return ResponseEntity.ok(ApiResponse.success(adminOrderService.findOrder(orderId)));
	}

	@PutMapping("/{orderId}/deliveries/prepare")
	public ResponseEntity<ApiResponse<Map<String, Long>>> prepareOrders(@PathVariable Long orderId) {
		return ResponseEntity.ok(ApiResponse.successId(adminOrderService.startPreparing(orderId)));
	}

	@PutMapping("/{orderId}/deliveries/start")
	public ResponseEntity<ApiResponse<Map<String, Long>>> startDelivery(@PathVariable Long orderId) {
		return ResponseEntity.ok(ApiResponse.successId(adminOrderService.startDelivery(orderId)));
	}

	@PutMapping("/{orderId}/deliveries/complete")
	public ResponseEntity<ApiResponse<Map<String, Long>>> completeDelivery(@PathVariable Long orderId) {
		return ResponseEntity.ok(ApiResponse.successId(adminOrderService.completeDelivery(orderId)));
	}

	@PutMapping("/{orderId}/cancel")
	public ResponseEntity<ApiResponse<Map<String, Long>>> cancelOrder(@PathVariable Long orderId) {
		return ResponseEntity.ok(ApiResponse.successId(adminOrderService.cancelOrder(orderId)));
	}
}
