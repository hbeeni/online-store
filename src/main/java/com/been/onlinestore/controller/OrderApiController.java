package com.been.onlinestore.controller;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.OrderRequest;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.service.OrderService;
import com.been.onlinestore.service.response.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderApiController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.pagination(orderService.findOrdersByOrderer(principalDetails.id(), pageable)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.success(orderService.findOrderByOrderer(orderId, principalDetails.id())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> order(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Validated OrderRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(bindingResult));
        }

        return ResponseEntity.ok(ApiResponse.successId(orderService.order(principalDetails.id(), request.toServiceRequest())));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Map<String, Long>>> cancelOrder(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(ApiResponse.successId(orderService.cancelOrder(orderId, principalDetails.id())));
    }
}
