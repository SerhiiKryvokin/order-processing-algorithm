package base.opa.basic;

import base.opa.PriceSizeQueryResponse;
import base.opa.baisc.BasicOPA;
import base.opa.baisc.BasicOPAImpl;
import org.junit.Before;
import org.junit.Test;

import static base.opa.baisc.BasicOPA.BookEntryType.ask;
import static base.opa.baisc.BasicOPA.BookEntryType.bid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BasicOpaTest {

    private BasicOPA opa;

    @Before
    public void init() {
        opa = new BasicOPAImpl();
    }
    
    @Test
    public void bestBidChanges() {
        opa.updateBook(bid, 100, 50);
        opa.updateBook(bid, 110, 40);
        PriceSizeQueryResponse bestBid = opa.getBestBid();
        assertEquals(110, bestBid.price);
        assertEquals(40, bestBid.size);
        opa.updateBook(bid, 120, 40);
        opa.updateBook(bid, 10, 40);
        opa.updateBook(bid, 130, 80);
        bestBid = opa.getBestBid();
        assertEquals(130, bestBid.price);
        assertEquals(80, bestBid.size);
        
        assertEquals(80, opa.querySize(130));
        assertEquals(40, opa.querySize(110));
    }

    @Test
    public void bestAskChanges() {
        
        opa.updateBook(ask, 100, 50);
        opa.updateBook(ask, 90, 40);
        PriceSizeQueryResponse bestAsk = opa.getBestAsk();
        assertEquals(90, bestAsk.price);
        assertEquals(40, bestAsk.size);
        opa.updateBook(ask, 120, 40);
        opa.updateBook(ask, 10, 90);
        opa.updateBook(ask, 130, 80);
        bestAsk = opa.getBestAsk();
        assertEquals(10, bestAsk.price);
        assertEquals(90, bestAsk.size);

        assertEquals(90, opa.querySize(10));
        assertEquals(40, opa.querySize(90));
    }

    @Test
    public void bidConsumeAsks() {
        opa.updateBook(ask, 100, 55);
        opa.updateBook(ask, 90, 50);
        opa.updateBook(ask, 80, 45);
        opa.updateBook(ask, 70, 40);
        opa.updateBook(ask, 60, 35);

        PriceSizeQueryResponse bestAsk = opa.getBestAsk();
        assertEquals(60, bestAsk.price);
        assertEquals(35, bestAsk.size);
        
        opa.updateBook(bid, 85, 95);

        bestAsk = opa.getBestAsk();
        assertEquals(80, bestAsk.price);
        assertEquals(25, bestAsk.size);

        assertEquals(25, opa.querySize(80));
        assertEquals(55, opa.querySize(100));
    }

    @Test
    public void askConsumeBids() {
        opa.updateBook(bid, 120, 35);
        opa.updateBook(bid, 110, 40);
        opa.updateBook(bid, 100, 45);
        opa.updateBook(bid, 90, 50);
        opa.updateBook(bid, 80, 55);

        PriceSizeQueryResponse bestBid = opa.getBestBid();
        assertEquals(120, bestBid.price);
        assertEquals(35, bestBid.size);

        opa.updateBook(ask, 95, 95);

        bestBid = opa.getBestBid();
        assertEquals(100, bestBid.price);
        assertEquals(25, bestBid.size);
    }
    
    @Test
    public void nullOnStart() {
        PriceSizeQueryResponse bestBid = opa.getBestBid();
        PriceSizeQueryResponse bestAsk = opa.getBestAsk();
        
        assertNull(bestBid);
        assertNull(bestAsk);
    }

    @Test
    public void nullAfterStart() {
        opa.updateBook(bid, 120, 35);
        opa.updateBook(bid, 110, 40);
        opa.updateBook(ask, 90, 75);
        
        PriceSizeQueryResponse bestBid = opa.getBestBid();
        PriceSizeQueryResponse bestAsk = opa.getBestAsk();

        assertNull(bestBid);
        assertNull(bestAsk);
        
        assertEquals(0, opa.querySize(120));
        assertEquals(0, opa.querySize(110));
        assertEquals(0, opa.querySize(90));
    }
    
    
}
