package com.vasilich.commands.bootstrap

import com.vasilich.commands.api.Command
import org.springframework.core.Ordered
import spock.lang.Specification

interface OrderedCommand extends Command, Ordered {
}

class ReactiveCommandInitializerTest extends Specification {

    def 'commands can be prioritise and the first one, that returns smth, should be used as success'() {
        given:
        OrderedCommand orderedCmd = Mock()
        orderedCmd.getOrder() >> order
        orderedCmd.execute(_) >> "ordered wins"
        Command cmd = Mock()
        cmd.execute(_) >> "unordered wins"

        when:
        def chained = BootstrapPackage.chainCommandsByOrder([orderedCmd, cmd], 50)
        def output = chained.execute("useless input")

        then:
        assert output == result

        where:
        order | result
        0   | "ordered wins"
        100 | "unordered wins"

    }

}
