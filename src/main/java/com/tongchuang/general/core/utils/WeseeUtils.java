package com.tongchuang.general.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tongchuang.general.core.constant.ResEnum;
import com.tongchuang.general.core.constant.WeseeLink;
import com.tongchuang.general.core.exception.BizException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WeseeUtils {

    public final static String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";


    public static Document findWeseeDoc(String url) throws IOException {
        //打开微视分享链接
        Connection weishiConn = Jsoup.connect(url);
        //模拟浏览器设置请求头
        weishiConn.header("User-Agent", userAgent);
        //获取响应结果
        Connection.Response weishiConnRes = weishiConn.ignoreContentType(true).method(Connection.Method.GET).execute();
        //获取全文文档
        Document weishiConnResDoc = weishiConnRes.parse();
        return weishiConnResDoc;
    }

    public static String findVideoLink(String url) throws IOException {
        //获取全文文档
        Document weishiConnResDoc = findWeseeDoc(url);
        //获取微视iframe标签
        Elements iframe = weishiConnResDoc.getElementsByTag("iframe");

        //获取微视iframe属性src值并且打开
        Connection iframeConn = Jsoup.connect(iframe.get(0).absUrl("src"));
        //模拟浏览器设置请求头
        iframeConn.header("User-Agent", userAgent);
        //获取响应结果
        Connection.Response iframeConnRes = iframeConn.ignoreContentType(true).method(Connection.Method.GET).execute();
        //获取全文文档
        Document iframeConnResDoc = iframeConnRes.parse();
        //获取微视scripts标签组
        Elements scripts = iframeConnResDoc.getElementsByTag("script");
        return findVideoLink(scripts);
    }

    public static String findVideoLink(Elements elements){
        //获取第10行带有window.syncData的script
        String scriptHtml = elements.get(10).html();
        //获取json
        String json = scriptHtml.substring(scriptHtml.indexOf("{"), scriptHtml.lastIndexOf("}") + 1);
        //转json对象
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getJSONArray("feeds").getJSONObject(0).getString("video_url").replaceFirst("http", "https");
    }

    public static String findImgLink(String url) throws IOException {
        //获取全文文档
        Document weishiConnResDoc = findWeseeDoc(url);
        //获取微视iframe标签
        Elements iframe = weishiConnResDoc.getElementsByTag("iframe");

        //获取微视iframe属性src值并且打开
        Connection iframeConn = Jsoup.connect(iframe.get(0).absUrl("src"));
        //模拟浏览器设置请求头
        iframeConn.header("User-Agent", userAgent);
        //获取响应结果
        Connection.Response iframeConnRes = iframeConn.ignoreContentType(true).method(Connection.Method.GET).execute();
        //获取全文文档
        Document iframeConnResDoc = iframeConnRes.parse();
        return findImgLink(iframeConnResDoc.getElementsByClass("player-cover j-player-cover"));
    }
    public static String findImgLink(Elements elements) {
        String attr = elements.get(0).attr("style");
        return "https:" + attr.substring(attr.indexOf("(") + 1, attr.lastIndexOf(")"));
    }

    public static WeseeLink findLink(String url) throws IOException {
        //获取全文文档
        Document weishiConnResDoc = findWeseeDoc(url);
        //获取微视iframe标签
        Elements iframe = weishiConnResDoc.getElementsByTag("iframe");
        if(iframe.size() == 0){
            throw new BizException("链接不合法").end(ResEnum.BAD_REQUEST);
        }

        //获取微视iframe属性src值并且打开
        Connection iframeConn = Jsoup.connect(iframe.get(0).absUrl("src"));
        //模拟浏览器设置请求头
        iframeConn.header("User-Agent", userAgent);
        //获取响应结果
        Connection.Response iframeConnRes = iframeConn.ignoreContentType(true).method(Connection.Method.GET).execute();
        //获取全文文档
        Document iframeConnResDoc = iframeConnRes.parse();
        String imageLink;
        String videoLink;
        try{
            imageLink = findImgLink(iframeConnResDoc.getElementsByClass("player-cover j-player-cover"));
            videoLink = findVideoLink(iframeConnResDoc.getElementsByTag("script"));
        }catch (Exception e){
            String msg = iframeConnResDoc.getElementsByClass("tips").get(0).getElementsByTag("span").html();
            throw new BizException(msg).end(ResEnum.NOT_FOUND);
        }
        WeseeLink weseeLink = new WeseeLink();
        weseeLink.setImageLink(imageLink);
        weseeLink.setVideoLink(videoLink);
        return weseeLink;
    }
}
