package com.been.onlinestore.controller.seller;

import com.been.onlinestore.controller.dto.ApiResponse;
import com.been.onlinestore.controller.dto.security.PrincipalDetails;
import com.been.onlinestore.service.OrderProductService;
import com.been.onlinestore.service.response.DeliveryStatusChangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Set;

@RequiredArgsConstructor
@RequestMapping("/api/seller/order-products")
@RestController
public class SellerOrderProductApiController {

    private final OrderProductService orderProductService;

    @PutMapping("/deliveries/prepare")
    public ResponseEntity<ApiResponse<DeliveryStatusChangeResponse>> prepareProducts(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam @NotNull Set<Long> orderProductIds
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderProductService.startPreparing(orderProductIds, principalDetails.id())));
    }

    @PutMapping("/deliveries/start")
    public ResponseEntity<ApiResponse<DeliveryStatusChangeResponse>> startDelivery(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam @NotNull Set<Long> orderProductIds
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderProductService.startDelivery(orderProductIds, principalDetails.id())));
    }

    @PutMapping("/deliveries/complete")
    public ResponseEntity<ApiResponse<DeliveryStatusChangeResponse>> completeDelivery(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam @NotNull Set<Long> orderProductIds
    ) {
        return ResponseEntity.ok(ApiResponse.success(orderProductService.completeDelivery(orderProductIds, principalDetails.id())));
    }
}
