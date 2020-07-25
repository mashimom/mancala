package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.domain.Player
import org.shimomoto.mancala.model.entity.Game
import spock.lang.Specification
import spock.lang.Subject

import javax.persistence.EntityNotFoundException

class GameFacadeSpec extends Specification {
	GameService service = Mock(GameService)
	@Subject
	GameFacade facade = new GameFacade(service)

	def "getAll works when empty"() {
		when:
		def result = facade.getAll()

		then:
		result != null
		result.count() == 0
		and:
		service.getAll() >> []
	}

	def "getAll works for 2 entries"() {
		when:
		def result = facade.getAll()

		then:
		result != null
		result.count() == 2
		and: 'interactions'
		1 * service.getAll() >> [Game.builder().build(), Game.builder().build()]
		0 * _
	}

	def "createGame works without names"() {
		when:
		def result = facade.createGame(null, null)

		then:
		result != null
		and: 'interactions'
		1 * service.newGame('Player 1', 'Player 2') >> Game.builder().build()
		0 * _
	}

	def "createGame works with names"() {
		given:
		def p1 = 'p1'
		def p2 = 'p2'

		when:
		def result = facade.createGame(p1, p2)

		then:
		result != null

		and: 'interactions'
		1 * service.newGame('p1', 'p2') >> Game.builder().build()
		0 * _
	}

	def "createRematch works"() {
		given:
		Game g1 = Game.builder().build()
		Game g2 = Game.builder().build()
		when:
		def result = facade.createRematch('someid')

		then:
		result != null
		result == g2
		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(g1)
		1 * service.createRematch(g1) >> g2
		0 * _
	}

	def "createRematch fails"() {
		when:
		def result = facade.createRematch('unknownid')

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * service.getGame('unknownid') >> Optional.empty()
		0 * _
	}

	def "getGameById works"() {
		when:
		def result = facade.getGameById('thatid')

		then:
		result != null
		and: 'interactions'
		1 * service.getGame('thatid') >> Optional.of(Game.builder().build())
		0 * _
	}

	def "getGameById fails"() {
		when:
		facade.getGameById('non-existent')

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * service.getGame('non-existent') >> Optional.empty()
		0 * _
	}

	def "move works"() {
		given:
		Game g = Game.builder().build()
		when:
		def result = facade.move('someid', Player.TWO, 4i)

		then:
		result == g
		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(g)
		1 * service.isFinished(g) >> false
		1 * service.move(g, Player.TWO, 4i)
		0 * _
	}

	def "move fails when game ended"() {
		given:
		Game g = Game.builder().build()
		when:
		facade.move('someid', Player.TWO, 4i)

		then:
		thrown UnsupportedOperationException
		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(g)
		1 * service.isFinished(g) >> true
		0 * _
	}

	def "move fails when id is not found"() {
		when:
		facade.move('missingid', Player.TWO, 4i)

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * service.getGame('missingid') >> Optional.empty()
		0 * _
	}
}
