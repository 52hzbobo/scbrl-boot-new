package com.scbrl.wechat.controller;

import com.scbrl.dto.ResultModel;
import com.scbrl.interfaces.LoginAuth;
import com.scbrl.util.RedisUtil;
import com.scbrl.wechat.base.BaseWeChatController;
import com.scbrl.wechat.config.BaseConfig;
import com.sun.net.httpserver.Authenticator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;

/**
 * 模块:【类模块名称】
 * <p>
 * 开发: Bruce.Liu By 2018/1/7 下午1:51 Create
 */
@Controller
public class IndexController extends BaseWeChatController {

    /**
     * 默认启动页Hello World!
     */
    @RequestMapping("/")
    public ModelAndView index(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        RedisUtil.put("BruceLiu","Hello Word Wechat Demo");
        ModelAndView mv = new ModelAndView();
        mv.setViewName("index");
        mv.addObject("msg",RedisUtil.get("BruceLiu"));
        return mv;
    }

    /**
     * 测试微信授权获取用户信息
     */
    @LoginAuth(isWeChat = true)
    @RequestMapping("/test")
    @ResponseBody
    public ResultModel test(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        return Succeed(getSession(BaseConfig.SESSION_WEIXIN_ID));
    }
}
