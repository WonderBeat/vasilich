package com.vasilich.commands.bootstrap
import com.vasilich.commands.api.Command
import spock.lang.Specification

class CommandWrappersTest extends Specification {

    def cfg(Map map, enabled = false, aliases = [], outputFormatter = '' ) {
        new CommandCfg(map.enabled ?: enabled , (map.aliases ?: aliases) as String[],
                map.outputFormatter ?: outputFormatter)
    }

    def 'wrappers should prevent command execution if conditionals match'() {

        given:
        Command cmd = Mock()
        cmd.execute(_) >> 'success'

        when:
        def wrappedCmd = wrapper.invoke(cmd, config)
        def output = wrappedCmd.execute('useless input and alias') ?: 'no answer'

        then:
        assert output == result, "Please check ${wrapper} with properties ${config.properties}"

        where:
        wrapper | config | result
        BootstrapPackage.enableThumblerCommandWrapper() | cfg(enabled: false) | 'no answer'
        BootstrapPackage.enableThumblerCommandWrapper() | cfg(enabled: true) | 'success'
        BootstrapPackage.aliasMatchCommandDetection() | cfg(aliases: ['what']) | 'no answer'
        BootstrapPackage.aliasMatchCommandDetection() | cfg(aliases: ['alias']) | 'success'
        BootstrapPackage.outputMessageWrapper() | cfg(outputFormatter: '') | 'success'
        BootstrapPackage.outputMessageWrapper() | cfg(outputFormatter: 'abc {0}') | 'abc success'
    }

}
