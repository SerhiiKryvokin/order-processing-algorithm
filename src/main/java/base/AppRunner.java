package base;

import base.opa.IndexPqLinkedHashSetOPA;
import base.opa.OrderProcessorAlgorithm;
import base.opa.OrderProcessorAlgorithm.PriceSizeQueryResponse;
import base.utils.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class AppRunner {
    
    OrderProcessorAlgorithm opa = new IndexPqLinkedHashSetOPA();

    private void processInput() {
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
                if (queryType.equals(Constants.CODE_BUYERS)) {
                    PriceSizeQueryResponse resp = opa.queryBuyers();
                    if (resp == null) {
                        out.println("empty");
                    } else {
                        out.println(resp.price + "," + resp.size);
                    }
                } else if (queryType.equals(Constants.CODE_SELLERS)) {
                    PriceSizeQueryResponse resp = opa.querySellers();
                    if (resp == null) {
                        out.println("empty");
                    } else {
                        out.println(resp.price + "," + resp.size);
                    }
                } else if (queryType.equals(Constants.CODE_SIZE)) {
                    int size = opa.querySize(in.nextInt());
                    out.println(size);
                }
            }
        }
    }
    
    Scanner in;
    PrintWriter out;

    void run() {
        try {
            in = new Scanner(new File(Constants.INPUT_FILE_NAME));
            out = new PrintWriter(new File(Constants.OUTPUT_FILE_NAME));

            processInput();

            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AppRunner().run();
    }
}
