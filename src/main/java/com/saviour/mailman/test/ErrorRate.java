package com.saviour.mailman.test;

import com.saviour.mailman.tool.FileUtil;

import java.io.File;

public class ErrorRate {
    public static void main(String[] args) {
        int counter = 0;
        FileUtil fileUtil = new FileUtil();
        byte[] bs1 = fileUtil.read(new File("D:/OTHER/test4cn/Mailman/demo/test11/decoded.txt"));
        byte[] bs2 = fileUtil.read(new File("D:/OTHER/test4cn/Mailman/demo/test11/input1.txt"));
        for (int i = 0; i < bs1.length; i++) {
            if (bs1[i] == 48) {
                continue;
            }
            if (bs1[i] != bs2[i]) {
                counter++;
            }
        }
        System.out.println(counter);
    }
}
