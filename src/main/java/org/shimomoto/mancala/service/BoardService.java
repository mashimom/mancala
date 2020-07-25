package org.shimomoto.mancala.service;

import com.codepoetics.protonpack.maps.MapStream;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.shimomoto.mancala.model.domain.Player;
import org.shimomoto.mancala.model.entity.Board;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class BoardService {

	private static boolean isPassingTurn(final int endsAt) {
		return endsAt != 6;
	}

	private static boolean isCapture(final int endsAt, final int[] board) {
		return endsAt >= 0 && endsAt < 6 && board[endsAt] == 1;
	}

	public Board newBoard() {
		return Board.builder().build();
	}

	public boolean isLegalMove(final Board board, final Player player, final int position) {
		if (board.getCurrentPlayer() == player) {
			return position >= 0 && position < 6 && board.getPits()[position] != 0;
		}
		return false;
	}

	public void changeTurn(final Board board) {
		board.setCurrentPlayer(board.getCurrentPlayer().opponent());
		final int[] copy = Arrays.copyOf(board.getPits(), board.getPits().length);
		for (int i = 0; i <= 6; i++) {
			board.getPits()[i] = copy[i + 7];
			board.getPits()[i + 7] = copy[i];
		}
	}

	public void move(final Board board, final Player player, final int position) {
		if (!isLegalMove(board, player, position)) {
			return;
		}

		//calculates intermediate values
		final int rounds = board.getPits()[position] / 14;
		final int spare = board.getPits()[position] % 14;
		final int endsAt = spare + position;

		//makes changes
		sow(board, position, rounds, spare);

		//will capture if sowing ends on own pits and at a count 1
		if (isCapture(endsAt, board.getPits())) {
			capture(board, endsAt);
		}

		if (isPassingTurn(endsAt)) {
			changeTurn(board);
		}

		board.setTurnCount(board.getTurnCount() + 1);
	}

	private void capture(final Board board, final int captureFrom) {
		final int opposingPosition = 12 - captureFrom;
		board.getPits()[6] += (board.getPits()[opposingPosition] + board.getPits()[captureFrom]);
		board.getPits()[opposingPosition] = 0;
		board.getPits()[captureFrom] = 0;
	}

	private void sow(final Board board, final int position, final int rounds, int spare) {
		board.getPits()[position] = 0;
		for (int i = 0; i < board.getPits().length; i++) {
			board.getPits()[i] += rounds;
			if (spare > 0 && i > position) {
				board.getPits()[i]++;
				spare--;
			}
		}
	}

	public boolean isEndOfGame(final Board board) {
		final boolean isFirstRowCleared = Arrays.stream(board.getPits())
				.limit(6)
				.allMatch(i -> 0 == i);
		final boolean isSecondRowCleared = Arrays.stream(board.getPits())
				.skip(7)
				.limit(6)
				.allMatch(i -> 0 == i);
		return isFirstRowCleared || isSecondRowCleared;
	}

	public Map<Player, Integer> getMatchScore(final Board board) {
		return MapStream.of(
				board.getCurrentPlayer(), board.getPits()[6],
				board.getCurrentPlayer().opponent(), board.getPits()[13])
				.collect();
	}

	public void endGameMove(final Board board) {
		board.getPits()[6] = Arrays.stream(board.getPits()).limit(7).sum();
		IntStream.range(0, 6)
				.forEach(i -> board.getPits()[i] = 0);
		board.getPits()[13] = Arrays.stream(board.getPits()).skip(7).sum();
		IntStream.range(7, 13)
				.forEach(i -> board.getPits()[i] = 0);
	}

	public Optional<Player> findWinner(final Board board) {
		final Map<Player, Integer> matchScore = this.getMatchScore(board);
		if (matchScore.get(Player.ONE).equals(matchScore.get(Player.TWO))) {
			//its a draw
			return Optional.empty();
		}
		return MapStream.of(matchScore)
				.max(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey);
	}
}
