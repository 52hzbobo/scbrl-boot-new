package com.scbrl.wechat.config;

/**
 * @Title： 公共基础配置
 * @author Bruce.liu
 * @date 2017年3月3日 下午4：34：08 
 * @version V1.0   
 * @Description： 
 */
public class BaseConfig {

    public static final String SERVICE_HOST_URL = "http://brl.free.ngrok.cc";//本地花生壳 - 开发测试用回调地址
	//public static final String SERVICE_HOST_URL = "http://liuwb730.vicp.io";//本地花生壳 - 开发测试用回调地址
	//public static final String SERVICE_HOST_URL = "http://wuyu22222.vicp.cc";//域名地址
	

	/**
	 * @微信公众号
	 */
	public static final String APPID    = "wx8027dffd0e25c757";
    public static final String SECRET   = "bafebe32ba69b1b716746930fe2cbf01";
    public static final String AUTH_TOKEN   = "bruceliu";//开发模式下公众号认证配置的Token（需与此一致！！）
    public static final String PARTNER  = "1415244102"; 
    public static final String PARTNER_KEY = "0d7632fd294f45146a04391e5bc68055";
    public static String notifyurl = "/pay/wxPayCallback";// 微信公众号回调地址 测试.
	
    public static final String WX_RETURN_URL = "/wechat/authorize/result";//微信获取用户信息 - 回调函数
    public static String SESSION_WEIXIN_ID ="SESSION_WEIXIN_ID";//微信用户信息 Session key
    public static String REDIS_WECHAT_ACCESS_TOKEN_KEY = "REDIS_WECHAT_ACCESS_TOKEN_KEY";
    public static String REDIS_WECHAT_TICKET_KEY = "REDIS_WECHAT_TICKET_KEY";
	
	
    
}
