package com.szx.downFileInZip.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.szx.downFileInZip.entity.FileBean;
import com.szx.downFileInZip.util.ZipUtils;

@Controller
public class DownFileController {

	@RequestMapping("/")
	private String index() {
		return "index";
	}

	@RequestMapping("/say")
	@ResponseBody
	private String say() {
		return "Hello man!";
	}

	@RequestMapping(value = "/downloadFile")
	@ResponseBody
	public void downloadFile(HttpServletResponse response) {
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			File file = new File("/Users/shen/Desktop/JAVA面试.docx");
			// Spring工具获取项目resources里的文件
			//File file2 = ResourceUtils.getFile("classpath:shell/init.sh");
			if (!file.exists()) {
				return; 
			}
			response.reset();
			response.setHeader("Content-Disposition", "attachment;filename=demo.docx");
			response.setContentType("application/octet-stream; charset=utf-8");
			os.write(FileUtils.readFileToByteArray(file));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(os);
		}

	}
	
	/**
     * 打包压缩下载文件
     */
    @RequestMapping(value = "/downLoadZipFile")
    public void downLoadZipFile(HttpServletResponse response) throws IOException{
        String zipName = "szx.zip";
        //List<FileBean> fileList = fileService.getFileList();//查询数据库中记录
        List<FileBean> fileList =new ArrayList<FileBean>();
        FileBean fileBean1 =  new FileBean();
        fileBean1.setFileId(1);
        fileBean1.setFileName("1.docx");
        fileBean1.setFilePath("/Users/shen/Desktop/");
        FileBean fileBean2 =  new FileBean();
        fileBean2.setFileId(1);
        fileBean2.setFileName("2.xlsx");
        fileBean2.setFilePath("/Users/shen/Desktop/111/");
        fileList.add(fileBean1);
        fileList.add(fileBean2);
        
        response.setContentType("APPLICATION/OCTET-STREAM");  
        response.setHeader("Content-Disposition","attachment; filename="+zipName);
        ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
        try {
            for(Iterator<FileBean> it = fileList.iterator();it.hasNext();){
                FileBean file = it.next();
               // ZipUtils.doCompress(file.getFilePath(), out);
                ZipUtils.doCompress(file.getFilePath()+file.getFileName(), out);
                response.flushBuffer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            out.close();
        }
    }

}
