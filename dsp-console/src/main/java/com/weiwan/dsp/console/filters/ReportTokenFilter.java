package com.weiwan.dsp.console.filters;

import com.weiwan.dsp.core.pub.SystemEnvManager;
import org.codehaus.plexus.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author: xiaozhennan
 * @Date: 2022/3/9 18:59
 * @Package: com.weiwan.dsp.console.filters
 * @ClassName: ReportTokenFilter
 * @Description: 数据上报过滤器
 **/
@WebFilter(urlPatterns = {"/metrics/report", "/unresolved/report"})
public class ReportTokenFilter implements Filter {

    private static final String SIGN_HEADER_KEY = "Dsp-Report-Sign";

    private static final String signKey = SystemEnvManager.getInstance().getDspSignKey();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if(servletRequest == null) return;
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        String sign = httpRequest.getHeader(SIGN_HEADER_KEY);
        if(StringUtils.isNotBlank(sign)){
            // do Nothing
            System.out.println(sign);
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            httpResponse.setStatus(401);
            httpResponse.setContentType("application/text");
            PrintWriter writer = httpResponse.getWriter();
            writer.print("Access Denied!");
        }
    }
}
