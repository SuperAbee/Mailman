package com.saviour.mailman.encoder;

import com.saviour.mailman.tool.PictureUtil;
import com.saviour.mailman.tool.QRCodeUtil;
import com.saviour.mailman.video.SimpleVideoLayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Component("orcBasedVideoEncoder")
public class ORCBasedVideoEncoder  extends SimpleVideoEncoder {
    private static final String PICTUREPREFIX = "tmp";

    @Autowired
    private QRCodeUtil qrCodeUtil;

    @Autowired
    private PictureUtil pictureUtil;

    @Autowired
    private SimpleVideoLayer videoLayer;

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
        qrCodeUtil.generatePictures(new String(srcByte), QRBasedVideoEncoder.maxLength, root, PICTUREPREFIX);

        /**
         * step2: pictures -> video
         */
        int numOfPictures =  ((new String(srcByte).length() + 1) / QRBasedVideoEncoder.maxLength + 1);
        videoLayer.compose(fps, numOfPictures, root, PICTUREPREFIX, false);

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
        pictureUtil.deletePictures(root, PICTUREPREFIX);

        return "OK";
    }
}
