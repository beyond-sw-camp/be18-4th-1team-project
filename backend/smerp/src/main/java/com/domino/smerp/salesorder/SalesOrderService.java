package com.domino.smerp.salesorder;

import com.domino.smerp.common.dto.PageResponse;
import com.domino.smerp.salesorder.dto.request.CreateSalesOrderRequest;
import com.domino.smerp.salesorder.dto.request.SearchSalesOrderRequest;
import com.domino.smerp.salesorder.dto.request.SearchSummarySalesOrderRequest;
import com.domino.smerp.salesorder.dto.request.UpdateSalesOrderRequest;
import com.domino.smerp.salesorder.dto.response.*;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface SalesOrderService {
    CreateSalesOrderResponse createSalesOrder(CreateSalesOrderRequest request);

    PageResponse<ListSalesOrderResponse> getSalesOrders(SearchSalesOrderRequest condition, Pageable pageable);

    DetailSalesOrderResponse getDetailSalesOrder(Long salesOrderId);

    UpdateSalesOrderResponse updateSalesOrder(Long salesOrderId, UpdateSalesOrderRequest request);

    DeleteSalesOrderResponse deleteSalesOrder(Long salesOrderId);

    List<SummarySalesOrderResponse> getSummarySalesOrder(SearchSummarySalesOrderRequest condition, Pageable pageable);
}
