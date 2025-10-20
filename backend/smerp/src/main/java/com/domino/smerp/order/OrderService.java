package com.domino.smerp.order;

import com.domino.smerp.common.dto.PageResponse;
import com.domino.smerp.order.dto.request.*;
import com.domino.smerp.order.dto.response.*;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    CreateOrderResponse createOrder(CreateOrderRequest request);

    PageResponse<ListOrderResponse> getOrders(SearchOrderRequest condition, Pageable pageable);

    DetailOrderResponse getDetailOrder(Long orderId);

    UpdateOrderResponse updateOrder(Long orderId, UpdateOrderRequest request);

    DeleteOrderResponse deleteOrder(Long orderId);

    List<SummaryOrderResponse> getSummaryOrder(SearchSummaryOrderRequest condition, Pageable pageable);

    CreateReturnOrderResponse createReturnOrder(CreateReturnOrderRequest request);

    List<SummaryReturnOrderResponse> getSummaryReturnOrders(
            SearchSummaryReturnOrderRequest condition, Pageable pageable);
}
