package com.hdsx.box.net.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by jzh on 2017/7/26.
 */
public class CrossDomainFilter implements Filter {

    public CrossDomainFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    	 HttpServletResponse resp = (HttpServletResponse)response;
    	
		 resp.addHeader("Access-Control-Allow-Origin", "*");
	     resp.addHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With");
	     resp.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
	     chain.doFilter(request, response);
			
       
       
    }

    public void destroy() {
    }
}