package com.saviour.mailman.encoder;

import com.saviour.mailman.tool.PictureUtil;
import com.saviour.mailman.tool.QRCodeUtil;
import com.saviour.mailman.video.SimpleVideoLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Component("orcBasedVideoEncoder")
public class ORCBasedVideoEncoder  extends SimpleVideoEncoder {
    private String picturePrefix;

    @Autowired
    private QRCodeUtil qrCodeUtil;

    @Autowired
    private PictureUtil pictureUtil;

    @Autowired
    private SimpleVideoLayer videoLayer;

    @Override
    public String encode() throws Exception {
        picturePrefix = path.substring(root.length()) + "tmp";

        /**
         * The size of bytes bigger than 10MB, switch to fast mode
         */
        if(srcByte.length >= 1024 * 1024 * 20){
            return fastEncode();
        }

        /**
         * step1: generate pictures
         */
        String message = QRCodeUtil.byteArrayToHexStr(srcByte);
        qrCodeUtil.generatePictures(message, QRBasedVideoEncoder.maxLength, root, picturePrefix);

        /**
         * step2: pictures -> video
         */
        int numOfPictures =  ((message.length() + 1) / QRBasedVideoEncoder.maxLength + 1);
        videoLayer.compose(fps, numOfPictures, root, path, picturePrefix, false);

        /**
         * step3: remove pictures
         */
        try {
            /**
             * This thread sleep 10ms per pictureNumber to delete pictures,
             * so that ffmpeg has enough time to combine videos.
             */
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        pictureUtil.deletePictures(root, picturePrefix);

        return "OK";
    }
}
