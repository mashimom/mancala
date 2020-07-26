package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.domain.Player
import org.shimomoto.mancala.model.entity.Board
import org.shimomoto.mancala.model.entity.Game
import spock.lang.Specification
import spock.lang.Subject

import javax.persistence.EntityNotFoundException

class GameFacadeSpec extends Specification {
	GameService service = Mock(GameService)
	BoardService boardService = Mock(BoardService)
	@Subject
	GameFacade facade = new GameFacade(service, boardService)

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
		given:
		def g = Game.builder().build()

		when:
		def result = facade.createGame(null, null)

		then:
		result != null
		and: 'interactions'

		1 * service.newGame('Player 1', 'Player 2') >> g
		1 * service.save(g)
		0 * _
	}

	def "createGame works with names"() {
		given:
		def g = Game.builder().build()
		def p1 = 'p1'
		def p2 = 'p2'

		when:
		def result = facade.createGame(p1, p2)

		then:
		result != null

		and: 'interactions'
		1 * service.newGame('p1', 'p2') >> g
		1 * service.save(g)
		0 * _
	}

	def "createRematch works"() {
		given:
		Game g = Mock(Game)
		Board b = Mock(Board)
		Game r = Mock(Game)

		when:
		def result = facade.createRematch('someid')

		then:
		result != null
		result == r
		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(g)
		1 * g.board >> b
		1 * boardService.isEndOfGame(b) >> true
		1 * service.createRematch(g) >> r
		1 * service.save(r)
		0 * _
	}

	def "createRematch fails"() {
		when:
		facade.createRematch('unknownid')

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
		2 * boardService.isEndOfGame(_) >> false
		1 * boardService.move(_, Player.TWO, 4i)
		1 * service.save(g)
		0 * _
	}

	def "move works at end of game"() {
		given:
		Board board = Board.builder()
				.turnCount(23)
				.currentPlayer(Player.ONE)
				.pits([0, 0, 0, 0, 0, 7, 21, 3, 2, 3, 13, 0, 0, 15] as int[])
				.build()
		Game game = Game.builder()
				.board(board)
				.build()
		when:
		def result = facade.move('someid', Player.ONE, 5i)

		then:
		result == game

		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(game)
		1 * boardService.isEndOfGame(board) >> false
		1 * boardService.move(board, Player.ONE, 5i)
		1 * boardService.isEndOfGame(board) >> true
		1 * boardService.endGameMove(board)
		1 * boardService.findWinner(board) >> Optional.of(Player.ONE)
		1 * service.increaseScore(game, Player.ONE) >> { it }
		1 * service.setEndOfGame(game)
		1 * service.save(game)
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
		1 * boardService.isEndOfGame(_) >> true
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
