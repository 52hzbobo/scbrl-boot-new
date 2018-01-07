package com.scbrl.wechat.controller;

import com.scbrl.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 模块:【类模块名称】
 * <p>
 * 开发: Bruce.Liu By 2017/11/18 上午10:46 Create
 */
@Controller
@Slf4j
public class TestController {

    /**
     * 默认启动页
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

}
