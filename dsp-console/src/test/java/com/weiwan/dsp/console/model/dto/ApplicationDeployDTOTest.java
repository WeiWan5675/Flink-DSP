package com.weiwan.dsp.console.model.dto;

import junit.framework.TestCase;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * @Author: xiaozhennan
 * @Date: 2022/4/13 13:46
 * @Package: com.weiwan.dsp.console.model.dto
 * @ClassName: ApplicationDeployDTOTest
 * @Description:
 **/
public class ApplicationDeployDTOTest  {
    public static void main(String[] args) throws MalformedURLException {

        File file = new File("G:\\project\\Flink-DSP\\tmp\\jobs\\job_3a990e018a5db99a54ca9642fa656c5e.json");
        Path path = file.toPath();
        System.out.println(path.toString());

        URI uri = file.toURI();
        System.out.println(uri.toString());

        URL url = uri.toURL();
        System.out.println(url);

        System.out.println(file.toString());

        URL url1 = new URL(path.toString());
        System.out.println(url1);
    }

}