package com.vasilich.commands.exec

import com.vasilich.os.OsDetails
import jet.Function1
import spock.lang.Specification

class BaseShellCommandExecTest extends Specification {


    def 'during verbose script execution Vasilich should process output partially'() {
        given:
        Function1 processor = Mock()
        def executor = new BaseShellCommandExecutor(processor)

        when:
        String output = executor.exec(new OsDetails().pickProperScript("src/test/resources/scripts/long-run"), 5)

        then:
        1 * processor.invoke('VSLC: one')
        1 * processor.invoke('VSLC: two')
        1 * processor.invoke('VSLC: three')
        assert !output.isEmpty()
    }

}
