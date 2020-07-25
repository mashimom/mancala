package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.domain.Player
import org.shimomoto.mancala.model.entity.Game
import org.shimomoto.mancala.model.internal.RawBoard
import org.shimomoto.mancala.repository.GameRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

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

	def "getUpdatedScore works"() {
		given:
		Game g = Mock(Game)

		when:
		def result = service.getUpdatedScore(g)

		then:
		result != null
		result == [(Player.ONE): 2, (Player.TWO): 3]
		and: 'interactions'
		1 * g.getWinner() >> Optional.of(Player.ONE)
		2 * g.winsByPlayer >> [(Player.ONE): 1, (Player.TWO): 3]
		0 * _
	}

	def "getUpdatedScore on unfinished game"() {
		given:
		Game g = Mock(Game)

		when:
		def result = service.getUpdatedScore(g)

		then:
		result != null
		result == [(Player.ONE): 1, (Player.TWO): 3]
		and: 'interactions'
		1 * g.getWinner() >> Optional.empty()
		1 * g.winsByPlayer >> [(Player.ONE): 1, (Player.TWO): 3]
		0 * _
	}

	def "createRematch works"() {
		given:
		def now = LocalDateTime.now()
		def theBoard = RawBoard.builder()
				.currentPlayer(Player.TWO)
				.turnCount(15)
				.board([0, 2, 3, 0, 0, 1, 28, 0, 0, 0, 0, 0, 0, 38] as int[])
				.build()
		Game theGame = Game.builder()
				.id('originalid')
				.board(theBoard)
				.gameStart(LocalDateTime.of(2020, 2, 15, 14, 38))
				.playerNames([(Player.ONE): 'Kirk', (Player.TWO): 'Spock'])
				.winsByPlayer([(Player.ONE): 77, (Player.TWO): 212])
				.build()

		when:
		def result = service.createRematch(theGame)

		then:
		result != null
		with(result) {
			id != theGame.id
			result.@board != theGame.@board
			result.@board == RawBoard.builder().build()
			gameStart != theGame.gameStart
			gameStart > now
			playerNames == theGame.playerNames
			winsByPlayer == [(Player.ONE): 78, (Player.TWO): 212]
		}
	}

	def "createRematch fails"() {
		given:
		def theBoard = RawBoard.builder()
				.currentPlayer(Player.TWO)
				.turnCount(15)
				.board([0, 2, 3, 3, 0, 1, 28, 0, 0, 1, 0, 0, 0, 34] as int[])
				.build()
		Game theGame = Game.builder()
				.id('originalid')
				.board(theBoard)
				.gameStart(LocalDateTime.of(2020, 2, 15, 14, 38))
				.playerNames([(Player.ONE): 'Kirk', (Player.TWO): 'Spock'])
				.winsByPlayer([(Player.ONE): 77, (Player.TWO): 212])
				.build()

		when:
		service.createRematch(theGame)

		then:
		thrown UnsupportedOperationException
	}

	def "move works"() {
		given:
		Game g = Mock(Game)

		when:
		service.move(g, Player.TWO, 3)

		then: 'interactions'
		1 * g.move(Player.TWO, 3)
		0 * _
	}
}
