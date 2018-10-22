package com.mrll.javelin.common.test.filter

import spock.lang.Specification
import spock.lang.Subject

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class SuppressDuringTestsFilterSpec extends Specification {
    HttpServletRequest request = Mock()
    HttpServletResponse response = Mock()
    FilterChain filterChain = Mock()

    @Subject
    TestSuppressionContextFilter debugSuppressibleFilter

    def setup() {
        request = Mock()
        response = Mock()
        filterChain = Mock()
        debugSuppressibleFilter = new TestSuppressionContextFilter()
        TestSuppressionContext.clear()
    }

    def 'testSuppressionFilter turns suppression on when header set'() {
        given:
        request.getHeader('SUPPRESS_RUN') >> true

        when:
        debugSuppressibleFilter.doFilter(request, response, filterChain)

        then:
        TestSuppressionContext.isSupressionOn()
    }

    def 'testSuppressFilter turns off suppress when header set to false'() {
        given:
        request.getHeader('SUPPRESS_RUN') >> false
        when:
        debugSuppressibleFilter.doFilter(request, response, filterChain)

        then:
        !TestSuppressionContext.isSupressionOn()
    }

}
