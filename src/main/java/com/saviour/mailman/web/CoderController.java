package com.saviour.mailman.web;

import com.saviour.mailman.decoder.SimpleVideoDecoder;
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

    @RequestMapping("/")
    public ModelAndView index(){
        return new ModelAndView("upload.html");
    }

    @RequestMapping("/encode")
    public ModelAndView encode(@RequestParam("file") MultipartFile srcFile,
                               @RequestParam("path") String path,
                               @RequestParam("length") int lengthOfVideo) throws Exception {
        /**
         * step1: preparation
         */
        File file = fileUtil.multiPartFile2File(srcFile);
        byte[] bytes = fileUtil.read(file);

        /**
         * step2: initialize encoder
         */
        encoder.load(bytes, lengthOfVideo, path);

        /**
         * step3: encode
         */
        String status = encoder.encode();

        /**
         * step4: return result
         */
        ModelAndView mav = new ModelAndView("status.html");
        mav.addObject("status", status);
        return mav;
    }

    @RequestMapping("/decode")
    public ModelAndView decode(@RequestParam("file") MultipartFile srcFile,
                               @RequestParam("path") String path) throws Exception {
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

        /**
         * step4: return status
         */
        ModelAndView mav = new ModelAndView("status.html");
        mav.addObject("status", status);
        return new ModelAndView("status.html");
    }
}
