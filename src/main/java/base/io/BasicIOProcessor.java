package base.io;

import base.opa.PriceSizeQueryResponse;
import base.opa.baisc.BasicOPA;
import base.opa.baisc.BasicOPA.BookEntryType;
import base.opa.baisc.BasicOPAImpl;
import base.utils.Scanner;

import java.io.PrintWriter;

import static base.Constants.*;

public class BasicIOProcessor implements IOProcessor {

    private BasicOPA opa = new BasicOPAImpl();
    
    @Override
    public void processInput(Scanner in, PrintWriter out) {
        while (in.hasMoreTokens()) {
            char code = in.next().charAt(0);
            if (code == CODE_UPDATE) {
                int price = in.nextInt();
                int size = in.nextInt();
                String side = in.next();
                opa.updateBook(BookEntryType.valueOf(side), price, size);
            } else if (code == CODE_ORDER) {
                String side = in.next();
                int size = in.nextInt();
                opa.processOrder(BasicOPA.OrderType.valueOf(side), size);
            } else if (code == CODE_QUERY) {
                String type = in.next();
                if (type.equals(QUERY_SIZE)) {
                    int price = in.nextInt();
                    out.println(opa.querySize(price));
                } else if (type.equals(QUERY_BEST_BID)) {
                    PriceSizeQueryResponse bestBid = opa.getBestBid();
                    if (bestBid == null) {
                        out.println("empty");
                    } else {
                        out.println(bestBid.price + " " + bestBid.size);
                    }
                } else if (type.equals(QUERY_BEST_ASK)) {
                    PriceSizeQueryResponse bestAsk = opa.getBestAsk();
                    if (bestAsk == null) {
                        out.println("empty");
                    } else {
                        out.println(bestAsk.price + " " + bestAsk.size);
                    }
                }
            }
        }
    }
}
