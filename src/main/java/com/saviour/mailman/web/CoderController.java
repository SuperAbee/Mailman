package com.saviour.mailman.web;

import com.saviour.mailman.decoder.SimpleVideoDecoder;
import com.saviour.mailman.encoder.QRBasedVideoEncoder;
import com.saviour.mailman.encoder.SimpleVideoEncoder;
import com.saviour.mailman.tool.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;

@RestController
public class CoderController {

    @Autowired
    private FileUtil fileUtil;

    @Qualifier("orcBasedVideoEncoder")
    @Autowired
    private SimpleVideoEncoder encoder;

    @Qualifier("orcBasedVideoDecoder")
    @Autowired
    private SimpleVideoDecoder decoder;

    @Qualifier("qrBasedVideoEncoder")
    @Autowired
    private SimpleVideoEncoder fastEncoder;

    @Qualifier("qrBasedVideoDecoder")
    @Autowired
    private SimpleVideoDecoder fastDecoder;

    @RequestMapping("/")
    public ModelAndView index(){
        return new ModelAndView("upload.html");
    }

    @RequestMapping("/encode")
    public ModelAndView encode(@RequestParam("file") MultipartFile srcFile,
                               @RequestParam("path") String path,
                               @RequestParam("fps") int fps,
                               @RequestParam("rate") int rate,
                               @RequestParam(value = "version", defaultValue = "1") int version) throws Exception {
        /**
         * step1: preparation
         */
        File file = fileUtil.multiPartFile2File(srcFile);
        byte[] bytes = fileUtil.read(file);
        file.delete();
        /**
         * DANGEROUS !!!
         */
        QRBasedVideoEncoder.maxLength = rate / 80;

        /**
         * step2: initialize encoder
         */
        encoder.load(bytes, fps, path);

        /**
         * step3: encode
         */
        String status = "OK";
        switch (version){
            case 1:
                status = encoder.encode();
                break;
            case 2:
                status = fastEncoder.encode();
                break;
            default:
        }

        /**
         * step4: return result
         */
        ModelAndView mav = new ModelAndView("status.html");
        mav.addObject("status", status);
        return mav;
    }

    @RequestMapping("/decode")
    public ModelAndView decode(@RequestParam("file") MultipartFile srcFile,
                               @RequestParam("path") String path,
                               @RequestParam(value = "version", defaultValue = "1") int version) throws Exception {
        /**
         * step1: initialize decoder
         */
        File file = fileUtil.multiPartFile2File(srcFile);
        decoder.load(file, path);

        /**
         * step2: decode
         */
        byte[] result = decoder.decode();

        /**
         * step3: write to file
         */
        String status = fileUtil.write(result, path);
        file.delete();

        /**
         * step4: return status
         */
        ModelAndView mav = new ModelAndView("status.html");
        mav.addObject("status", status);
        return new ModelAndView("status.html");
    }
}
