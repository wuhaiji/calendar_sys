package com.yuntun.calendar_sys.controller.wechat;

import com.alibaba.fastjson.JSONObject;
import com.yuntun.calendar_sys.constant.FileConstant;
import com.yuntun.calendar_sys.model.response.Result;
import com.yuntun.calendar_sys.properties.GoFastDFSProperties;
import com.yuntun.calendar_sys.service.FileService;
import com.yuntun.calendar_sys.util.Base64DecodeMultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *
 * </p>
 *
 * @author whj
 * @since 2020/11/10
 */


@RestController
@RequestMapping("/wechat/file")
public class FileUploadController {
    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());

    @Autowired
    private FileService fileService;
    @Autowired
    private GoFastDFSProperties goFastDFSProperties;

    /**
     * 文件上传
     *
     * @param file 文件 以及设备类型id
     * @return 文件路径
     */
    @PostMapping("/upload")
    public Result<Object> goFastDFSUploadFile(@RequestBody MultipartFile file) {
        String data = fileService.goFastDFSUploadFile(file);
        return Result.ok(
                goFastDFSProperties.path.substring(0, goFastDFSProperties.path.length() - 1)
                        + data
                        + FileConstant.DOWNLOAD_0
        );
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 文件路径
     */
    @PostMapping("/upload/base64")
    public Result<Object> goFastDFSUploadFile(@RequestBody JSONObject file) {
        String base64Data = file.getString("file");
        MultipartFile multipartFile = Base64DecodeMultipartFile.base64ToMultipartFile(base64Data);
        String data = fileService.goFastDFSUploadFile(multipartFile);
        return Result.ok(
                goFastDFSProperties.path.substring(0, goFastDFSProperties.path.length() - 1)
                        + data
                        + FileConstant.DOWNLOAD_0);
    }

    /**
     * 删除图片
     *
     * @return 删除文件
     */
    @RequestMapping("/delete")
    public Result<Object> goFastDFSDeleteFile(@RequestParam String path) {
        fileService.goFastDFSDeleteFile(path);
        return Result.ok();
    }
}
