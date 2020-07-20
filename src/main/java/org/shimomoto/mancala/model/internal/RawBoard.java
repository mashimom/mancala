package org.shimomoto.mancala.model.internal;

import lombok.Builder;
import lombok.Data;
import org.shimomoto.mancala.model.domain.Player;

import java.util.Arrays;

@Builder(toBuilder = true)
@Data
public class RawBoard {
	@Builder.Default
	int turnCount = 1;
	@Builder.Default
	Player currentPlayer = Player.ONE;
	@Builder.Default
	int[] board = {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};

	private static boolean isPassingTurn(int endsAt) {
		return endsAt != 6;
	}

	private static boolean isCapture(int endsAt, int[] board) {
		return endsAt >= 0 && endsAt < 6 && board[endsAt] == 1;
	}

	public boolean isLegalMove(Player player, int position) {
		if (currentPlayer == player) {
			return position >= 0 && position < 6 && board[position] != 0;
		}
		return false;
	}

	public void changeTurn() {
		currentPlayer = currentPlayer.opponent();
		final int[] copy = Arrays.copyOf(board, board.length);
		for (int i = 0; i <= 6; i++) {
			board[i] = copy[i + 7];
			board[i + 7] = copy[i];
		}
	}

	public void move(Player player, int position) {
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

	private void capture(int captureFrom) {
		int opposingPosition = 12 - captureFrom;
		board[6] += (board[opposingPosition] + board[captureFrom]);
		board[opposingPosition] = 0;
		board[captureFrom] = 0;
	}

	private void sow(int position, int rounds, int spare) {
		board[position] = 0;
		for (int i = 0; i < board.length; i++) {
			board[i] += rounds;
			if (spare > 0 && i > position) {
				board[i]++;
				spare--;
			}
		}
	}
}
