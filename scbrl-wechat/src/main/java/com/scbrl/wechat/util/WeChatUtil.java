package com.scbrl.wechat.util;

import com.scbrl.util.HttpUtil;
import com.scbrl.util.JsonUtil;
import com.scbrl.wechat.config.BaseConfig;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


public class WeChatUtil {
	 /**
     * @功能 微信网页Oauth2授权
     * @日期 liuwb 2016-08-16 15:36 by create
     * @说明 oauth2授权需在微信公众号平台设置你的回调地址
     * @param response   response.sendRedirect(url); 需用response发送请求
     * @param return_url 回调的URL地址
     * @scope [snsapi_base 只获取openid] | [snsapi_userinfo 获取用户信息]
     */
    public static void Oauth2Authorize(HttpServletResponse response,String return_url,String reqUrl) throws Exception {
        String apiUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI" +
                "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        return_url = URLEncoder.encode(BaseConfig.SERVICE_HOST_URL + return_url, "UTF-8");
        apiUrl = apiUrl.replace("REDIRECT_URI", return_url).replace("APPID", BaseConfig.APPID).replace("STATE",reqUrl);
        response.sendRedirect(apiUrl);
    }

    /**
     * @功能: 微信网页获取AccessToken
     * @日期: liuwb 2016-08-16 17:18 by create
     * @param code oauth2Authorize微信接口回调返回的code
     * @return PageData 返回信息:
     *         access_token	    网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     *         expires_in	    ccess_token接口调用凭证超时时间，单位（秒）
     *         refresh_token	用户刷新access_token
     *         openid	        用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
     *         scope	        用户授权的作用域，使用逗号（,）分隔
     */
    public static PageData Oauth2AccessToken(String code) throws Exception {
        String accurl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID" +
                "&secret=SECRET&code=CODE&grant_type=authorization_code";
        accurl = accurl.replace("APPID",BaseConfig.APPID).replace("SECRET",BaseConfig.SECRET).replace("CODE", code);
        String result = HttpUtil.executeGet(accurl, null);
        PageData SRModel = new PageData();
        SRModel.putAll(JsonUtil.toMap(result));
        return SRModel;
    }

    /**
     * @功能 微信网页Oauth2授权获取用户信息
     * @日期 liuwb 2016-08-16 17:37 by create
     * @param openid 微信openid
     * @param access_token 微信access_token
     * @return PageData
     *         openid	    用户的唯一标识
     *         nickname     用户昵称
     *         sex	        用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     *         province	    用户个人资料填写的省份
     *         city	        普通用户个人资料填写的城市
     *         country	    国家，如中国为CN
     *         headimgurl	用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     *         privilege	用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
     *         unionid	    只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。详见：获取用户个人信息（UnionID机制）
     */
    public static PageData snsUserInfo(String openid,String access_token) throws Exception{
        String userinfoUrl  = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        userinfoUrl = userinfoUrl.replace("OPENID",openid);
        userinfoUrl = userinfoUrl.replace("ACCESS_TOKEN",access_token);
        String userInfoResult = HttpUtil.executeGet(userinfoUrl,null);
        PageData SRModel = new PageData();
        SRModel.putAll(JsonUtil.toMap(userInfoResult));
        return SRModel;
    }

    /**
     * @功能: 微信 access_token 是否可以用(true 可用/ flase 失效)
     * @日期  liuwb 2016-08-16 18:42 by create
     * @param openid 微信openid
     * @param access_token 微信access_token
     * @return { "errcode":0,"errmsg":"ok"}
     */
    public static boolean snsAuth(String openid,String access_token) throws Exception{
        String snsAuthUrl  = "https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID";
        snsAuthUrl = snsAuthUrl.replace("OPENID",openid);
        snsAuthUrl = snsAuthUrl.replace("ACCESS_TOKEN",access_token);
        String userInfoResult = HttpUtil.executeGet(snsAuthUrl,null);
        PageData SRModel = new PageData();
        SRModel.putAll(JsonUtil.toMap(userInfoResult));
        return SRModel.getString("errcode").equals("0") ? true : false;
    }

    /**
     * @日期  liuwb 2016-08-16 18:45 by create
     * @功能: 微信 刷新access_token
     *        由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，
     *        refresh_token拥有较长的有效期（7天、30天、60天、90天），当refresh_token失效的后，需要用户重新授权。
     * @param access_token 微信access_token
     * @return PageData 返回信息:
     *         access_token	    网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
     *         expires_in	    access_token接口调用凭证超时时间，单位（秒）
     *         refresh_token	用户刷新access_token
     *         openid	        用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
     *         scope	        用户授权的作用域，使用逗号（,）分隔
     */
    public static PageData Oauth2RefreshToken(String access_token) throws Exception{
        String refreshTokenUrl  = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
        refreshTokenUrl = refreshTokenUrl.replace("APPID",BaseConfig.APPID);
        refreshTokenUrl = refreshTokenUrl.replace("REFRESH_TOKEN",access_token);
        String userInfoResult = HttpUtil.executeGet(refreshTokenUrl,null);
        PageData SRModel = new PageData();
        SRModel.putAll(JsonUtil.toMap(userInfoResult));
        return SRModel;
    }

    /**
     * @日期: liuwb 2016-08-23 09:24 by create
     * @功能: 获取服务端授权access_token(非网页授权access_token)
     * @说明: acess_token是公众号的全局唯一接口调用凭据，公众号调用各接口时都需使用access_token。
     *        开发者需要进行妥善保存。access_token的存储至少要保留512个字符空间。access_token的有效期目前为2个小时，
     *        需定时刷新，重复获取将导致上次获取的access_token失效。
     */
    public static Map<String,Object> getWxAccessToken(){
        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        tokenUrl = tokenUrl.replace("APPID",BaseConfig.APPID);
        tokenUrl = tokenUrl.replace("APPSECRET",BaseConfig.SECRET);
        try{
            String tokenJson = HttpUtil.executeGet(tokenUrl,null);
            Map<String,Object> mp =  JsonUtil.toMap(tokenJson);
            return mp ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String,Object> getWxAccessToken(String appid,String secret){
        String tokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        tokenUrl = tokenUrl.replace("APPID",appid);
        tokenUrl = tokenUrl.replace("APPSECRET",secret);
        try{
            String tokenJson = HttpUtil.executeGet(tokenUrl,null);
            Map<String,Object> mp =  JsonUtil.toMap(tokenJson);
            return mp ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    /**
     * @功能: 获取自定义菜单
     * @param accessToken
     */
    public static Map<String,Object> wxCreateMenu(String accessToken){
        String menuUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
        menuUrl = menuUrl.replace("ACCESS_TOKEN",accessToken);
        try{
            String param = JsonUtil.toJson(getWxMenu());
            param = new String(param.getBytes("utf-8"));
            String result = HttpUtil.sendPost(menuUrl, param);
            System.err.println(JsonUtil.toJson(result));
            Map<String,Object> mp =  JsonUtil.toMap(result);
            return mp ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * return {
         "errcode":0,
         "errmsg":"ok",
         "ticket":"bxLdikRXVbTPdHSM05e5u5sUoXNKd8-41ZO3MhKoyN5OfkWITDGgnr2fwJ0m9E8NYzWKVZvdVtaUgWvsdshFKA",
         "expires_in":7200
         }
     * @param accessToken
     */
    public static Map<String,Object> getWxTicket(String accessToken){
        String ticketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
        ticketUrl = ticketUrl.replace("ACCESS_TOKEN",accessToken);
        try{
            String tokenJson = HttpUtil.executeGet(ticketUrl,null);
            Map<String,Object> mp =  JsonUtil.toMap(tokenJson);
            return mp ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 校验签名
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @return
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        // 对token、timestamp和nonce按字典排序
        String[] paramArr = new String[] { BaseConfig.AUTH_TOKEN, timestamp, nonce };
        Arrays.sort(paramArr);
        // 将排序后的结果拼接成一个字符串
        String content = paramArr[0].concat(paramArr[1]).concat(paramArr[2]);
        String ciphertext = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 对接后的字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            ciphertext = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        // 将sha1加密后的字符串与signature进行对比
        return ciphertext != null ? ciphertext.equals(signature.toUpperCase()) : false;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }

    /**
     * @功能: 菜单格式
        {
            "button": [
            {
                "name": "开摩邦网",
                    "sub_button": [
                {
                    "type": "view",
                    "name": "我要推广",
                    "url": "http://15558f1v51.imwork.net/api/wx/my/share"
                },
                {
                    "type": "view",
                    "name": "用户入驻",
                    "url": "http://15558f1v51.imwork.net/api/wx/league/main"
                }
                ]
            },
            {
                "type": "view",
                "name": "会员中心",
                "url": "http://15558f1v51.imwork.net/api/wx/user/members/center"
            },
            {
                "name": "平台审核",
                    "sub_button": [
                {
                    "type": "view",
                    "name": "平台(正式)",
                    "url": "http://moto.iwop.cn/api/wx/kmb/audit/center"
                },
                {
                    "type": "view",
                    "name": "平台(内网)",
                     "url": "http://15558f1v51.imwork.net/api/wx/kmb/audit/center"
                }
                ]
            }
            ]
        }
     * @return
     */
    public static Map<String,Object> getWxMenu(){
        Map<String,Object> menu = new HashMap<>();
        List<Map<String,Object>> mpList = new ArrayList<>();
        Map<String,Object> mapA_Menu = new HashMap<>();
        mapA_Menu.put("name","Bruce");
        List<Map<String,Object>> mpAList = new ArrayList<>();
        Map<String,Object> mapA1 = new HashMap<>();
        mapA1.put("type","view");
        mapA1.put("name","菜单一");
        mapA1.put("url","http://brl.free.ngrok.cc/test");
        mpAList.add(mapA1);
        Map<String,Object> mapA2 = new HashMap<>();
        mapA2.put("type","view");
        mapA2.put("name","菜单二");
        mapA2.put("url","http://brl.free.ngrok.cc/test");
        mpAList.add(mapA2);
        mapA_Menu.put("sub_button",mpAList);
        Map<String,Object> mapB_Menu = new HashMap<>();
        mapB_Menu.put("type","view");
        mapB_Menu.put("name","Liu");
        mapB_Menu.put("url","http://brl.free.ngrok.cc/test");
        mpList.add(mapA_Menu);
        mpList.add(mapB_Menu);
        menu.put("button",mpList);
        return menu;
    }

    /**
     * 获取微信关注的openid列表
     * @param accessToken
     * @param nextOpenid
     */
    public static String getWxAttentionUser(String accessToken,String nextOpenid){
        String userUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
        userUrl = userUrl.replace("ACCESS_TOKEN",accessToken);
        userUrl = userUrl.replace("NEXT_OPENID",nextOpenid==null?"":nextOpenid);
        try{
            String result = HttpUtil.executeGet(userUrl,null);
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
