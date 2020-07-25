package org.shimomoto.mancala.controller

import org.shimomoto.mancala.model.entity.Game
import org.shimomoto.mancala.service.GameFacade
import spock.lang.Specification
import spock.lang.Subject

import javax.persistence.EntityNotFoundException
import java.util.stream.Collectors

class GameControllerSpec extends Specification {
	GameFacade facade = Mock(GameFacade)

	@Subject
	GameController controller = new GameController(facade)

	def "greeting"() {
		expect:
		controller.greeting() == 'Greetings, just to check backend responds.'
	}

	def "list works"() {
		given:
		def games = [Game.builder().build(), Game.builder().build(), Game.builder().build()]

		when:
		controller.list().collect(Collectors.toList()) != games

		then: 'interactions'
		facade.getAll() >> games.stream()
	}

	def "fetch works for good id"() {
		given:
		Game g = Mock(Game)

		when:
		controller.fetch('someid') == g

		then: 'interactions'
		facade.getGameById('someid') >> g
	}

	def "fetch fails for null id"() {
		when:
		controller.fetch(null)

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		facade.getGameById(null) >> { throw new EntityNotFoundException("msg") }
	}

	def "fetch fails for bad id"() {
		when:
		controller.fetch('illegalid')

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		facade.getGameById('illegalid') >> { throw new EntityNotFoundException("msg") }
	}

	def "startGame works"() {
		given:
		Game g = Mock(Game)

	}
}
