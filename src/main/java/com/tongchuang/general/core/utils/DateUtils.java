package com.tongchuang.general.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	public static int Guid=100;
	
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
		DateUtils.Guid+=1;
		long now = System.currentTimeMillis();  
		String time=dateFormat.format(now);
		String info=String.valueOf(now);
		//获取三位随机数  
		//int ran=(int) ((Math.random()*9+1)*100); 
		//要是一段时间内的数据连过大会有重复的情况，所以做以下修改
		int ran=0;
		if(DateUtils.Guid>999){
			DateUtils.Guid=100;    	
		}
		ran=DateUtils.Guid;
		return time+info.substring(6, info.length())+ran;  
	}
}
