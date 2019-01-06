package com.szx.downFileInZip.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.szx.downFileInZip.util.FileUtil;

@RestController
public class UploadMultipartFileController {
	@Value("${fileType}")
	String fileType;
	@Value("${rootPath}")
	String rootPath;
	@Value("${subDir}")
	String subDir;
	@Value("${maxSizeM}")
	int maxSizeM;
	@Value("${picNum}")
	int picNum;
	

	@CrossOrigin
	@PostMapping(value = "/uploadPic")
	@ResponseBody
	public Map<String, Object> UploadMultipartFile(HttpServletRequest request,
			@RequestParam(value = "files") MultipartFile[] files) throws IllegalStateException, IOException {
		//Map<String, Object> paramMap = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<String> failList = new ArrayList<String>();
		if (files.length>picNum) {
			resultMap.put("Staus", "上传失败");
			resultMap.put("Msg", "该附件数量不大于5个");
			return resultMap;
		} else {
			// TODO 查询已有该类型附件数量
			if (files.length>picNum-1) {
				resultMap.put("Staus", "上传失败");
				resultMap.put("Msg", "该类附件数量不大于5个");
				return resultMap;
			}
		}
		String newPath = rootPath + subDir;
		// 判断上传目录是否存在
		File newDir = new File(newPath);
		if (!newDir.exists()) {
			newDir.mkdir();
		}
		if (!newDir.canWrite()) {
			resultMap.put("Staus", "上传失败");
			resultMap.put("Msg", "Dir can't write!");
			return resultMap;
		}
		// 上蹿文件大小不能超过2MB
		for (MultipartFile file : files) {
			// 判断文件大小是否超过限制
			if (file.getSize() >= (maxSizeM * 1024 * 1024)) {
				failList.add(file.getOriginalFilename() + "超过大小限制");
				continue;
				// 判断文件真实格式是否在范围内
			} else if (!fileType.contains(FileUtil.getFileType(file.getBytes()))) {
				failList.add(file.getOriginalFilename() + "格式不在允许范围");
				continue;
			} else {
				file.transferTo(new File(newPath + File.separator + file.getOriginalFilename()));
			}
		}
		// TODO 返回现有材料列表
		resultMap.put("Staus", "上传成功");
		resultMap.put("Msg", files.length - failList.size() + "条成功，" + failList.size() + "条失败");
		resultMap.put("errorList", failList);
		return resultMap;
	}

	@CrossOrigin
	@PostMapping(value = "/deletePic")
	@ResponseBody
	public Map<String, Object> deletePic(@RequestParam(value = "picId") String picId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (FileUtil.deleteFile(picId)) {
			// TODO 返回现有材料列表
			resultMap.put("Staus", "删除成功");
		} else {
			resultMap.put("Staus", "删除失败");
		}
		return resultMap;
	}

}
