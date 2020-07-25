package org.shimomoto.mancala.service

import org.shimomoto.mancala.model.domain.Player
import org.shimomoto.mancala.model.entity.Board
import spock.lang.Specification

class BoardServiceSpec extends Specification {

	BoardService service = new BoardService()

	//TODO: adapt all rawboard tests
	def "move works"() {
		given:
		Board board = Board.builder()
				.currentPlayer(Player.TWO)
				.build()

		when:
		service.move(board, Player.TWO, 3)

		then:
		with(board) {
			turnCount == 2
			currentPlayer == Player.ONE
			board.getPits() == [7, 7, 7, 6, 6, 6, 0, 6, 6, 6, 0, 7, 7, 1] as int[]
		}
	}
}
