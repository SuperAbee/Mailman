package com.saviour.mailman.decoder;

import com.saviour.mailman.tool.FFmpegUtil;
import com.saviour.mailman.tool.FileUtil;
import com.saviour.mailman.tool.FormatTransfer;
import com.saviour.mailman.tool.PictureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author xincong yao
 */
@Component
public class SimpleVideoDecoder {

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private PictureUtil pictureUtil;

    @Autowired
    private FFmpegUtil ffmpegUtil;

    @Autowired
    private FormatTransfer transfer;

    private File file;

    /**
     * root + filename
     */
    private String path;

    /**
     * The root path work place
     */
    private String root;

    public void load(File file, String path) {
        this.file = file;
        this.path = path;
        int index = path.lastIndexOf("/");
        this.root = path.substring(0, index);
    }

    public byte[] decode() {
        /**
         * step1: save video to local
         */
        fileUtil.toLocal(file, path);

        /**
         * step2: video -> picture
         */

        /**
         * step3: picture -> byte
         */
        return null;
    }
}
