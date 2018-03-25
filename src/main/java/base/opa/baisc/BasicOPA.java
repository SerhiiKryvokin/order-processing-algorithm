package base.opa.baisc;

import base.opa.PriceSizeQueryResponse;

public interface BasicOPA {
    void updateBook(BookEntryType bookEntryType, int price, int size);
    void processOrder(OrderType orderType, int size);
    PriceSizeQueryResponse getBestBid();
    PriceSizeQueryResponse getBestAsk();
    int querySize(int price);
    
    enum OrderType {
        buy, sell
    }
    
    enum BookEntryType {
        bid, ask
    }
}
