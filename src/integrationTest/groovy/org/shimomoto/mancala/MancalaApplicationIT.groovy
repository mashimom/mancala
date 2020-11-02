package org.shimomoto.mancala


import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Specification

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
}
