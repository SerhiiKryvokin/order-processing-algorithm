package base;

public class Constants {
    public static final String INPUT_FILE_NAME = "input.txt";
    public static final String OUTPUT_FILE_NAME = "output.txt";

    public static final String QUERY_BUYERS = "buyers";
    public static final String QUERY_SELLERS = "sellers";
    public static final String QUERY_SIZE = "size";
    public static final String QUERY_BEST_BID = "best_bid";
    public static final String QUERY_BEST_ASK = "best_ask";
    
    public static final char CODE_SIDE_BUYER = 'b';
    public static final char CODE_SIDE_SELLER = 's';
    public static final char CODE_ORDER = 'o';
    public static final char CODE_CANCEL = 'c';
    public static final char CODE_QUERY = 'q';
    public static final char CODE_UPDATE = 'u';
    
    

    public static final int MAX_PRICE_DIFFERENCE = (int) 1e4;
    public static final int MAX_ORDER_ID = (int) 1e6;

    private Constants() {
    }
}
