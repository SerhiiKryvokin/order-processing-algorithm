package base.opa.normal;

import base.indexpq.IndexMaxPQ;
import base.indexpq.IndexMinPQ;
import base.opa.PriceSizeQueryResponse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import static base.Constants.*;

public class IndexPqLinkedHashSetOPA implements OrderProcessorAlgorithm {

    private IndexMinPQ<Integer> sellPrices = new IndexMinPQ<>(MAX_PRICE_DIFFERENCE * 2 + 1);

    private IndexMaxPQ<Integer> buyPrices = new IndexMaxPQ<>(MAX_PRICE_DIFFERENCE * 2 + 1);

    private HashMap<Integer, LinkedHashSet<Integer>> buyOrdersAtPrice = new HashMap<>(MAX_PRICE_DIFFERENCE * 2 + 1);

    private HashMap<Integer, LinkedHashSet<Integer>> sellOrdersAtPrice = new HashMap<>(MAX_PRICE_DIFFERENCE * 2 + 1);

    private int[] remainingOrderSize = new int[MAX_ORDER_ID + 1];

    private int[] totalSizeAtPrice = new int[MAX_PRICE_DIFFERENCE * 2 + 1];

    private int[] orderPrice = new int[MAX_ORDER_ID + 1];

    private char[] orderType = new char[MAX_ORDER_ID + 1];

    private int compressBase = -1;

    @Override
    public void processOrder(int orderId, char side, int price, int size) {
        if (price <= 0) {
            throw new IllegalArgumentException("Price should be positive integer");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size should be positive integer");
        }
        if (orderPrice[orderId] != 0) {
            throw new IllegalArgumentException("Order id:" + orderId + " has been already taken by another order");
        }
        price = compress(price);
        orderPrice[orderId] = price;
        orderType[orderId] = side;
        if (side == CODE_SIDE_BUYER) {
            processOrderBuyer(orderId, price, size);

        } else if (side == CODE_SIDE_SELLER) {
            processOrderSeller(orderId, price, size);

        }
    }

    @Override
    public boolean cancelOrder(int orderId) {
        if (remainingOrderSize[orderId] == 0) {
            return false;
        }

        if (orderType[orderId] == CODE_SIDE_BUYER) {
            buyOrdersAtPrice.get(orderPrice[orderId]).remove(orderId);
            if (buyOrdersAtPrice.get(orderPrice[orderId]).isEmpty()) {
                buyPrices.delete(orderPrice[orderId]);
            }

        } else if (orderType[orderId] == CODE_SIDE_SELLER) {
            sellOrdersAtPrice.get(orderPrice[orderId]).remove(orderId);
            if (sellOrdersAtPrice.get(orderPrice[orderId]).isEmpty()) {
                sellPrices.delete(orderPrice[orderId]);
            }
            
        }

        totalSizeAtPrice[orderPrice[orderId]] -= remainingOrderSize[orderId];
        return true;
    }

    @Override
    public PriceSizeQueryResponse queryBuyers() {
        if (buyPrices.isEmpty()) {
            return null;
        }
        int maxPrice = buyPrices.minKey();
        return new PriceSizeQueryResponse(decompress(maxPrice), totalSizeAtPrice[maxPrice]);
    }

    @Override
    public PriceSizeQueryResponse querySellers() {
        if (sellPrices.isEmpty()) {
            return null;
        }
        int minPrice = sellPrices.minKey();
        return new PriceSizeQueryResponse(decompress(minPrice), totalSizeAtPrice[minPrice]);
    }

    @Override
    public int querySize(int price) {
        return totalSizeAtPrice[compress(price)];
    }

    private void processOrderSeller(int orderId, int price, int size) {
        while (!buyPrices.isEmpty() && buyPrices.minKey() >= price && size > 0) {
            int maxBuyPrice = buyPrices.minKey();
            size = processOrders(buyOrdersAtPrice.get(maxBuyPrice), maxBuyPrice, size);
            if (buyOrdersAtPrice.get(maxBuyPrice).isEmpty()) {
                buyPrices.delMin();
            }
        }
        if (size > 0) {
            addSellOrder(orderId, price, size);
        }
    }

    private void addSellOrder(int orderId, int price, int size) {
        totalSizeAtPrice[price] += size;
        sellOrdersAtPrice.computeIfAbsent(price, k -> new LinkedHashSet<>()).add(orderId);
        remainingOrderSize[orderId] = size;
        if (!sellPrices.contains(price)) {
            sellPrices.insert(price, price);
        }
    }

    private void processOrderBuyer(int orderId, int price, int size) {
        while (!sellPrices.isEmpty() && sellPrices.minKey() <= price && size > 0) {
            int minSellPrice = sellPrices.minKey();
            size = processOrders(sellOrdersAtPrice.get(minSellPrice), minSellPrice, size);
            if (sellOrdersAtPrice.get(minSellPrice).isEmpty()) {
                sellPrices.delMin();
            }
        }
        if (size > 0) {
            addBuyOrder(orderId, price, size);
        }
    }
    
    private int processOrders(Set<Integer> orders, int price, int size) {
        Iterator<Integer> it = orders.iterator();
        while (it.hasNext() && size > 0) {
            int order = it.next();
            int remainingSize = remainingOrderSize[order];
            if (remainingSize >= size) {
                remainingOrderSize[order] -= size;
                totalSizeAtPrice[price] -= size;
                size = 0;
            } else {
                totalSizeAtPrice[price] -= remainingSize;
                size -= remainingSize;
                remainingOrderSize[order] = 0;
            }
            if (remainingOrderSize[order] == 0) {
                it.remove();
            }
        }
        return size;
    }

    private void addBuyOrder(int orderId, int price, int size) {
        totalSizeAtPrice[price] += size;
        buyOrdersAtPrice.computeIfAbsent(price, k -> new LinkedHashSet<>()).add(orderId);
        remainingOrderSize[orderId] = size;
        if (!buyPrices.contains(price)) {
            buyPrices.insert(price, price);
        }
    }

    private int compress(int price) {
        if (compressBase == -1) {
            if (price <= MAX_PRICE_DIFFERENCE) {
                compressBase = 0;
            } else {
                compressBase = price - MAX_PRICE_DIFFERENCE;
            }
        }
        return price - compressBase;
    }

    private int decompress(int price) {
        return price + compressBase;
    }


}
