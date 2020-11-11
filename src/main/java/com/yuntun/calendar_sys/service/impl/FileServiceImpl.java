package com.yuntun.calendar_sys.service.impl;

import cn.hutool.core.io.resource.InputStreamResource;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.yuntun.calendar_sys.exception.ServiceException;
import com.yuntun.calendar_sys.model.code.FileCode;
import com.yuntun.calendar_sys.properties.GoFastDFSProperties;
import com.yuntun.calendar_sys.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 文件上传实现类
 * </p>
 *
 * @author tangcx
 * @since 2020-09-10
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    GoFastDFSProperties goFastDFSProperties;

    /**
     * 文件上传
     *
     * @param file 文件
     * @return
     */

    @Override
    public String goFastDFSUploadFile(MultipartFile file) {
        try {
            String filename = file.getOriginalFilename();
            String newName = "";
            if (filename != null) {
                newName = UUID.randomUUID() + filename.substring(filename.lastIndexOf("."));
            }
            InputStreamResource isr = new InputStreamResource(file.getInputStream(), newName);
            Map<String, Object> params = new HashMap<>(6);
            params.put("file", isr);
            params.put("path", "/cl_mini_app/" + LocalDate.now().toString());
            params.put("output", "json2");
            String urlString = goFastDFSProperties.path + goFastDFSProperties.group + "/upload";
            String resp = HttpUtil.post(urlString, params);
            JSONObject jsonObject = JSONObject.parseObject(resp);
            String path = jsonObject.getJSONObject("data").getString("path");
            log.info("path:{}",path);
            return path;

        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ServiceException(FileCode.FILE_NOT_EXISTS_ERROR);
        }
    }

    @Override
    public void goFastDFSDeleteFile(String path) {
        Map<String, Object> params = new HashMap<>(6);
        params.put("path", path);
        String res = HttpUtil.post(goFastDFSProperties.path + goFastDFSProperties.group + "/delete", params);
        //log.info("resp:{}",res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        if (!"ok".equals(jsonObject.getString("status"))) {
            log.error("goFastDFS 文件删除异常");
            throw new ServiceException(FileCode.FILE_DELETE_ERROR);
        }

    }
}
