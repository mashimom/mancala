package org.shimomoto.mancala


import spock.lang.Specification

class MechanicsSpec extends Specification {

	//KISS

	def "first move mechanics"() {
		given:
		int position = 0
		//             0  1  2  3  4  5  6  7  8  9  0  1  2  3
		int[] board = [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0] as int[]
		int seeds = board[position]
		int rounds = seeds.intdiv 14
		int extra = seeds % 14
		def endsAt = extra + position

		when:
		board[position] = 0;
		for (int i = 0; i < board.length; i++) {
			board[i] += rounds
			if (extra > 0 && i > position) {
				board[i]++
				extra--
			}
		}
		def steal = (0..5).contains(endsAt) && board[endsAt] == 1
		def extraTurn = endsAt == 6

		then:
		//                             6  5  4  3  2  1  0
		//        1  2  3  4  5  6  7  8  9  0  1  2  3  4
		board == [0, 7, 7, 7, 7, 7, 1, 6, 6, 6, 6, 6, 6, 0] as int[]
		!steal
		extraTurn
	}

	def "big move mechanics"() {
		given:
		int position = 2
		//             1  2   3  4  5  6   7  8  9  0  1  2  3   4
		int[] board = [0, 0, 50, 0, 0, 0, 11, 0, 0, 0, 0, 0, 0, 11] as int[]
		int seeds = board[position]
		int rounds = seeds.intdiv 14
		int extra = seeds % 14
		def endsAt = extra + position

		when:
		board[position] = 0;
		for (int i = 0; i < board.length; i++) {
			board[i] += rounds
			if (extra > 0 && i > position) {
				board[i]++
				extra--
			}
		}
		def steal = (0..5).contains(endsAt) && board[endsAt] == 1
		def extraTurn = endsAt == 6

		then:
		//        0  1  2  3  4  5   6  7  8  9  0  1  2   3
		board == [3, 3, 3, 4, 4, 4, 15, 4, 4, 4, 4, 3, 3, 14] as int[]
		endsAt == 10
		!steal
		!extraTurn
	}

	def "steal mechanics"() {
		given:
		int position = 5
		//             0  1  2  3  4   5  6  7  8  9  0  1  2  3
		int[] board = [6, 2, 2, 6, 6, 14, 0, 6, 6, 6, 6, 6, 6, 0] as int[]
		int seeds = board[position]
		int rounds = seeds.intdiv 14
		int extra = seeds % 14
		def endsAt = extra + position

		when:
		board[position] = 0;
		for (int i = 0; i < board.length; i++) {
			board[i] += rounds
			if (extra > 0 && i > position) {
				board[i]++
				extra--
			}
		}
		def steal = (0..5).contains(endsAt) && board[endsAt] == 1
		def extraTurn = endsAt == 6
		if(steal) {
			int opposingPos = 12-endsAt
			board[6] += (board[opposingPos] + board[endsAt])
			board[opposingPos] = 0
			board[endsAt] = 0
		}

		then:
		//                             6  5  4  3  2  1  0
		//        0  1  2  3  4  5  6  7  8  9  0  1  2  3
		board == [7, 3, 3, 7, 7, 0, 9, 0, 7, 7, 7, 7, 7, 1] as int[]
		endsAt == 5
		steal
		!extraTurn
	}
}
