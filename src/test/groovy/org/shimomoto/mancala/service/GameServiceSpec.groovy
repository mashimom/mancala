package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.domain.Player
import org.shimomoto.mancala.model.entity.Board
import org.shimomoto.mancala.model.entity.Game
import org.shimomoto.mancala.repository.GameRepository
import spock.lang.Specification
import spock.lang.Subject

class GameServiceSpec extends Specification {
	GameRepository repo = Mock(GameRepository)

	@Subject
	GameService service = new GameService(repo)

	def "getAll with some results"() {
		given:
		def games = [Game.builder().build(), Game.builder().build()]

		when:
		def result = service.getAll()

		then:
		result == games
		and: 'interactions'
		1 * repo.findAll() >> games
		0 * _
	}

	def "getAll empty results"() {
		when:
		def result = service.getAll()

		then:
		result == []
		and: 'interactions'
		1 * repo.findAll() >> []
		0 * _
	}

	def "getGame works"() {
		given:
		Game g = Mock(Game)

		when:
		def result = service.getGame('someid')

		then:
		result != null
		result.get() == g
		and: 'interactions'
		1 * repo.findById('someid') >> Optional.of(g)
		0 * _
	}

	def "getGame cannot find"() {
		when:
		def result = service.getGame('invalidid')

		then:
		result != null
		result.isEmpty()
		and: 'interactions'
		1 * repo.findById('invalidid') >> Optional.empty()
		0 * _
	}

	def "getGame cannot empty id"() {
		when:
		def result = service.getGame('')

		then:
		result != null
		result.isEmpty()
		and: 'interactions'
		0 * repo.findById(_)
		0 * _
	}

	def "newGame works"() {
		given:
		def p1 = 'player1'
		def p2 = 'player2'

		when:
		def result = service.newGame(p1, p2)

		then:
		result != null
		with(result) {
			playerNames == [(Player.ONE): p1, (Player.TWO): p2]
		}
	}

	def "newGame fails"() {
		when:
		service.newGame(null, 'somename')

		then:
		thrown NullPointerException

		when:
		service.newGame('someothername', null)

		then:
		thrown NullPointerException
	}

	def "createRematch works"() {
		given:
		Game g = Mock(Game)
		def playerNames = [(Player.ONE): 'Kirk', (Player.TWO): 'Spock']
		def gameScore = [(Player.ONE): 77, (Player.TWO): 212]

		when:
		def result = service.createRematch(g)

		then:
		result != null
		with(result) {
			board == Board.builder().build()
			!endOfGame
			playerNames == playerNames
			winsByPlayer == gameScore
		}
		and: 'interactions'
		1 * g.playerNames >> playerNames
		1 * g.winsByPlayer >> [(Player.ONE): 77, (Player.TWO): 212]
	}
}
