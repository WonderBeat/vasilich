package com.vasilich.commands.basic.exec

import jet.Function1
import spock.lang.Specification

class VerboseExecTest extends Specification {


    def 'during verbose script execution Vasilich should process output partyally'() {
        given:
        Function1 processor = Mock()
        def commandResolver = new ShellCommandResolver(new ShellProviderCfg())
        def cmd = commandResolver.cmdForName('src/test/resources/scripts/long-run')
        def executor = new VerboseShellCommandExecutor(new VerboseExecuteCfg(['VSLC'] as String[], 500), processor)

        when:
        executor.exec(cmd, 5)

        then:
        3 * processor.invoke(_)


    }


}
