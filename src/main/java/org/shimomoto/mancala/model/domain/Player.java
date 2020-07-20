package org.shimomoto.mancala.model.domain;

public enum Player {
	ONE,
	TWO;

	public Player opponent() {
		return switch (this) {
			case ONE -> TWO;
			case TWO -> ONE;
		};
	}
}
