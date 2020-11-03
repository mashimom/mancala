package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.domain.PlayerRole
import org.shimomoto.mancala.model.entity.Board
import spock.lang.Specification

class BoardServiceSpec extends Specification {

	BoardService service = new BoardService()

	def "isCapture accepts"() {
		given:
		def pits = [1, 4, 5, 1, 7, 8, 9, 2, 0, 0, 5, 0, 0, 30] as int[]

		expect:
		BoardService.isCapture(0, pits)
		BoardService.isCapture(3, pits)
	}

	def "isCapture rejects"() {
		given:
		def pits = [1, 4, 5, 6, 7, 8, 9, 2, 0, 0, 0, 0, 0, 30] as int[]

		expect:
		!BoardService.isCapture(-1, pits)
		!BoardService.isCapture(7, pits)
		!BoardService.isCapture(1, pits)
		!BoardService.isCapture(5, pits)
	}

	def "newBoard works"() {
		when:
		def result = service.newBoard()

		then:
		result != null
		with(result) {
			turnCount == 1
			currentPlayer == PlayerRole.ONE
			pits == [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[]
		}
	}

	def "isLegalMove accepts"() {
		given:
		Board board = Board.builder().currentPlayer(p).pits(b).build()

		expect:
		service.isLegalMove(board, p, i)

		where:
		//                0  1  2  3  4  5  6  7  8  9  0  1  2  3
		_  | p              | b                                                   | i
		0  | PlayerRole.ONE | [1, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 0
		1  | PlayerRole.ONE | [0, 1, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 1
		2  | PlayerRole.ONE | [0, 0, 2, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 2
		3  | PlayerRole.ONE | [0, 0, 0, 3, 0, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 3
		4  | PlayerRole.ONE | [0, 0, 0, 0, 4, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 4
		5  | PlayerRole.ONE | [0, 0, 0, 0, 0, 5, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 5
		6  | PlayerRole.ONE | [9, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 0
		7  | PlayerRole.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 0
		8  | PlayerRole.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 1
		9  | PlayerRole.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 2
		10 | PlayerRole.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 3
		11 | PlayerRole.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 4
		12 | PlayerRole.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 5
	}

	def "isLegalMove rejects"() {
		given:
		Board board = Board.builder().currentPlayer(p).pits(b).build()

		expect:
		!service.isLegalMove(board, p, i)

		where:
		//                0  1  2  3  4  5  6  7  8  9  0  1  2  3
		_  | p              | b                                                   | i
		0  | PlayerRole.ONE | [0, 1, 1, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 0
		1  | PlayerRole.ONE | [1, 0, 1, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 1
		2  | PlayerRole.ONE | [1, 1, 0, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 2
		3  | PlayerRole.ONE | [1, 1, 1, 0, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 3
		4  | PlayerRole.ONE | [1, 1, 1, 1, 0, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 4
		5  | PlayerRole.ONE | [1, 1, 1, 1, 1, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 5
		6  | PlayerRole.ONE | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | -1
		7  | PlayerRole.ONE | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 7
		8  | PlayerRole.TWO | [0, 1, 1, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 0
		9  | PlayerRole.TWO | [1, 0, 1, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 1
		10 | PlayerRole.TWO | [1, 1, 0, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 2
		11 | PlayerRole.TWO | [1, 1, 1, 0, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 3
		12 | PlayerRole.TWO | [1, 1, 1, 1, 0, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 4
		13 | PlayerRole.TWO | [1, 1, 1, 1, 1, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 5
		14 | PlayerRole.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | -1
		15 | PlayerRole.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 7
	}

	def "changeTurn swaps player and board position and back"() {
		given:
		Board b = Board.builder()
						.currentPlayer(PlayerRole.ONE)
				.pits((0..13).collect() as int[])
				.build()

		when:
		service.changeTurn(b)

		then:
		b.currentPlayer == PlayerRole.TWO
		b.pits == [7, 8, 9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6] as int[]

		when:
		service.changeTurn(b)

		then:
		b.currentPlayer == PlayerRole.ONE
		b.pits == (0..13).collect() as int[]
	}

	def "move works"() {
		given:
		Board board = Board.builder()
						.currentPlayer(PlayerRole.TWO)
				.build()

		when:
		service.move(board, PlayerRole.TWO, 3)

		then:
		with(board) {
			turnCount == 2
			currentPlayer == PlayerRole.ONE
			board.getPits() == [7, 7, 7, 6, 6, 6, 0, 6, 6, 6, 0, 7, 7, 1] as int[]
		}
	}

	def "game move"() {
		given: "starting board"
		Board b = Board.builder().build()

		when: "@1 - player 1 makes a valid extra turn move"
		service.move(b, PlayerRole.ONE, 0)

		then: "@1 - the board changes and player 1 has extra turn"
		b.currentPlayer == PlayerRole.ONE
		b.pits == [0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0] as int[]
		b.turnCount == 2

		when: "@2 - player 2 tries a move out of turn"
		service.move(b, PlayerRole.TWO, 0)

		then: "@2 - nothing changes"
		b.currentPlayer == PlayerRole.ONE
		b.pits == [0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0] as int[]
		b.turnCount == 2

		when: "@3 - player 1 makes a valid move"
		service.move(b, PlayerRole.ONE, 1)

		then: "@3 - board changes and player 2 takes turn"
		b.currentPlayer == PlayerRole.TWO
		//          0  1  2  3  4  5  6  7  8  9  0  1  2  3
		b.pits == [7, 7, 6, 6, 6, 6, 0, 0, 0, 8, 8, 8, 8, 2] as int[]
		b.turnCount == 3

		when: "@4 - player 2 makes invalid move"
		service.move(b, PlayerRole.TWO, 7)

		then: "@4 - nothing changes"
		b.currentPlayer == PlayerRole.TWO
		b.pits == [7, 7, 6, 6, 6, 6, 0, 0, 0, 8, 8, 8, 8, 2] as int[]
		b.turnCount == 3

		when: "@5 - player 2 makes a valid move"
		service.move(b, PlayerRole.TWO, 1)

		then: "@5 - board changes and player 1 takes turn"
		b.currentPlayer == PlayerRole.ONE
		b.pits == [1, 1, 8, 8, 8, 8, 2, 7, 0, 7, 7, 7, 7, 1] as int[]
		b.turnCount == 4

		when: "@6 - player 1 makes a valid move"
		service.move(b, PlayerRole.ONE, 1)

		then: "@6 - board changes and player 2 takes turn"
		b.currentPlayer == PlayerRole.TWO
		b.pits == [7, 0, 7, 7, 7, 7, 1, 1, 0, 9, 8, 8, 8, 2] as int[]
		b.turnCount == 5

		when: "@7 - player 2 makes a valid move"
		service.move(b, PlayerRole.TWO, 5)

		then: "@7 - board changes and player 1 takes turn"
		b.currentPlayer == PlayerRole.ONE
		b.pits == [2, 1, 10, 9, 9, 9, 2, 7, 0, 7, 7, 7, 0, 2] as int[]
		b.turnCount == 6

		when: "@8 - player 1 makes a valid move"
		service.move(b, PlayerRole.ONE, 2)

		then: "@8 - board changes and player 2 takes turn"
		b.currentPlayer == PlayerRole.TWO
		b.pits == [8, 1, 8, 8, 8, 1, 2, 2, 1, 0, 10, 10, 10, 3] as int[]
		b.turnCount == 7

		when: "@9 - player 2 makes a valid extra turn move"
		service.move(b, PlayerRole.TWO, 5)

		then: "@9 - board changes and player 2 has extra turn"
		b.currentPlayer == PlayerRole.TWO
		b.pits == [8, 1, 8, 8, 8, 0, 3, 2, 1, 0, 10, 10, 10, 3] as int[]
		b.turnCount == 8

		when: "@10 - player 2 makes a valid move"
		service.move(b, PlayerRole.TWO, 2)

		then: "@10 - board changes and player 1 takes turn"
		b.currentPlayer == PlayerRole.ONE
		//			0  1  2  3  4  5  6  7  8  9   0   1   2  3
		b.pits == [3, 2, 1, 11, 10, 10, 3, 8, 1, 0, 9, 9, 1, 4] as int[]
		b.turnCount == 9

		when: "@11 - player 1 makes a valid move"
		service.move(b, PlayerRole.ONE, 0)

		then: "@11 - board changes and player 2 takes turn"
		b.currentPlayer == PlayerRole.TWO
		//          0  1  2  3  4  5  6  7  8  9   0   1   2  3
		b.pits == [8, 1, 0, 9, 9, 1, 4, 0, 3, 2, 12, 10, 10, 3] as int[]
		b.turnCount == 10

		when: "@12 - player 1 makes a capture move"
		service.move(b, PlayerRole.TWO, 1)

		then: "@12 - capture happens, board changes and player 1 takes turn"
		b.currentPlayer == PlayerRole.ONE
		//          0  1  2  3   4   5  6  7  8  9  0  1  2  3
		b.pits == [0, 3, 2, 0, 10, 10, 3, 8, 0, 0, 9, 9, 1, 17] as int[]
		b.turnCount == 11
	}

	def "Has the game ended accepts"() {
		given:
		Board board1 = Board.builder()
				.pits([0, 0, 0, 0, 0, 0, 30, 1, 2, 3, 4, 5, 6, 7] as int[])
				.build()
		Board board2 = Board.builder()
				.pits([2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0, 0, 0, 42] as int[])
				.build()

		expect:
		service.isEndOfGame(board1)
		service.isEndOfGame(board2)
	}

	def "Has the game ended rejects"() {
		given:
		Board board1 = Board.builder().build()
		Board board2 = Board.builder()
				.pits([0, 3, 2, 0, 10, 10, 3, 8, 0, 0, 9, 9, 1, 17] as int[])
				.build()

		expect:
		!service.isEndOfGame(board1)
		!service.isEndOfGame(board2)
	}

	def "endGameMove"() {
		given:
		Board board = Board.builder()
				.turnCount(30)
				.pits([0, 0, 0, 0, 0, 0, 30, 3, 4, 5, 6, 7, 8, 9] as int[])
				.build()

		when:
		service.finalizeGame(board)

		then:
		board != null
		with(board) {
			turnCount == 30
			currentPlayer == PlayerRole.ONE
			pits == [0, 0, 0, 0, 0, 0, 30, 0, 0, 0, 0, 0, 0, 42] as int[]
		}
	}

	def "getMatchScore works"() {
		given:
		Board b1 = Board.builder()
						.currentPlayer(PlayerRole.TWO)
				.pits((1..14).collect() as int[])
				.build()
		Board b2 = Board.builder()
						.currentPlayer(PlayerRole.ONE)
		//     0  1  2  3  4   5  6  7  8  9  0  1  2   3
						.pits([1, 3, 2, 1, 8, 10, 3, 8, 3, 2, 9, 9, 1, 12] as int[])
						.build()

		when:
		def result1 = service.getMatchScore(b1)

		then:
		result1 != null
		result1[PlayerRole.ONE] == 14
		result1[PlayerRole.TWO] == 7

		when:
		def result2 = service.getMatchScore(b2)

		then:
		result2 != null
		result2[PlayerRole.ONE] == 3
		result2[PlayerRole.TWO] == 12
	}

	def "findWinner works when game has ended"() {
		given:
		Board board = Board.builder()
						.currentPlayer(PlayerRole.TWO)
						.pits([0, 0, 0, 0, 0, 0, 30, 1, 2, 3, 4, 5, 6, 7] as int[])
						.build()

		when:
		def result = service.findWinner(board)

		then:
		result.isPresent()
		result.get() == PlayerRole.TWO
	}

	def "findWinner is empty there is a draw"() {
		given:
		Board board = Board.builder()
						.currentPlayer(PlayerRole.TWO)
				.build()

		when:
		def result = service.findWinner(board)

		then:
		result.isEmpty()
	}
}
