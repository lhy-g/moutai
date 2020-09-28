package com.tongchuang.general.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDateUtils extends org.apache.commons.lang3.time.DateUtils {

	public static int Guid=100;
	
	public static int timeStamp=100;
	
	public static SimpleDateFormat dateFormat=new SimpleDateFormat("yyMMdd"); 
	
    public static Date now(){
        return new Date();
    }
    
    /**
     * 获取时间戳
     * @return
     */
    public static String getTimeStamp() {
    	return String.valueOf(System.currentTimeMillis()/1000);
    }
    
    /**
     * 生成16位ID
     * [6位年月日+7位时间戳+3位随机数]
	 *  
	 */
	public static String getGuid() {
		MyDateUtils.Guid+=1;
		long now = System.currentTimeMillis();  
		String time=dateFormat.format(now);
		String info=String.valueOf(now);
		//获取三位随机数  
		//int ran=(int) ((Math.random()*9+1)*100); 
		//要是一段时间内的数据连过大会有重复的情况，所以做以下修改
		int ran=0;
		if(MyDateUtils.Guid>999){
			MyDateUtils.Guid=100;    	
		}
		ran=MyDateUtils.Guid;
		return time+info.substring(6, info.length())+ran;  
	}
	
	public static String getNowTimeStamp() {
		MyDateUtils.timeStamp+=1;
		long now = System.currentTimeMillis();  
		String info=String.valueOf(now);
		//获取三位随机数  
		//int ran=(int) ((Math.random()*9+1)*100); 
		//要是一段时间内的数据连过大会有重复的情况，所以做以下修改
		int ran=0;
		if(MyDateUtils.timeStamp>999){
			MyDateUtils.timeStamp=100;    	
		}
		ran=MyDateUtils.timeStamp;
		return info.substring(0,10)+ran;  
	}
	
	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 2000; i++) {
		//	System.out.println(System.currentTimeMillis());
			//System.out.println(getTimeStamp());
			String str = getNowTimeStamp();
			System.out.println(str);
			if(str.endsWith("999")) {
				Thread.sleep(1000);
			}
		}
	}
}
