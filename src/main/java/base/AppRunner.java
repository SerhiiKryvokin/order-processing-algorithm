package base;

import base.io.BasicIOProcessor;
import base.io.IOProcessor;
import base.utils.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class AppRunner {
    private IOProcessor ioProcessor = new BasicIOProcessor();
//    private IOProcessor ioProcessor = new NormalIOProcessor();

    private void run() {
        try (PrintWriter out = new PrintWriter(new File(Constants.OUTPUT_FILE_NAME))) {
            Scanner in = new Scanner(new File(Constants.INPUT_FILE_NAME));
            ioProcessor.processInput(in, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new AppRunner().run();
    }
}
