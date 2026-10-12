package org.ecommerce.product.util;

import org.ecommerce.product.dto.ProductRequest;
import org.ecommerce.product.dto.ProductResponse;
import org.ecommerce.product.dto.ProductServiceResponse;
import org.ecommerce.product.dto.ServiceStatus;

import java.util.List;

public class ServiceUtil {

    public static ProductServiceResponse getProductServiceResponse(int statusCode,
                                                                   boolean success,
                                                                   String statusMessage,
                                                                   List<ProductResponse> productResponseList) {
        ServiceStatus serviceStatus = ServiceStatus.builder()
                .statusCode(statusCode)
                .success(success)
                .message(statusMessage)
                .build();

        ProductServiceResponse response = ProductServiceResponse.builder()
                .serviceStatus(serviceStatus)
                .productResponse(productResponseList)
                .build();
        return response;
    }

    public static boolean validateProductRequest(ProductRequest request) {

        return false;
    }
}
