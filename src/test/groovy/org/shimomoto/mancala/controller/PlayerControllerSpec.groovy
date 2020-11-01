package org.shimomoto.mancala.controller


import org.shimomoto.mancala.model.transfer.UserDto
import org.shimomoto.mancala.service.UserFacade
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
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

	def "player goes to wait room"() {
		when:
		controller.waitRoom(pid)

		then:
		notThrown ResponseStatusException
		and: 'interactions'
		1 * facade.waitRoom(pid) >> true
		0 * _
	}

	def "nonexistent player cannot go to wait room"() {
		when:
		controller.waitRoom("bogus")

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * facade.waitRoom(_) >> { throw new EntityNotFoundException("some message") }
		0 * _
	}

	def "player cannot go to wait room twice"() {
		when:
		controller.waitRoom(pid)

		then:
		def ex = thrown(ResponseStatusException)
		ex.status == HttpStatus.NOT_ACCEPTABLE
		1 * facade.waitRoom(pid) >> false
		0 * _
	}
}
