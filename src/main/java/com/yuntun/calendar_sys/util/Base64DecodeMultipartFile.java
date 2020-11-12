package com.yuntun.calendar_sys.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Base64;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/11
 */
public class Base64DecodeMultipartFile implements MultipartFile {
    private final byte[] imgContent;
    private final String header;

    public Base64DecodeMultipartFile(byte[] imgContent, String header) {
        this.imgContent = imgContent;
        this.header = header.split(";")[0];
    }

    @Override
    public String getName() {
        return System.currentTimeMillis() + Math.random() + "." + header.split("/")[1];
    }

    @Override
    public String getOriginalFilename() {
        return System.currentTimeMillis() + (int) Math.random() * 10000 + "." + header.split("/")[1];
    }

    @Override
    public String getContentType() {
        return header.split(":")[1];
    }

    @Override
    public boolean isEmpty() {
        return imgContent == null || imgContent.length == 0;
    }

    @Override
    public long getSize() {
        return imgContent.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return imgContent;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(imgContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        new FileOutputStream(dest).write(imgContent);
    }

    /**
     * base64转multipartFile
     *
     * @param base64
     * @return
     */
    public static MultipartFile base64ToMultipartFile(String base64) {
        //可能带有文件类型
        String[] baseStrs = base64.split(",");
        String baseStr = baseStrs[0];
        if(baseStrs.length>1){
            baseStr = baseStrs[0];
        }
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] b = decoder.decode(baseStr);
        for (int i = 0; i < b.length; ++i) {
            //调整异常数据
            if (b[i] < 0) {
                b[i] += 256;
            }
        }
        return new Base64DecodeMultipartFile(b, baseStrs[0]);
    }


    public static String fileToBase64(File file) throws IOException {
        Base64.Encoder encoder = Base64.getEncoder();
        FileInputStream input = new FileInputStream(file);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return encoder.encodeToString(output.toByteArray());
    }

    public static String fileToBase64(MultipartFile file) throws IOException {
        InputStream input = file.getInputStream();
        Base64.Encoder encoder = Base64.getEncoder();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return encoder.encodeToString(output.toByteArray());
    }

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\whj\\Desktop\\godfs\\微信截图_20201110150211.png");
        String s = fileToBase64(file);
        System.out.println(s);
    }
}
