package com.been.onlinestore.service.response;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record DeliveryStatusChangeResponse(
		Set<Long> notFountOrderProductIds,
		Set<Long> succeededOrderProductIds,
		Set<Long> failedOrderProductIds
) {

	public static DeliveryStatusChangeResponse of(Set<Long> notFountOrderProductIds, Set<Long> succeededOrderProductIds,
			Set<Long> failedOrderProductIds) {
		return new DeliveryStatusChangeResponse(notFountOrderProductIds, succeededOrderProductIds,
				failedOrderProductIds);
	}
}
