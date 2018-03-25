package base.opa.normal;

import base.opa.PriceSizeQueryResponse;

public interface OrderProcessorAlgorithm {
    void processOrder(int orderId, char side, int price, int size);

    boolean cancelOrder(int orderId);

    PriceSizeQueryResponse queryBuyers();

    PriceSizeQueryResponse querySellers();

    int querySize(int price);
    
}
