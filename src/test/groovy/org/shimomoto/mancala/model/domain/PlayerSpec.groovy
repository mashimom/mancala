package org.shimomoto.mancala.model.domain

import spock.lang.Specification

class PlayerSpec extends Specification {

	def "opponent works"() {
		expect:
		PlayerRole.TWO == PlayerRole.ONE.opponent()
		and:
		PlayerRole.ONE == PlayerRole.TWO.opponent()
	}
}
