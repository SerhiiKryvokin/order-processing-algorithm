import base.opa.IndexPqLinkedHashSetOPA;
import base.opa.OrderProcessorAlgorithm;
import org.junit.Before;
import org.junit.Test;

import static base.Constants.CODE_SIDE_BUYER;
import static base.Constants.CODE_SIDE_SELLER;
import static org.junit.Assert.*;

public class OpaTest {

    OrderProcessorAlgorithm opa;

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
        OrderProcessorAlgorithm.PriceSizeQueryResponse resp = opa.querySellers();
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
}
