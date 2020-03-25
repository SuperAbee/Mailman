package com.company;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        FileUtil fileUtil = new FileUtil();
        byte[] bs1 = fileUtil.read(new File("D:/Mailman/Workplace/test1/decode.bin"));
        byte[] result = new byte[bs1.length];
        for (int i = 0; i < bs1.length; i++) {
            if (bs1[i] == 0||i>=bs1.length-bs1.length *0.07) {
                result[i] = 0;
            } else {
                result[i] = -1;
            }
        }
        fileUtil.write(result, "D:/Mailman/Workplace/test1/detected.bin");
        finalCheck();
    }


    public static void finalCheck() {
        FileUtil fileUtil = new FileUtil();
        byte[] src = fileUtil.read(new File("D:/Mailman/Workplace/test1/e3.bin"));
        byte[] decode = fileUtil.read(new File("D:/Mailman/Workplace/test1/decode.bin"));
        byte[] detect = fileUtil.read(new File("D:/Mailman/Workplace/test1/detected.bin"));

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
