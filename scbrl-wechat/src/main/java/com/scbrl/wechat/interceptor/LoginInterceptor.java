package com.scbrl.wechat.interceptor;

import com.scbrl.interfaces.LoginAuth;
import com.scbrl.util.RedisUtil;
import com.scbrl.wechat.config.BaseConfig;
import com.scbrl.wechat.util.PageData;
import com.scbrl.wechat.util.WeChatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 统一登录拦截器
 * Created by Bruce.Liu on 2017/8/28.
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


    public LoginInterceptor() {
        super();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  {
        /**
         * 对来自后台的请求统一进行日志处理
         */
        try{
            /**
                String url = request.getRequestURL().toString();
                String method = request.getMethod();
                String uri = request.getRequestURI();
                String queryString = request.getQueryString();
                System.out.println(request.getParameterMap().toString())
            **/
            if(handler.getClass().isAssignableFrom(HandlerMethod.class)) {
                LoginAuth auth = ((HandlerMethod) handler).getMethodAnnotation(LoginAuth.class);
                if(auth != null && auth.isWeChat()){
                    PageData wxUeseInfo = (PageData)request.getSession().getAttribute(BaseConfig.SESSION_WEIXIN_ID);
                    if(wxUeseInfo==null){
                        String reqUrl = request.getRequestURI().toString();
                        String param = request.getQueryString();
                        if(param != null) {
                            reqUrl += "?" + param;
                        }
                        WeChatUtil.Oauth2Authorize(response,BaseConfig.WX_RETURN_URL ,reqUrl);
                        return false;
                    } else {
                        /** 判断微信用户网页授权token是否过期,过期则重新请求授权 **/
                        if(RedisUtil.get(wxUeseInfo.getString("openid"))==null){
                            PageData RefreshToken = WeChatUtil.Oauth2RefreshToken(wxUeseInfo.getString("refresh_token"));
                            wxUeseInfo.putAll(RefreshToken);
                            request.getSession().setAttribute(BaseConfig.SESSION_WEIXIN_ID,wxUeseInfo);
                            RedisUtil.put(wxUeseInfo.getString("openid"), "", (Integer)wxUeseInfo.get("expires_in"));
                        }
                        return true;
                    }
                }
            }
            //log.info(String.format("请求参数, url: %s, method: %s, uri: %s, params: %s", url, method, uri, queryString));
            } catch (Exception e){
                e.printStackTrace();
            }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse,
                                Object o, Exception e) throws Exception {

    }


}
