package com.saviour.mailman.web;

import com.saviour.mailman.decoder.SimpleVideoDecoder;
import com.saviour.mailman.encoder.QRBasedVideoEncoder;
import com.saviour.mailman.encoder.SimpleVideoEncoder;
import com.saviour.mailman.tool.FileUtil;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
                               @RequestParam("time") int time,
                               @RequestParam(value = "version", defaultValue = "1") int version) throws Exception {
        /**
         * step1: preparation
         */
        File file = fileUtil.multiPartFile2File(srcFile);
        byte[] bytes = fileUtil.read(file);
        bytes = Arrays.copyOf(bytes, rate * time / 8);

        /**
         * DANGEROUS !!!
         */
        QRBasedVideoEncoder.maxLength = rate / (8 * fps) * 2;

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

        /**
         * step4: return status
         */
        ModelAndView mav = new ModelAndView("status.html");
        mav.addObject("status", status);
        return new ModelAndView("status.html");
    }

    @RequestMapping("/detect")
    public String detect(@RequestParam("dfile") MultipartFile srcFile,
                               @RequestParam("path") String path) throws IOException {
        byte[] bs1 = srcFile.getBytes();
        byte[] result = new byte[bs1.length];
        for (int i = 0; i < bs1.length; i++) {
            if (bs1[i] == 0 || i > bs1.length * 0.93) {
                result[i] = 0;
            } else {
                result[i] = -1;
            }
        }
        fileUtil.write(result, path);
        return "OK";
    }

    @RequestMapping("/test/encode")
    public String encode4test(String srcFilePath, String encodedFilePath, int time) throws Exception {
        time /= 1000;
        int rate = 32000;
        int fps = 10;

        /**
         * step1: preparation
         */
        File file = new File(srcFilePath);
        byte[] bytes = fileUtil.read(file);
        bytes = Arrays.copyOf(bytes, rate * time / 8);

        /**
         * DANGEROUS !!!
         */
        QRBasedVideoEncoder.maxLength = rate / (8 * fps) * 2;

        /**
         * step2: initialize encoder
         */
        encoder.load(bytes, fps, encodedFilePath);

        /**
         * step3: encode
         */
        String status = "OK";
        status = encoder.encode();


        /**
         * step4: return result
         */
        return status;
    }

    @RequestMapping("/test/decode")
    public String decode4test(String videoPath, String decodePath, String detectPath) throws Exception {
        /**
         * step1: initialize decoder
         */
        File file = new File(videoPath);
        decoder.load(file, decodePath);

        /**
         * step2: decode
         */
        byte[] result = decoder.decode4test(videoPath);

        /**
         * step3: write to file
         */
        String status = fileUtil.write(result, decodePath);

        /**
         * step4: return status
         */
        File dfile = new File(decodePath);
        byte[] bs1 = fileUtil.read(dfile);
        byte[] detect = new byte[bs1.length];
        for (int i = 0; i < bs1.length; i++) {
            if (bs1[i] == 0 || i > bs1.length * 0.93) {
                detect[i] = 0;
            } else {
                detect[i] = -1;
            }
        }
        fileUtil.write(detect, detectPath);

        return status;
    }
}
