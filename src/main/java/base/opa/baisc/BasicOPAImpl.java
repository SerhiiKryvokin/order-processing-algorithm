package base.opa.baisc;

import base.opa.PriceSizeQueryResponse;

import java.util.*;

import static base.opa.baisc.BasicOPA.BookEntryType.ask;
import static base.opa.baisc.BasicOPA.BookEntryType.bid;
import static base.opa.baisc.BasicOPA.OrderType.buy;
import static base.opa.baisc.BasicOPA.OrderType.sell;

public class BasicOPAImpl implements BasicOPA {
    
    private HashMap<Integer, Integer> sizeAtPrice = new HashMap<>();

    private TreeSet<Integer> bidPrices = new TreeSet<>(Comparator.reverseOrder());

    private TreeSet<Integer> askPrices = new TreeSet<>();

    @Override
    public void updateBook(BookEntryType bookEntryType, int price, int size) {
        if (price < 0) {
            throw new IllegalArgumentException("Price must be positive integer");
        }
        if (bookEntryType == null) {
            throw new IllegalArgumentException("Book entry type is not specified");
        }
        if (bookEntryType == ask) {
            updateAsk(price, size);
        } else if (bookEntryType == bid) {
            updateBid(price, size);
        }
    }

    @Override
    public void processOrder(OrderType orderType, int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Size must be positive integer");
        }
        if (orderType == null) {
            throw new IllegalArgumentException("Order type is not specified");
        }
        if (orderType == sell) {
            processSellOrder(size);
        } else if (orderType == buy) {
            processBuyOrder(size);
        }
    }

    @Override
    public PriceSizeQueryResponse getBestBid() {
        if (bidPrices.isEmpty()) return null;
        int maxBidPrice = bidPrices.first();
        return new PriceSizeQueryResponse(maxBidPrice, sizeAtPrice.get(maxBidPrice));
    }

    @Override
    public PriceSizeQueryResponse getBestAsk() {
        if (askPrices.isEmpty()) return null;
        int minAskPrice = askPrices.first();
        return new PriceSizeQueryResponse(minAskPrice, sizeAtPrice.get(minAskPrice));
    }

    @Override
    public int querySize(int price) {
        return sizeAtPrice.getOrDefault(price, 0);
    }

    private void updateBid(int price, int size) {
        if (size == 0) {
            bidPrices.remove(price);
            sizeAtPrice.put(price, size);
            return;
        }


        if (!askPrices.isEmpty() && price >= askPrices.first()) {
            int minAskPrice = askPrices.first();
            Iterator<Integer> it = askPrices.iterator();
            while (it.hasNext() && price >= minAskPrice && size != 0) {
                minAskPrice = it.next();
                size = reduceSize(minAskPrice, size);
                if (size != 0 || sizeAtPrice.get(minAskPrice) == 0) {
                    it.remove();
                }
            }
        }

        if (size != 0) {
            sizeAtPrice.put(price, size);
            bidPrices.add(price);
        }
    }

    private void updateAsk(int price, int size) {
        if (size == 0) {
            askPrices.remove(price);
            sizeAtPrice.put(price, size);
            return;
        }

        if (!bidPrices.isEmpty() && price <= bidPrices.first()) {
            int maxBidPrice = bidPrices.first();
            Iterator<Integer> it = bidPrices.iterator();
            while (it.hasNext() && price <= maxBidPrice && size != 0) {
                maxBidPrice = it.next();
                size = reduceSize(maxBidPrice, size);
                if (size != 0 || sizeAtPrice.get(maxBidPrice) == 0) {
                    it.remove();
                }
            }
        }

        if (size != 0) {
            sizeAtPrice.put(price, size);
            askPrices.add(price);
        }
    }

    private int reduceSize(int price, int size) {
        int wasAtSize = sizeAtPrice.get(price);
        if (size < wasAtSize) {
            sizeAtPrice.compute(price, (k, v) -> v - size);
            return 0;
        } else {
            sizeAtPrice.put(price, 0);
            return size - wasAtSize;
        }
    }

    private void processBuyOrder(int size) {
        reduceSizeByPrices(size, askPrices);

    }

    private void processSellOrder(int size) {
        reduceSizeByPrices(size, bidPrices);
    }

    private void reduceSizeByPrices(int size, TreeSet<Integer> askPrices) {
        Iterator<Integer> it = askPrices.iterator();
        while (size > 0 && it.hasNext()) {
            int minAskPrice = it.next();
            size = reduceSize(minAskPrice, size);
            if (size != 0 || sizeAtPrice.get(minAskPrice) == 0) {
                it.remove();
            }
        }
    }
}
