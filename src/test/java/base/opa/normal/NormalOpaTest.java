package base.opa.normal;

import base.opa.PriceSizeQueryResponse;
import org.junit.Before;
import org.junit.Test;

import static base.Constants.CODE_SIDE_BUYER;
import static base.Constants.CODE_SIDE_SELLER;
import static org.junit.Assert.*;

public class NormalOpaTest {

    private OrderProcessorAlgorithm opa;

    @Before
    public void init() {
        opa = new IndexPqLinkedHashSetOPA();
    }

    @Test
    public void partiallyProcessedOrderCanceled() {
        int orderId = 0;
        opa.processOrder(orderId++, CODE_SIDE_BUYER, 100, 30);
        opa.processOrder(orderId++, CODE_SIDE_BUYER, 100, 30);
        opa.processOrder(orderId++, CODE_SIDE_SELLER, 100, 20);
        boolean canceled = opa.cancelOrder(0);
        assertTrue(canceled);
        int size = opa.querySize(100);

        //order 0 was canceled with remaining size = 10.
        //order 1 left with initial size = 30
        assertEquals(30, size);
    }

    @Test
    public void bigPricesCompressed() {
        int orderId = 0;
        opa.processOrder(orderId++, CODE_SIDE_BUYER, (int) 1e9, 30);
        opa.processOrder(orderId++, CODE_SIDE_BUYER, (int) 1e9, 30);
        opa.processOrder(orderId++, CODE_SIDE_BUYER, (int) 1e9 + 10000, 30);
        opa.processOrder(orderId++, CODE_SIDE_SELLER, (int) 1e9, 20);
        assertEquals(opa.querySize((int) 1e9), 60);
        assertEquals(opa.querySize((int) 1e9 + 10000), 10);
        opa.cancelOrder(0);
        assertEquals(opa.querySize((int) 1e9), 30);
    }

    @Test
    public void bestPriceCanceledThenAdded() {
        int orderId = 0;
        opa.processOrder(orderId++, CODE_SIDE_SELLER, 100, 30);
        opa.processOrder(orderId++, CODE_SIDE_SELLER, 101, 30);
        opa.processOrder(orderId++, CODE_SIDE_SELLER, 99, 30);
        opa.processOrder(orderId++, CODE_SIDE_SELLER, 99, 30);
        opa.processOrder(orderId++, CODE_SIDE_SELLER, 98, 30);
        assertEquals(30, opa.querySize(98));
        PriceSizeQueryResponse resp = opa.querySellers();
        assertEquals(98, resp.price);
        assertEquals(30, resp.size);
        assertTrue(opa.cancelOrder(orderId - 1));
        assertEquals(0, opa.querySize(98));

        resp = opa.querySellers();
        assertEquals(99, resp.price);
        assertEquals(60, resp.size);

        assertEquals(30, opa.querySize(100));
        assertEquals(30, opa.querySize(101));
        assertEquals(60, opa.querySize(99));

        opa.processOrder(orderId++, CODE_SIDE_BUYER, 91, 25);
        assertEquals(25, opa.querySize(91));
        resp = opa.queryBuyers();
        assertEquals(91, resp.price);
        assertEquals(25, resp.size);

        opa.processOrder(orderId++, CODE_SIDE_SELLER, 90, 30);
        resp = opa.querySellers();
        assertEquals(90, resp.price);
        assertEquals(5, resp.size);

        resp = opa.queryBuyers();
        assertNull(resp);
    }
    
    @Test
    public void totalSizeZeroOnStart() {
        assertEquals(0, opa.querySize(100));
    }
    
    @Test
    public void nullResponseOnStart() {
        PriceSizeQueryResponse resp = opa.querySellers();
        assertNull(resp);
        resp = opa.queryBuyers();
        assertNull(resp);
    }
    
    @Test
    public void bigBuyOrderTakesSeveralSmallSellOrders() {
        int orderId = 0;
        opa.processOrder(orderId++, CODE_SIDE_SELLER, 100, 30);
        opa.processOrder(orderId++, CODE_SIDE_SELLER, 100, 40);
        opa.processOrder(orderId++, CODE_SIDE_SELLER, 100, 50);
        PriceSizeQueryResponse resp = opa.querySellers();
        assertEquals(100, resp.price);
        assertEquals(120, resp.size);
        opa.processOrder(orderId++, CODE_SIDE_BUYER, 101, 75);
        resp = opa.querySellers();
        assertEquals(100, resp.price);
        assertEquals(45, resp.size);
        resp = opa.queryBuyers();
        assertNull(resp);
    }
    
    @Test
    public void bigSellOrderTakesSeveralBuyOrders() {
        int orderId = 0;
        opa.processOrder(orderId++, CODE_SIDE_BUYER, 100, 30);
        opa.processOrder(orderId++, CODE_SIDE_BUYER, 100, 40);
        opa.processOrder(orderId++, CODE_SIDE_BUYER, 100, 50);
        PriceSizeQueryResponse resp = opa.queryBuyers();
        assertEquals(100, resp.price);
        assertEquals(120, resp.size);
        opa.processOrder(orderId++, CODE_SIDE_SELLER, 99, 75);
        resp = opa.queryBuyers();
        assertEquals(100, resp.price);
        assertEquals(45, resp.size);
        resp = opa.querySellers();
        assertNull(resp);
    }
}
