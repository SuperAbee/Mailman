package com.saviour.mailman.tool;

import org.springframework.stereotype.Component;

/**
 * @author xincong yao
 */
@Component
public class FormatTransfer {

    public Boolean[] byte2Bit(byte[] bytes){
        /**
         * tip: little-endian
         */
        Boolean[] result = new Boolean[bytes.length * 8];
        for (int i = 0; i < bytes.length; i++) {
            result[i * 8 + 0] = ((bytes[i] & 0b00000001) == 0b00000001);
            result[i * 8 + 1] = ((bytes[i] & 0b00000010) == 0b00000010);
            result[i * 8 + 2] = ((bytes[i] & 0b00000100) == 0b00000100);
            result[i * 8 + 3] = ((bytes[i] & 0b00001000) == 0b00001000);
            result[i * 8 + 4] = ((bytes[i] & 0b00010000) == 0b00010000);
            result[i * 8 + 5] = ((bytes[i] & 0b00100000) == 0b00100000);
            result[i * 8 + 6] = ((bytes[i] & 0b01000000) == 0b01000000);
            result[i * 8 + 7] = ((bytes[i] & 0b10000000) == 0b10000000);
        }
        return result;
    }

    public byte[] int2Byte(int val){
        byte[] b = new byte[4];
        b[0] = (byte)(val & 0xff);
        b[1] = (byte)((val >> 8) & 0xff);
        b[2] = (byte)((val >> 16) & 0xff);
        b[3] = (byte)((val >> 24) & 0xff);
        return b;
    }

    public Boolean[] int2Bit(int val){
        return byte2Bit(int2Byte(val));
    }
}
