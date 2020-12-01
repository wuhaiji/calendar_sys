package com.yuntun.calendar_sys.controller.sys;

import com.alibaba.fastjson.JSONObject;
import com.yuntun.calendar_sys.model.response.Result;
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
@RequestMapping("/sys/file")
public class FileUploadSysController {
    private static final Logger log = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
    @Autowired
    private FileService fileService;

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 文件路径
     */
    @PostMapping("/upload")
    public Result<Object> goFastDFSUploadFile(MultipartFile file) {
        String data = fileService.goFastDFSUploadFile(file);
        return Result.ok(data);
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
        return Result.ok(data);
    }


    /**
     * 删除图片
     *
     * @return 删除文件
     */
    @RequestMapping("/delete")
    public Result<Object> goFastDFSDeleteFile(@RequestParam String path) {
        log.info("smart-home-service->FileController->begin go-fastDFS upload file");
        fileService.goFastDFSDeleteFile(path);
        return Result.ok();
    }
}
