package base.io;

import base.Constants;
import base.opa.PriceSizeQueryResponse;
import base.opa.normal.IndexPqLinkedHashSetOPA;
import base.opa.normal.OrderProcessorAlgorithm;
import base.utils.Scanner;

import java.io.PrintWriter;

public class NormalIOProcessor implements IOProcessor {
    
    private OrderProcessorAlgorithm opa = new IndexPqLinkedHashSetOPA();

    @Override
    public void processInput(Scanner in, PrintWriter out) {
        while (in.hasMoreTokens()) {
            char code = in.next().charAt(0);
            if (code == Constants.CODE_ORDER) {
                opa.processOrder(in.nextInt(), in.next().charAt(0), in.nextInt(), in.nextInt());
            } else if (code == Constants.CODE_CANCEL) {
                int id = in.nextInt();
                boolean canceled = opa.cancelOrder(id);
                if (!canceled) {
                    out.println("Order " + id + " not found or already processed");
                }
            } else if (code == Constants.CODE_QUERY) {
                String queryType = in.next();
                if (queryType.equals(Constants.QUERY_BUYERS)) {
                    PriceSizeQueryResponse resp = opa.queryBuyers();
                    if (resp == null) {
                        out.println("empty");
                    } else {
                        out.println(resp.price + "," + resp.size);
                    }
                } else if (queryType.equals(Constants.QUERY_SELLERS)) {
                    PriceSizeQueryResponse resp = opa.querySellers();
                    if (resp == null) {
                        out.println("empty");
                    } else {
                        out.println(resp.price + "," + resp.size);
                    }
                } else if (queryType.equals(Constants.QUERY_SIZE)) {
                    int size = opa.querySize(in.nextInt());
                    out.println(size);
                }
            }
        }
    }
}
