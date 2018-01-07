package com.scbrl.wechat.controller;

import com.scbrl.dto.ResultModel;
import com.scbrl.util.RedisUtil;
import com.scbrl.wechat.base.BaseWeChatController;
import com.scbrl.wechat.config.BaseConfig;
import com.scbrl.wechat.util.PageData;
import com.scbrl.wechat.util.WeChatUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @Title: 微信公共号公共模块
 * @author Bruce.liu
 * @date 2017年4月6日 下午8:48:31 
 */
@Controller
@RequestMapping(value="/wechat")
public class WeChatController extends BaseWeChatController {


    /**
     * @功能: 微信服务端(开发模式)认证Url
     * @日期: liuwb by 2016-09-12 on create
     * @说明: 微信平台管理 - (启用)服务器配置开启开发模式 / 事件回调访问函数
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/check/sign")
    @ResponseBody
    public void apiWxCheckSignature(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String signature = request.getParameter("signature");
        String timestamp = request.getParameter("timestamp");
        String nonce = request.getParameter("nonce");
        String openid = request.getParameter("openid");
        String echostr = request.getParameter("echostr");
        if (echostr != null) { /** 微信服务端认证Url **/
            PrintWriter out = response.getWriter();
            // 请求校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
            if (WeChatUtil.checkSignature(signature, timestamp, nonce)) {
                out.print(echostr);
            }
            out.close();
            out = null;
        } else {
            //测试微信号开发模式-创建菜单
            WeChatUtil.wxCreateMenu(getWxAccessToken().get("access_token").toString());
            WeChatUtil.Oauth2Authorize(response, BaseConfig.SERVICE_HOST_URL, "test");
            System.out.println("OpenId:"+openid);
        }
    }


	/** ----------------------------------------------------------------------------------------------------**/
    /** **************************************** Star 微信认证处理模块 Star  ******************************* **/
    /** ----------------------------------------------------------------------------------------------------**/

    /**
     * @param response
     * @throws Exception
     * @功能: 微信获取openid与用户授权信息
     * @日期: liuwb 2016-08-16 19:20 by create
     * @说明: return_url 微信返回的回调方法
     */
    @RequestMapping("/oauth2/authorize")
    public void apiWxoOauth2Authorize(HttpServletResponse response) throws Exception {
        try {
            PageData pd = this.getPageData();
            WeChatUtil.Oauth2Authorize(response, BaseConfig.WX_RETURN_URL, pd.getString("reqUrl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return jsonString
     * @功能: 微信获取用户信息 - 回调函数
     * @日期: liuwb 2016-08-16 19:22 by create
     * @说明: 根据code获取accessToken 授权获取用户信息
     */
    @RequestMapping("/authorize/result")
    public void apiWxAuthorizeResult(HttpServletResponse response) {
        PageData pd = this.getPageData();
        try {
            String code = pd.getString("code");
            String reqUrl = pd.getString("state");
            if(code != null && !"".equals(code.trim())){
	            pd = WeChatUtil.Oauth2AccessToken(code);// 获取openid、access_token
	            PageData wxInfoSrm = WeChatUtil.snsUserInfo(pd.getString("openid"),pd.getString("access_token"));
	            wxInfoSrm.put("scope", pd.getString("scope"));
	            wxInfoSrm.put("expires_in", pd.get("expires_in"));
	            wxInfoSrm.put("access_token", pd.getString("access_token"));
	            wxInfoSrm.put("refresh_token", pd.getString("refresh_token"));
	            RedisUtil.put(pd.getString("openid"), "", ((Double)pd.get("expires_in")).intValue());
				 //TODO: 授权后处理逻辑(如根据用户ID获取Token)
				setSession(BaseConfig.SESSION_WEIXIN_ID, wxInfoSrm);
				response.sendRedirect(BaseConfig.SERVICE_HOST_URL + reqUrl);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @功能: 微信刷新access_token
     * @日期: liuwb 2016-08-17 09:12 by create
     * @说明: 由于access_token拥有较短的有效期，当access_token超时后，可以使用refresh_token进行刷新，
     * refresh_token有效期为30天，当refresh_token失效之后，需要用户重新授权。
     */
    @RequestMapping("/refresh/token")
    @ResponseBody
    public ResultModel apiWxRefreshToken() {
        PageData pd =  this.getPageData();
        try {
            String refreshToken = pd.getString("refresh_token");
            Long tokenTime = new Date().getTime();
            pd = WeChatUtil.Oauth2RefreshToken(refreshToken);
            pd.put("token_time", tokenTime);
            return Succeed(pd);
        } catch (Exception e) {
            e.printStackTrace();
            return Error();
        }
    }
	
}
