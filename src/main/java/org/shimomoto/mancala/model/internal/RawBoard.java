package org.shimomoto.mancala.model.internal;

import com.codepoetics.protonpack.maps.MapStream;
import lombok.Builder;
import lombok.Data;
import org.shimomoto.mancala.model.domain.Player;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Builder(toBuilder = true)
@Data
public class RawBoard {
	@SuppressWarnings("UnusedAssignment")
	@Builder.Default
	int turnCount = 1;
	@SuppressWarnings("UnusedAssignment")
	@Builder.Default
	Player currentPlayer = Player.ONE;
	@SuppressWarnings("UnusedAssignment")
	@Builder.Default
	int[] board = {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};

	private static boolean isPassingTurn(final int endsAt) {
		return endsAt != 6;
	}

	private static boolean isCapture(final int endsAt, final int[] board) {
		return endsAt >= 0 && endsAt < 6 && board[endsAt] == 1;
	}

	public boolean isLegalMove(final Player player, final int position) {
		if (currentPlayer != player) {
			return false;
		}
		return position >= 0 && position < 6 && board[position] != 0;
	}

	public void changeTurn() {
		currentPlayer = currentPlayer.opponent();
		final int[] copy = Arrays.copyOf(board, board.length);
		for (int i = 0; i <= 6; i++) {
			board[i] = copy[i + 7];
			board[i + 7] = copy[i];
		}
	}

	public void move(final Player player, final int position) {
		if (!isLegalMove(player, position)) {
			return;
		}

		//calculates intermediate values
		final int rounds = board[position] / 14;
		final int spare = board[position] % 14;
		final int endsAt = spare + position;

		//makes changes
		sow(position, rounds, spare);

		//will capture if sowing ends on own pits and at a count 1
		if (isCapture(endsAt, board)) {
			capture(endsAt);
		}

		if (isPassingTurn(endsAt)) {
			changeTurn();
		}

		turnCount++;
	}

	private void capture(final int captureFrom) {
		final int opposingPosition = 12 - captureFrom;
		board[6] += (board[opposingPosition] + board[captureFrom]);
		board[opposingPosition] = 0;
		board[captureFrom] = 0;
	}

	private void sow(final int position, final int rounds, int spare) {
		board[position] = 0;
		for (int i = 0; i < board.length; i++) {
			board[i] += rounds;
			if (spare > 0 && i > position) {
				board[i]++;
				spare--;
			}
		}
	}

	public boolean isEndOfGame() {
		final boolean isFirstRowCleared = Arrays.stream(board)
				.limit(6)
				.allMatch(i -> 0 == i);
		final boolean isSecondRowCleared = Arrays.stream(board)
				.skip(7)
				.limit(6)
				.allMatch(i -> 0 == i);
		return isFirstRowCleared || isSecondRowCleared;
	}

	public Map<Player, Integer> getScore() {
		return MapStream.of(
				currentPlayer,
				Arrays.stream(board).limit(7).sum(),
				currentPlayer.opponent(),
				Arrays.stream(board).skip(7).sum())
				.collect();
	}

	public Optional<Player> getWinner() {
		if (!isEndOfGame()) {
			return Optional.empty();
		}
		return MapStream.of(this.getScore())
				.max(Map.Entry.comparingByValue())
				.map(Map.Entry::getKey);
	}
}
