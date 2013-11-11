package com.vasilich.commands.basic.exec

import com.vasilich.os.OsDetails
import jet.Function1
import spock.lang.Specification

class VerboseExecTest extends Specification {


    def 'during verbose script execution Vasilich should process output partyally'() {
        given:
        Function1 processor = Mock()
        def executor = new VerboseShellCommandExecutor(new VerboseExecuteCfg(['VSLC'] as String[]), processor)

        when:
        String output = executor.exec(new OsDetails().pickProperScript("src/test/resources/scripts/long-run"), 5)

        then:
        1 * processor.invoke('VSLC: one')
        1 * processor.invoke('VSLC: two')
        1 * processor.invoke('VSLC: three')
        assert !output.isEmpty()
    }

}
