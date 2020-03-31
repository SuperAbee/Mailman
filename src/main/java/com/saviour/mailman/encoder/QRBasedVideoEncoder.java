package com.saviour.mailman.encoder;

import com.google.zxing.qrcode.encoder.QRCode;
import com.saviour.mailman.tool.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Component("qrBasedVideoEncoder")
public class QRBasedVideoEncoder extends SimpleVideoEncoder {

    private static final String PICTUREPREFIX = "tmp";
    public static int maxLength = 800;

    @Autowired
    private QRCodeUtil qrCodeUtil;

    @Autowired
    private FFmpegUtil ffmpegUtil;

    @Autowired
    private PictureUtil pictureUtil;

    @Override
    public String encode() throws Exception {
        /**
         * The size of bytes bigger than 10MB, switch to fast mode
         */
        if(srcByte.length >= 1024 * 1024 * 10){
            return fastEncode();
        }

        /**
         * step1: generate pictures
         */
        qrCodeUtil.generatePictures(QRCodeUtil.byteArrayToHexStr(srcByte), maxLength, root, PICTUREPREFIX);

        /**
         * step2: pictures -> video
         */
        ffmpegUtil.generateVideo(path, fps, "tmp");

        /**
         * step3: remove pictures
         */
        try {
            /**
             * This thread sleep 10ms per pictureNumber to delete pictures,
             * so that ffmpeg has enough time to combine videos.
             */
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pictureUtil.deletePictures(root, PICTUREPREFIX);

        return "OK";
    }
}
