package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.domain.PlayerRole
import org.shimomoto.mancala.model.entity.Board
import org.shimomoto.mancala.model.entity.Game
import org.shimomoto.mancala.model.entity.User
import spock.lang.Specification
import spock.lang.Subject

import javax.persistence.EntityNotFoundException
import java.util.stream.Collectors

class GameFacadeSpec extends Specification {
	GameService service = Mock(GameService)
	BoardService boardService = Mock(BoardService)
	UserService userService = Mock(UserService)
	@Subject
	GameFacade facade = new GameFacade(service, boardService, userService)

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
		result == g

		and: 'interactions'
		1 * service.newGame(p1, p2) >> g
		0 * _
	}

	def "createRematch works"() {
		given:
		Game g = Mock(Game)
		Game r = Mock(Game)

		when:
		def result = facade.createRematch('someid')

		then:
		result != null
		result == r
		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(g)
		1 * service.isEndOfGame(g) >> true
		1 * service.createRematch(g) >> r
		0 * _
	}

	def "createRematch fails on invalid id"() {
		when:
		facade.createRematch('unknownid')

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		1 * service.getGame('unknownid') >> Optional.empty()
		0 * _
	}

	def "createRematch fails on unfinished game"() {
		given:
		Game g = Mock(Game)

		when:
		facade.createRematch('someid')

		then:
		thrown UnsupportedOperationException
		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(g)
		1 * service.isEndOfGame(g) >> false
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
		1 * service.isEndOfGame(g) >> false
		1 * boardService.isLegalMove(_, PlayerRole.TWO, 4i) >> true
		1 * boardService.move(_, PlayerRole.TWO, 4i)
		1 * boardService.isEndOfGame(_) >> false
		1 * service.save(g)
		0 * _
	}

	def "last move ends the game"() {
		given:
		User p1 = Mock(User)
		User p2 = Mock(User)
		Board board = Board.builder()
						.turnCount(23)
						.currentPlayer(PlayerRole.ONE)
						.pits([0, 0, 0, 0, 0, 7, 21, 3, 2, 3, 13, 0, 0, 15] as int[])
						.build()
		Game game = Game.builder()
						.board(board)
						.playerOne(p1)
						.playerTwo(p2)
						.build()
		when:
		def result = facade.move('someid', PlayerRole.ONE, 5i)

		then:
		result == game

		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(game)
		1 * service.isEndOfGame(game) >> false
		1 * boardService.isLegalMove(board, PlayerRole.ONE, 5i) >> true
		1 * boardService.move(board, PlayerRole.ONE, 5i)
		1 * boardService.isEndOfGame(board) >> true
		1 * boardService.finalizeGame(board)
		1 * boardService.findWinner(board) >> Optional.of(PlayerRole.ONE)
		1 * service.increaseScore(game, PlayerRole.ONE) >> { it }
		1 * service.setEndOfGame(game)
		1 * userService.scoreWin(p1)
		1 * userService.scoreLoose(p2)
		1 * service.save(game)
		0 * _
	}

	def "move fails on illegal move"() {
		given:
		Board b = Mock(Board)
		Game g = Game.builder()
						.playerOne(Mock(User))
						.playerTwo(Mock(User))
						.board(b)
						.build()
		when:
		facade.move('someid', PlayerRole.TWO, 4i)

		then:
		thrown UnsupportedOperationException
		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(g)
		1 * service.isEndOfGame(g) >> false
		1 * boardService.isLegalMove(b, PlayerRole.TWO, 4i) >> false
		0 * _
	}

	def "move fails when game ended"() {
		given:
		Game g = Game.builder()
						.playerOne(Mock(User))
						.playerTwo(Mock(User))
						.build()
		when:
		facade.move('someid', PlayerRole.TWO, 4i)

		then:
		thrown UnsupportedOperationException
		and: 'interactions'
		1 * service.getGame('someid') >> Optional.of(g)
		1 * service.isEndOfGame(g) >> true
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

	def "endOfGameChanges winner is Player 1 works"() {
		given:
		User p1 = User.builder().screenName("Kirk").build()
		User p2 = User.builder().screenName("Bones").build()
		Game g = Game.builder().playerOne(p1).playerTwo(p2).build()

		when:
		//noinspection GroovyAccessibility
		facade.endOfGameChanges(g, Optional.of(PlayerRole.ONE))

		then: 'interactions'
		1 * service.increaseScore(g, PlayerRole.ONE)
		1 * service.setEndOfGame(g)
		1 * userService.scoreWin(p1)
		1 * userService.scoreLoose(p2)
		0 * _
	}

	def "endOfGameChanges winner is Player 2 works"() {
		given:
		User p1 = User.builder().screenName("Kirk").build()
		User p2 = User.builder().screenName("Bones").build()
		Game g = Game.builder().playerOne(p1).playerTwo(p2).build()

		when:
		//noinspection GroovyAccessibility
		facade.endOfGameChanges(g, Optional.of(PlayerRole.TWO))

		then: 'interactions'
		1 * service.increaseScore(g, PlayerRole.TWO)
		1 * service.setEndOfGame(g)
		1 * userService.scoreWin(p2)
		1 * userService.scoreLoose(p1)
		0 * _
	}

	def "endOfGameChanges it is a draw works"() {
		given:
		User p1 = User.builder().screenName("Kirk").build()
		User p2 = User.builder().screenName("Bones").build()
		Game g = Game.builder().playerOne(p1).playerTwo(p2).build()

		when:
		//noinspection GroovyAccessibility
		facade.endOfGameChanges(g, Optional.empty())

		then: 'interactions'
		1 * service.increaseScore(g, null)
		1 * service.setEndOfGame(g)
		1 * userService.scoreDraw(p1)
		1 * userService.scoreDraw(p2)
		0 * _
	}

	def "getAllByUser works"() {
		given:
		UUID id = UUID.fromString('79cde6b5-4539-4fc3-9614-b83bfa092f06')
		String pid = 'ec3mtUU5T8OWFLg7-gkvBg'
		User p1 = Mock(User)
		User p2 = Mock(User)
		def games = (1..2).collect {
			Game.builder()
							.playerOne(p1)
							.playerTwo(p2)
							.build()
		}
		def otherGame = Game.builder()
						.playerOne(Mock(User))
						.playerTwo(Mock(User))
						.build()
		games << otherGame

		when:
		def result = facade.getAllByUser(pid).collect(Collectors.toList())

		then:
		result.size() == 2
		result == (games - otherGame)
		and: 'interactions'
		1 * userService.getPlayer(id) >> Optional.of(p1)
		1 * service.getAll() >> games
		1 * service.isPlayerOn(games[0], p1) >> true
		1 * service.isPlayerOn(games[1], p1) >> true
		1 * service.isPlayerOn(games[2], p1) >> false
		0 * _
	}

	def "getAllByUser fails on invalid id"() {
		when:
		facade.getAllByUser('bogus')

		then:
		thrown EntityNotFoundException
		and: 'interactions'
		0 * _
	}

	def "getAllByUser fails user not found"() {
		given:
		UUID id = UUID.fromString('79cde6b5-4539-4fc3-9614-b83bfa092f06')
		String pid = 'ec3mtUU5T8OWFLg7-gkvBg'

		when:
		facade.getAllByUser(pid)

		then:
		thrown EntityNotFoundException
		and: 'interaction'
		1 * userService.getPlayer(id) >> Optional.empty()
		0 * _
	}
}
