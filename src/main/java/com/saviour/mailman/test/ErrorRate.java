package com.saviour.mailman.test;

import com.saviour.mailman.tool.FileUtil;

import java.io.File;

public class ErrorRate {
    public static void main(String[] args) {
        finalCheck();
    }

    public static void calcErrorRate() {
        FileUtil fileUtil = new FileUtil();
        /**
         * 以字节读取解码文件
         */
        byte[] bs1 = fileUtil.read(new File("D:/OTHER/test4cn/Mailman/good/d-e5-1.bin"));
        /**
         * 以字节读取原文件
         */
        byte[] bs2 = fileUtil.read(new File("D:/OTHER/test4cn/Mailman/test_message/e5.bin"));

        int abstain = 0;
        int counters = 0;
        int batch = 10;
        int max = bs1.length / batch;
        for (int j = 0; j < batch; j++) {
            int counter = 0;
            int giveup = 0;
            for (int i = j * max; i < (j + 1) * max; i++) {
                if (bs1[i] == 0) {
                    giveup++;
                    continue;
                }
                if (bs1[i] != bs2[i]) {
                    counter++;
                }
            }

            counters += counter;
            abstain += giveup;
            System.out.println("batch " + j + " error rate " + 100.0 * counter / max + "%"
                    + " total " + max
                    + " give up " + giveup);
        }
        System.out.println("All message with error rate " + 100.0 * counters / bs1.length + "%"
                + " total " + bs1.length
                + " give up " + abstain);

    }

    public void detectErrorBits() {
        FileUtil fileUtil = new FileUtil();
        byte[] bs1 = fileUtil.read(new File("D:/OTHER/test4cn/Mailman/demo/test11/decoded.txt"));
        byte[] result = new byte[bs1.length];
        for (int i = 0; i < bs1.length; i++) {
            if (bs1[i] == 0) {
                result[i] = 0;
            } else {
                result[i] = -1;
            }
        }

        fileUtil.write(result, "D:/OTHER/test4cn/Mailman/demo/test11/detected.txt");
    }

    public static void finalCheck() {
        FileUtil fileUtil = new FileUtil();
        byte[] src = fileUtil.read(new File("D:/OTHER/test4cn/Mailman/test_message/e5.bin"));
        byte[] decode = fileUtil.read(new File("D:/OTHER/test4cn/Mailman/tmp/decode.bin"));
        byte[] detect = fileUtil.read(new File("D:/OTHER/test4cn/Mailman/tmp/detect.bin"));

        int count = 0;
        int abstain = 0;
        for (int i = 0; i < decode.length; i++) {
            if (detect[i] == 0) {
                abstain++;
                continue;
            }

            if (src[i] != decode[i]) {
                count++;
            }
        }

        System.out.print("error rate " + 100.0 * (count) / (decode.length - abstain) + "%"
            + " with " + abstain + " bits abstain " + "within " + decode.length + " bits.");
    }
}
