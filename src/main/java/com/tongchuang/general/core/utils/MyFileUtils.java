package com.tongchuang.general.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

 
 

import cn.hutool.core.util.IdUtil;
import cn.hutool.log.Log;

/**
 * 服务器 
 * /var/app/forThemOnly/upload/ 目录下 : 
 * 样式:2007029530402103.jpg 为用户注册商家信息上传图片    或者 用户自定义模板 
 * 
 * 样式:tmp-1594263427.jpg 供用户下载促销图片,临时文件可删除
 *  
 * 
 * 
 * 
 * 
 * @author Admin
 *
 */
public class MyFileUtils {
	
	
	public static final String downloadPath = "/var/app/maotai/download/";
	
	public static final String uploadPath = "/var/app/maotai/upload/";

	public static final String urlPath = "https://q.3p3.top/upload/";
	
	 

	public static String fileUpload(MultipartFile file,String type) {
		if (file != null) {
			BufferedOutputStream bw = null;
			File outFile = null;
			try {
				String fileName = file.getOriginalFilename();
				String fn = DateUtils.getGuid() + getFileType(fileName);
				// 判断是否有文件
				if(StringUtils.isNotEmpty(type)) {
					fn = type + fn;
				}
				if (StringUtils.isNotBlank(fileName)) {
					// 输出到本地路径
					outFile = new File(uploadPath + fn);
					file.transferTo(outFile);
				}
				//return urlPath + fn;
			 
				return urlPath + fn;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bw != null) {
						bw.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String getFileType(String filename) {
		if (filename.endsWith(".jpg") || filename.endsWith(".jepg")) {
			return ".jpg";
		}else if (filename.endsWith(".png") || filename.endsWith(".PNG")) {
			
			return ".png";
		} else {
			//return "application/octet-stream";
			return filename.substring(filename.lastIndexOf("."));
		}
//		else {
//			throw new BizException("只支持JPG格式");
//		}

	}
	public static void main(String[] args) {
		String filename = ".1.jp.png";
		System.out.println(filename.substring(filename.lastIndexOf(".")));
	}
	
	
	public static void fileWriteByte(File file, byte[] fimalByte) {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(fimalByte);
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(fos);
			close(bos);
		}

	}

	public static <T extends java.io.Closeable> void close(T t) {
		try {
			if (t != null) {
				t.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /** 
     * 将存放在sourceFilePath目录下的源文件，打包成fileName名称的zip文件，并存放到zipFilePath路径下 
     * @param sourceFilePath :待压缩的文件路径 
     * @param zipFilePath :压缩后存放路径 
     * @param fileName :压缩后文件的名称 
     * @return 
     */  
    public static boolean fileToZip(String sourceFilePath,String zipFilePath,String fileName){  
        boolean flag = false;  
        File sourceFile = new File(sourceFilePath);  
        FileInputStream fis = null;  
        BufferedInputStream bis = null;  
        FileOutputStream fos = null;  
        ZipOutputStream zos = null;  
        if(sourceFile.exists() == false){  
            System.out.println("待压缩的文件目录："+sourceFilePath+"不存在.");  
        }else{  
            try {  
                File zipFile = new File(zipFilePath + "/" + fileName +".zip");  
                if(zipFile.exists()){  
                    System.out.println(zipFilePath + "目录下存在名字为:" + fileName +".zip" +"打包文件.");  
                }else{  
                    File[] sourceFiles = sourceFile.listFiles();  
                    if(null == sourceFiles || sourceFiles.length<1){  
                        System.out.println("待压缩的文件目录：" + sourceFilePath + "里面不存在文件，无需压缩.");  
                    }else{  
                        fos = new FileOutputStream(zipFile);  
                        zos = new ZipOutputStream(new BufferedOutputStream(fos));  
                        byte[] bufs = new byte[1024*10];  
                        for(int i=0;i<sourceFiles.length;i++){  
                            //创建ZIP实体，并添加进压缩包  
                            ZipEntry zipEntry = new ZipEntry(sourceFiles[i].getName());  
                            zos.putNextEntry(zipEntry);  
                            //读取待压缩的文件并写进压缩包里  
                            fis = new FileInputStream(sourceFiles[i]);  
                            bis = new BufferedInputStream(fis, 1024*10);  
                            int read = 0;  
                            while((read=bis.read(bufs, 0, 1024*10)) != -1){  
                                zos.write(bufs,0,read);  
                            }  
                        }  
                        flag = true;  
                    }  
                }  
            } catch (FileNotFoundException e) {  
                e.printStackTrace();  
                throw new RuntimeException(e);  
            } catch (IOException e) {  
                e.printStackTrace();  
                throw new RuntimeException(e);  
            } finally{  
                //关闭流  
                try {  
                    if(null != bis) bis.close();  
                    if(null != zos) zos.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                    throw new RuntimeException(e);  
                }  
            }  
        }  
        return flag;  
    }  
}
