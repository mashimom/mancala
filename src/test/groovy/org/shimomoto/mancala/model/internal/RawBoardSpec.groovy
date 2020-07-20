package org.shimomoto.mancala.model.internal

import org.shimomoto.mancala.model.domain.Player
import spock.lang.Specification

class RawBoardSpec extends Specification {

	def "isLegalMove accepts"() {
		given:
		RawBoard board = RawBoard.builder().currentPlayer(p).board(b).build()

		expect:
		board.isLegalMove(p, i)

		where:
		//                0  1  2  3  4  5  6  7  8  9  0  1  2  3
		_  | p          | b                                                   | i
		0  | Player.ONE | [1, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 0
		1  | Player.ONE | [0, 1, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 1
		2  | Player.ONE | [0, 0, 2, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 2
		3  | Player.ONE | [0, 0, 0, 3, 0, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 3
		4  | Player.ONE | [0, 0, 0, 0, 4, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 4
		5  | Player.ONE | [0, 0, 0, 0, 0, 5, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 5
		6  | Player.ONE | [9, 0, 0, 0, 0, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 0
		7  | Player.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 0
		8  | Player.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 1
		9  | Player.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 2
		10 | Player.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 3
		11 | Player.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 4
		12 | Player.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 5
	}

	def "isLegalMove rejects"() {
		given:
		RawBoard board = RawBoard.builder().currentPlayer(p).board(b).build()

		expect:
		!board.isLegalMove(p, i)

		where:
		//                0  1  2  3  4  5  6  7  8  9  0  1  2  3
		_  | p          | b                                                   | i
		0  | Player.ONE | [0, 1, 1, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 0
		1  | Player.ONE | [1, 0, 1, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 1
		2  | Player.ONE | [1, 1, 0, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 2
		3  | Player.ONE | [1, 1, 1, 0, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 3
		4  | Player.ONE | [1, 1, 1, 1, 0, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 4
		5  | Player.ONE | [1, 1, 1, 1, 1, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 5
		6  | Player.ONE | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | -1
		7  | Player.ONE | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 7
		8  | Player.TWO | [0, 1, 1, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 0
		9  | Player.TWO | [1, 0, 1, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 1
		10 | Player.TWO | [1, 1, 0, 1, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 2
		11 | Player.TWO | [1, 1, 1, 0, 1, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 3
		12 | Player.TWO | [1, 1, 1, 1, 0, 1, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 4
		13 | Player.TWO | [1, 1, 1, 1, 1, 0, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 5
		14 | Player.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | -1
		15 | Player.TWO | [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[] | 7
	}

	def "changeTurn swaps player and board position and back"() {
		given:
		RawBoard b = RawBoard.builder()
				.currentPlayer(Player.ONE)
				.board((0..13).collect() as int[])
				.build()

		when:
		b.changeTurn()

		then:
		b.currentPlayer == Player.TWO
		b.board == [7, 8, 9, 10, 11, 12, 13, 0, 1, 2, 3, 4, 5, 6] as int[]

		when:
		b.changeTurn()

		then:
		b.currentPlayer == Player.ONE
		b.board == (0..13).collect() as int[]
	}

	def "game move"() {
		given: "starting board"
		RawBoard b = RawBoard.builder().build()

		when: "@1 - player 1 makes a valid move"
		b.move(Player.ONE, 0)

		then: "@1 - the board changes and player 1 has extra turn"
		b.currentPlayer == Player.ONE
		b.board == [0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0] as int[]

		when: "@2 - player 2 tries a move out of turn"
		b.move(Player.TWO, 0)

		then: "@2 - nothing changes"
		b.currentPlayer == Player.ONE
		b.board == [0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0] as int[]

		when: "@3 - player 1 makes a valid move"
		b.move(Player.ONE, 1)

		then: "@3 - board changes and player 2 takes turn"
		b.currentPlayer == Player.TWO
		b.board == [7, 7, 6, 6, 6, 6, 0, 0, 0, 8, 8, 8, 8, 2] as int[]

		when: "@4 - player 2 makes invalid move"
		b.move(Player.TWO, 7)

		then: "@4 - nothing changes"
		b.currentPlayer == Player.TWO
		b.board == [7, 7, 6, 6, 6, 6, 0, 0, 0, 8, 8, 8, 8, 2] as int[]

		when: "@5 - player 2 makes a valid move"
		b.move(Player.TWO, 1)

		then: "@5 - board changes and player 1 takes turn"
		b.currentPlayer == Player.ONE
		b.board == [1, 1, 8, 8, 8, 8, 2, 7, 0, 7, 7, 7, 7, 1] as int[]

		when: "@6 - player 1 makes a valid move"
		b.move(Player.ONE, 1)

		then: "@6 - board changes and player 2 takes turn"
		b.currentPlayer == Player.TWO
		b.board == [7, 0, 7, 7, 7, 7, 1, 1, 0, 9, 8, 8, 8, 2] as int[]

		when: "@7 - player 2 makes a valid move"
		b.move(Player.TWO, 5)

		then: "@7 - board changes and player 1 takes turn"
		b.currentPlayer == Player.ONE
		b.board == [2, 1, 10, 9, 9, 9, 2, 7, 0, 7, 7, 7, 0, 2] as int[]

		when: "@8 - player 1 makes a valid move"
		b.move(Player.ONE, 2)

		then: "@8 - board changes and player 2 takes turn"
		b.currentPlayer == Player.TWO
		b.board == [8, 1, 8, 8, 8, 1, 2, 2, 1, 0, 10, 10, 10, 3] as int[]

		when: "@9 - player 2 makes a valid move"
		b.move(Player.TWO, 5)

		then: "@9 - board changes and player 2 has extra turn"
		b.currentPlayer == Player.TWO
		b.board == [8, 1, 8, 8, 8, 0, 3, 2, 1, 0, 10, 10, 10, 3] as int[]
	}
}
