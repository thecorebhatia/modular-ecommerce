package org.ecommerce.order.util;

import org.ecommerce.order.dto.OrderResponse;
import org.ecommerce.order.dto.OrderServiceResponse;
import org.ecommerce.order.dto.ServiceStatus;

import java.util.List;

public class ServiceUtil {

    public static OrderServiceResponse getOrderServiceResponse(int statusCode,
                                                                 boolean success,
                                                                 String statusMessage,
                                                                 List<OrderResponse> orderResponseList) {
        ServiceStatus serviceStatus = ServiceStatus.builder()
                .statusCode(statusCode)
                .success(success)
                .message(statusMessage)
                .build();

        OrderServiceResponse response = OrderServiceResponse.builder()
                .serviceStatus(serviceStatus)
                .orderResponse(orderResponseList)
                .build();
        return response;
    }
}
