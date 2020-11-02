package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.domain.PlayerRole
import org.shimomoto.mancala.model.entity.Board
import org.shimomoto.mancala.model.entity.Game
import org.shimomoto.mancala.model.entity.User
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
		1 * service.getAll() >> [Game.builder().playerOne(Mock(User)).playerTwo(Mock(User)).build(),
		                         Game.builder().playerOne(Mock(User)).playerTwo(Mock(User)).build()]
		0 * _
	}

	def "createGame works with users"() {
		given:
		def p1 = Mock(User)
		def p2 = Mock(User)
		def g = Game.builder()
						.playerOne(p1)
						.playerTwo(p2)
						.build()

		when:
		def result = facade.createGame(p1, p2)

		then:
		result != null

		and: 'interactions'
		1 * service.newGame(p1, p2) >> g
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
		given:
		Game g = Mock()
		when:
		def result = facade.getGameById('thatid')

		then:
		result == g
		and: 'interactions'
		1 * service.getGame('thatid') >> Optional.of(g)
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
		Game g = Game.builder().playerOne(Mock(User)).playerTwo(Mock(User)).build()
		when:
		def result = facade.move('someid', PlayerRole.TWO, 4i)

		then:
		result == g
		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(g)
		2 * boardService.isEndOfGame(_) >> false
		1 * boardService.isLegalMove(_, PlayerRole.TWO, 4i) >> true
		1 * boardService.move(_, PlayerRole.TWO, 4i)
		1 * service.save(g)
		0 * _
	}

	def "move works at end of game"() {
		given:
		Board board = Board.builder()
						.turnCount(23)
						.currentPlayer(PlayerRole.ONE)
						.pits([0, 0, 0, 0, 0, 7, 21, 3, 2, 3, 13, 0, 0, 15] as int[])
						.build()
		Game game = Game.builder()
						.board(board)
						.playerOne(Mock(User))
						.playerTwo(Mock(User))
						.build()
		when:
		def result = facade.move('someid', PlayerRole.ONE, 5i)

		then:
		result == game

		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(game)
		1 * boardService.isEndOfGame(board) >> false
		1 * boardService.isLegalMove(board, PlayerRole.ONE, 5i) >> true
		1 * boardService.move(board, PlayerRole.ONE, 5i)
		1 * boardService.isEndOfGame(board) >> true
		1 * boardService.endGameMove(board)
		1 * boardService.findWinner(board) >> Optional.of(PlayerRole.ONE)
		1 * service.increaseScore(game, PlayerRole.ONE) >> { it }
		1 * service.setEndOfGame(game)
		1 * service.save(game)
		0 * _
	}

	def "move fails when game ended"() {
		given:
		Game g = Game.builder().playerOne(Mock(User)).playerTwo(Mock(User)).build()
		when:
		facade.move('someid', PlayerRole.TWO, 4i)

		then:
		thrown UnsupportedOperationException
		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(g)
		1 * boardService.isEndOfGame(_) >> true
		0 * _
	}

	def "move fails when id is not found"() {
		when:
		facade.move('missingid', PlayerRole.TWO, 4i)

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * service.getGame('missingid') >> Optional.empty()
		0 * _
	}
}
