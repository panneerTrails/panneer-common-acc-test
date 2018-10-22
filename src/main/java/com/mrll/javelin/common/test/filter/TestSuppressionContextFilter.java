package com.mrll.javelin.common.test.filter;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class TestSuppressionContextFilter implements Filter {

    private static final String HEADER = "SUPPRESS_RUN";
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSuppressionContextFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String suppressRunHeader = httpServletRequest.getHeader(HEADER);
        boolean shouldSuppress = BooleanUtils.toBoolean(suppressRunHeader);
        LOGGER.trace("message=runSuppression enabled={}", shouldSuppress);
        if (shouldSuppress) {
            TestSuppressionContext.turnSuppressionOn();
        } else {
            TestSuppressionContext.turnSuppressionOff();
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //none required
    }

    @Override
    public void destroy() {
        //none required
    }
}
