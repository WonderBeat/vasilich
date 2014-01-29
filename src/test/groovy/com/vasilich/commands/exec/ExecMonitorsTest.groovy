package com.vasilich.commands.exec

import com.vasilich.connectors.xmpp.Topics
import spock.lang.Specification

class ExecMonitorsTest extends Specification {

    def '"line match" based process monitor should send notifications if scritp execution output contains markers'() {

        given:
        reactor.core.Observable reactor = Mock()
        def monitor = ExecPackage.createMarkerBasedMonitor(['marker ', 'another'] as String[], reactor, new Topics())

        when:
        monitor.invoke(input)

        then:
        1 * reactor.notify(_, { it.data == notification })

        where:
        input | notification
        'marker one' | 'one'
        'some string with marker in the middle' | 'in the middle'
        'let\'s test linebreaks. \nmarker monitor should know about them' | 'monitor should know about them'
        'and another thing. Linebreaks can be different. Win \r\n and Lin \n' | 'thing. Linebreaks can be different. Win'
        'Multiple lines\r\nmarker this\n also supported' | 'this'
    }
}
