package base.opa;

public interface OrderProcessorAlgorithm {
    void processOrder(int orderId, char side, int price, int size);

    boolean cancelOrder(int orderId);

    PriceSizeQueryResponse queryBuyers();

    PriceSizeQueryResponse querySellers();

    int querySize(int price);

    class PriceSizeQueryResponse {
        public int price;
        public int size;

        public PriceSizeQueryResponse(int price, int size) {
            this.price = price;
            this.size = size;
        }
    }
}
