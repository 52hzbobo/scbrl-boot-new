package com.scbrl.wechat.base;

import com.scbrl.dto.ResultModel;
import com.scbrl.util.JsonUtil;
import com.scbrl.util.RedisUtil;
import com.scbrl.wechat.config.BaseConfig;
import com.scbrl.wechat.util.PageData;
import com.scbrl.wechat.util.WeChatUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class BaseWeChatController  {

	/** new PageData对象
	 * @return
	 */
	public PageData getPageData(){
		return new PageData(this.getRequest());
	}

	/**得到ModelAndView
	 * @return
	 */
	public ModelAndView getModelAndView(){
		return new ModelAndView();
	}

	/**得到request对象
	 * @return
	 */
	public HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}

	/**
	 * 获取Session
	 * 当Session要换成其他来维持时，便于统一修改Session
	 * @return
	 */
	public HttpSession getSession(){
		HttpServletRequest requst = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return requst.getSession();
	}

	/**
	 * 将数据存入Session
	 * @param k Key
	 * @param obj 对象
	 */
	public void setSession(String k , Object obj){
		getSession().setAttribute(k, obj);
	}

	/**
	 * 获取Requset的Session
	 * @param k
	 * @return
	 */
	public Object getSession(String k){
		return (Object)getSession().getAttribute(k);
	}


	/**
	 * 返回系统异常状态
	 *
	 * @return ResultModel
	 */
	public ResultModel Error(){
		ResultModel result = new ResultModel();
		result.setCode("ErrorSystem");
		result.setMsg("系统异常，请联系开发人员！");
		return result;
	}

	/**
	 * 返回异常状态
	 *
	 * @param code 状态码
	 * @param msg  说明
	 * @return ResultModel
	 */
	public ResultModel Error(String code ,String msg){
		ResultModel result = new ResultModel();
		result.setCode(code);
		result.setMsg(msg);
		return result;
	}

	/**
	 * 返回失败状态
	 *
	 * @return ResultModel
	 */
	public ResultModel Failure(String msg){
		ResultModel result = new ResultModel();
		result.setCode("Failure");
		result.setMsg(msg);
		return result;
	}

	/**
	 * 返回成功状态
	 *
	 * @return ResultModel
	 */
	public ResultModel Succeed(){
		ResultModel result = new ResultModel();
		result.setCode("Succeed");
		result.setMsg("成功");
		return result;
	}

	/**
	 * 返回成功状态
	 *
	 * @param data 说明
	 * @return ResultModel
	 */
	public ResultModel Succeed(Object data){
		ResultModel result = new ResultModel();
		result.setCode("Succeed");
		result.setMsg("成功");
		result.setData(data);
		return result;
	}


	/**
	 * @功能: 获取微信AccessToken
	 */
	public Map<String,Object> getWxAccessToken(){
		try {
			Map<String,Object> wxAccessToken = RedisUtil.get(BaseConfig.REDIS_WECHAT_ACCESS_TOKEN_KEY)==null?null:
					JsonUtil.toMap(RedisUtil.get(BaseConfig.REDIS_WECHAT_ACCESS_TOKEN_KEY).toString());
			if(wxAccessToken == null){
				wxAccessToken = WeChatUtil.getWxAccessToken();
				RedisUtil.put(BaseConfig.REDIS_WECHAT_ACCESS_TOKEN_KEY, JsonUtil.toJson(wxAccessToken), ((Double)wxAccessToken.get("expires_in")).intValue());
			}
			return wxAccessToken;
		}catch (Exception e){
			e.printStackTrace();
			Map<String,Object> wxAccessToken = WeChatUtil.getWxAccessToken();
			RedisUtil.put(BaseConfig.REDIS_WECHAT_ACCESS_TOKEN_KEY, JsonUtil.toJson(wxAccessToken), ((Double)wxAccessToken.get("expires_in")).intValue());
			return wxAccessToken;
		}
	}

	/**
	 * @功能: 获取微信ticket
	 */
	public  Map<String,Object> getWxTicket(){
		try {
			Map<String,Object> wxAccessToken = getWxAccessToken();
			Map<String,Object> wxTicket = RedisUtil.get(BaseConfig.REDIS_WECHAT_TICKET_KEY)==null?null:
					JsonUtil.toMap(RedisUtil.get(BaseConfig.REDIS_WECHAT_TICKET_KEY).toString());
			if(wxTicket == null || !wxTicket.get("errcode").equals(0)){
				wxAccessToken = WeChatUtil.getWxAccessToken();
				RedisUtil.put(BaseConfig.REDIS_WECHAT_ACCESS_TOKEN_KEY, JsonUtil.toJson(wxAccessToken), ((Double)wxAccessToken.get("expires_in")).intValue() );
				wxTicket = WeChatUtil.getWxTicket(wxAccessToken.get("access_token").toString());
				RedisUtil.put(BaseConfig.REDIS_WECHAT_TICKET_KEY, JsonUtil.toJson(wxTicket), ((Double)wxAccessToken.get("expires_in")).intValue());
			}
			return wxTicket;
		} catch (Exception e){
			e.printStackTrace();
			Map<String,Object> wxAccessToken = getWxAccessToken();
			RedisUtil.put(BaseConfig.REDIS_WECHAT_ACCESS_TOKEN_KEY, JsonUtil.toJson(wxAccessToken), ((Double)wxAccessToken.get("expires_in")).intValue());
			Map<String,Object> wxTicket = WeChatUtil.getWxTicket(wxAccessToken.get("access_token").toString());
			RedisUtil.put(BaseConfig.REDIS_WECHAT_TICKET_KEY, JsonUtil.toJson(wxTicket), ((Double)wxAccessToken.get("expires_in")).intValue());
			return wxTicket;
		}
	}
}
