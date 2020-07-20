package org.shimomoto.mancala.model.internal;

import lombok.Builder;
import lombok.Data;
import org.shimomoto.mancala.model.domain.Player;

import java.util.Arrays;

@Builder(toBuilder = true)
@Data
public class RawBoard {
	@Builder.Default
	Player currentPlayer = Player.ONE;
	@Builder.Default
	int[] board = {6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0};


	public boolean isLegalMove(Player player, int position) {
		if(currentPlayer == player) {
			return position >= 0 && position < 6 && board[position] != 0;
		}
		return false;
	}

	public void changeTurn() {
		currentPlayer = currentPlayer.opponent();
		final int[] copy = Arrays.copyOf(board, board.length);
		for (int i = 0; i <= 6; i++) {
			board[i] = copy[i+7];
			board[i+7] = copy[i];
		}
	}

	public void move(Player player, int position) {

	}
}
