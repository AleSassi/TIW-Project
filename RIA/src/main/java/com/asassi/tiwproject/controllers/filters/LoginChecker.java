package com.asassi.tiwproject.controllers.filters;

import com.asassi.tiwproject.constants.PageConstants;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginChecker implements Filter {

	public LoginChecker() {
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		HttpSession s = req.getSession();
		boolean isLoggedIn = s.getAttribute("username") != null;
		String uri = req.getRequestURI();
		if (uri.equals("/") || uri.equals("") || uri.equals("/signup")) {
			if (isLoggedIn) {
				res.sendRedirect(PageConstants.Home.getRawValue());
				return;
			}
		} else if (uri.equals("/home") || uri.equals("/logout")) {
			if (!isLoggedIn) {
				res.sendRedirect(PageConstants.Default.getRawValue());
				return;
			}
		} else if (s.isNew() || !isLoggedIn) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return;
		}

		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
