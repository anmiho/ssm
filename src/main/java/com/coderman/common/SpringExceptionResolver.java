package com.coderman.common;

import com.coderman.exception.BizException;
import com.coderman.exception.ParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class SpringExceptionResolver implements HandlerExceptionResolver {

    private Logger log= LoggerFactory.getLogger(SpringExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String url = request.getRequestURL().toString();
        ModelAndView mv;
        String defaultMsg = "系统异常,请联系管理员";

        if (isAjax(request)) {
            if (ex instanceof BizException) {
                log.error("ajax请求:业务异常, url:" + url, ex);
                JsonData result = JsonData.fail(ex.getMessage());
                mv = new ModelAndView("jsonView", result.toMap());
            } else if(ex instanceof ParamException){
                log.error("ajax请求:参数异常, url:" + url, ex);
                JsonData result = JsonData.fail(JsonData.PARAM_ERROR,ex.getMessage());
                mv = new ModelAndView("jsonView", result.toMap());
            } else {//系统异常
                log.error("ajax请求:系统异常, url:" + url, ex);
                JsonData result = JsonData.fail(JsonData.INTERNAL_SERVER_ERROR,defaultMsg);
                mv = new ModelAndView("jsonView", result.toMap());
            }
        } else {
            log.error("页面请求出现异常:" + url, ex);
            Map<String,Object> map=new HashMap<>();
            map.put("errorMsg",ex.toString());
            mv = new ModelAndView("error/exception",map);
        }
        return mv;
    }

    /**
     * 判断网络请求是否为ajax
     *
     * @param req
     * @return
     */
    private boolean isAjax(HttpServletRequest req) {
        String contentTypeHeader = req.getHeader("Content-Type");
        String acceptHeader = req.getHeader("Accept");
        String xRequestedWith = req.getHeader("X-Requested-With");
        return (contentTypeHeader != null && contentTypeHeader.contains("application/json"))
                || (acceptHeader != null && acceptHeader.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(xRequestedWith);
    }
}
