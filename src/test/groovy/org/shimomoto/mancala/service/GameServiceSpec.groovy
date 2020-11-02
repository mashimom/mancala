package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.domain.PlayerRole
import org.shimomoto.mancala.model.entity.Board
import org.shimomoto.mancala.model.entity.Game
import org.shimomoto.mancala.model.entity.User
import org.shimomoto.mancala.repository.GameRepository
import spock.lang.Specification
import spock.lang.Subject

class GameServiceSpec extends Specification {
	GameRepository repo = Mock(GameRepository)

	@Subject
	GameService service = new GameService(repo)

	def "getAll with some results"() {
		given:

		def games = [Game.builder().playerOne(Mock(User)).playerTwo(Mock(User)).build(),
		             Game.builder().playerOne(Mock(User)).playerTwo(Mock(User)).build()]

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
		def p1 = Mock(User)
		def p2 = Mock(User)

		when:
		def result = service.newGame(p1, p2)

		then:
		result != null
		with(result) {
			playerOne == p1
			playerTwo == p2
		}
	}

	def "newGame fails"() {
		given:
		User p = Mock(User)
		when:
		service.newGame(null, p)

		then:
		thrown NullPointerException

		when:
		service.newGame(p, null)

		then:
		thrown NullPointerException
	}

	def "createRematch works"() {
		given:
		Game g = Mock(Game)
		User p1 = Mock(User)
		User p2 = Mock(User)
		def gameScore = [(PlayerRole.ONE): 77, (PlayerRole.TWO): 212]

		when:
		def result = service.createRematch(g)

		then:
		result != null
		with(result) {
			board == Board.builder().build()
			!endOfGame
			playerOne == p1
			playerTwo == p2
			winsByPlayer == gameScore
		}
		and: 'interactions'
		1 * g.playerOne >> p1
		1 * g.playerTwo >> p2
		1 * g.winsByPlayer >> [(PlayerRole.ONE): 77, (PlayerRole.TWO): 212]
	}

	def "increaseScore works"() {
		given:
		Game g = Game.builder()
						.playerOne(Mock(User))
						.playerTwo(Mock(User))
						.winsByPlayer([(PlayerRole.ONE): 0, (PlayerRole.TWO): 0])
						.build()

		when:
		service.increaseScore(g, PlayerRole.ONE)

		then:
		g.winsByPlayer == [(PlayerRole.ONE): 1, (PlayerRole.TWO): 0]

		when:
		service.increaseScore(g, PlayerRole.TWO)

		then:
		g.winsByPlayer == [(PlayerRole.ONE): 1, (PlayerRole.TWO): 1]

		when:
		service.increaseScore(g, PlayerRole.TWO)

		then:
		g.winsByPlayer == [(PlayerRole.ONE): 1, (PlayerRole.TWO): 2]

		when:
		service.increaseScore(g, null)

		then:
		g.winsByPlayer == [(PlayerRole.ONE): 2, (PlayerRole.TWO): 3]
	}

	def "setEndOfGame works"() {
		given:
		Game g = Game.builder()
						.playerOne(Mock(User))
						.playerTwo(Mock(User))
						.endOfGame(false)
						.build()

		when:
		service.setEndOfGame(g)

		then:
		g.endOfGame
	}

	def "save works"() {
		given:
		Game g = Mock(Game)

		when:
		service.save(g)

		then:
		repo.save(g) >> g
	}
}
