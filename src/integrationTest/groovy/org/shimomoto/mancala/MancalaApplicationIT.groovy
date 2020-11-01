package org.shimomoto.mancala


import groovyx.net.http.HttpBuilder
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MancalaApplicationIT extends Specification {
	@LocalServerPort
	private int port

	def "REST API is up and answers"() {
		given: 'dumb url'
		def testUrl = "http://localhost:${port}/api/game/greeting".toURL()

		when: 'invoked'
		testUrl.text

		then: 'returns 418 - I am a teapot'
		def ex = thrown(IOException)
		ex.message == "Server returned HTTP response code: 418 for URL: http://localhost:${port}/api/game/greeting"
	}

	def "Create a game"() {
		given:
		def httpBuilder = HttpBuilder.configure {
			request.uri = "http://localhost:${port}"
		}

		when:
		def result = httpBuilder.post {
			request.uri.path = '/api/game/'
			request.uri.query = [player1: 'kirk', player2: 'spock']
		}

		then:
		result.id != null
		result.board.turnCount == 1
		result.board.currentPlayer == 'ONE'
		result.board.pits == [6, 6, 6, 6, 6, 6, 0, 6, 6, 6, 6, 6, 6, 0]
		result.gameStart != null
		LocalDateTime.parse(result.gameStart, DateTimeFormatter.ISO_LOCAL_DATE_TIME) <= LocalDateTime.now()
		!result.endOfGame
		result.playerNames == ['TWO': 'spock', 'ONE': 'kirk']
		result.winsByPlayer == ['TWO': 0, 'ONE': 0]
	}
}
