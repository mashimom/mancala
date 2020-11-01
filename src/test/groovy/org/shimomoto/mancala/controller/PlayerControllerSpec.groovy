package org.shimomoto.mancala.controller


import org.shimomoto.mancala.model.transfer.UserDto
import org.shimomoto.mancala.service.UserFacade
import spock.lang.Specification
import spock.lang.Subject

import javax.persistence.EntityNotFoundException

class PlayerControllerSpec extends Specification {

	UserFacade facade = Mock(UserFacade)

	@Subject
	PlayerController controller = new PlayerController(facade)

	UUID id = UUID.fromString('3b90eec8-21cf-4d38-8de7-468c470c43b0')
	String pid = 'O5DuyCHPTTiN50aMRwxDsA'

	def "fetching existing player works"() {
		given:
		UserDto u = Mock(UserDto)
		when:
		def result = controller.fetch(pid)
		println id
		println pid

		then:
		result == u
		and: 'interactions'
		1 * facade.getPlayer(pid) >> Optional.of(u)
		0 * _
	}

	def "fetching non-existing player fails"() {
		when:
		controller.fetch("bogus")

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * facade.getPlayer(_) >> Optional.empty()
		0 * _
	}
}
