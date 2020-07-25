package org.shimomoto.mancala.model.domain

import spock.lang.Specification

class PlayerSpec extends Specification {

	def "opponent works"() {
		expect:
		Player.TWO == Player.ONE.opponent()
		and:
		Player.ONE == Player.TWO.opponent()
	}
}
