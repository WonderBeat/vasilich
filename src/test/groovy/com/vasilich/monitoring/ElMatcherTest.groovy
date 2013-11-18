package com.vasilich.monitoring

import spock.lang.Specification

class ElMatcherTest extends Specification {


    def 'User provides SpringEL based matcher in configuration file, and we should support it'() {

        when:
        def matcherFunc = MonitoringPackage.elMatcher(matcher)

        then:
        assert matcherFunc.invoke(input) == result

        where:
        matcher                                     | input                | result
        'answer.contains("I shot a sheriff")'       | 'what?'              | false
        'answer.contains("I shot a sheriff")'       | 'I shot a sheriff'   | true
        'number == 42'                              | 'Answer is 42'       | true
        'number == 42'                              | 'Answer is 40'       | false


    }


}
