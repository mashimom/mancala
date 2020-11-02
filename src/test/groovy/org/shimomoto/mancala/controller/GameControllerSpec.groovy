package org.shimomoto.mancala.controller

import org.shimomoto.mancala.model.domain.PlayerRole
import org.shimomoto.mancala.model.entity.Game
import org.shimomoto.mancala.service.GameFacade
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import spock.lang.Specification
import spock.lang.Subject

import javax.persistence.EntityNotFoundException
import java.util.stream.Collectors

class GameControllerSpec extends Specification {
	GameFacade facade = Mock(GameFacade)

	@Subject
	GameController controller = new GameController(facade)

	def "greeting"() {
		expect: 'message to be correct'
		controller.greeting() == 'Greetings, just to check backend responds.'
	}

	def "list works"() {
		given: '3 games'
		def games = (1..3).collect { Mock(Game) }

		when: 'listing the stream'
		def result = controller.list().collect(Collectors.toList())

		then: 'result is 3 games'
		result == games
		and: 'interactions'
		1 * facade.getAll() >> games.stream()
		0 * _
	}

	def "fetch works for good id"() {
		given: 'a game mock'
		Game g = Mock(Game)

		when: 'fetching by valid id'
		def result = controller.fetch('someid')

		then:
		result == g
		and: 'interactions'
		1 * facade.getGameById('someid') >> g
		0 * _
	}

	def "fetch fails for null id"() {
		when:
		controller.fetch(null)

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * facade.getGameById(null) >> { throw new EntityNotFoundException("msg") }
		0 * _
	}

	def "fetch fails for bad id"() {
		when:
		controller.fetch('illegalid')

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * facade.getGameById('illegalid') >> { throw new EntityNotFoundException("msg") }
		0 * _
	}

	def "rematch works"() {
		given:
		Game g = Mock(Game)

		when:
		def result = controller.rematch('someid')

		then:
		result == g
		and: 'interactions'
		1 * facade.createRematch('someid') >> g
		0 * _
	}

	def "rematch fails on invalid id - LITTLE value"() {
		when:
		controller.rematch('bogus')

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * facade.createRematch('bogus') >> { throw new EntityNotFoundException("facade thrown") }
		0 * _
	}

	def "move works"() {
		given:
		Game g = Mock(Game)

		when:
		def result = controller.move('someid', PlayerRole.ONE, 1)

		then:
		result != null
		result == g
		and: 'interactions'
		1 * facade.move('someid', PlayerRole.ONE, 1) >> g
		0 * _
	}

	def "move fails for finished game"() {
		when: 'controller is called for a finished game'
		controller.move('someid', PlayerRole.ONE, 1)

		then: ''
		ResponseStatusException e = thrown()
		e.status == HttpStatus.NOT_ACCEPTABLE
		and: 'interactions'
		1 * facade.move('someid', PlayerRole.ONE, 1) >> { throw new UnsupportedOperationException("facade thrown") }
		0 * _
	}
}
