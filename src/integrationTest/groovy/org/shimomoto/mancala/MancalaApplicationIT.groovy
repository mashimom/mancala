package org.shimomoto.mancala

import groovyx.net.http.HttpBuilder
import org.shimomoto.mancala.service.GameFacade
import org.shimomoto.mancala.service.UserFacade
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MancalaApplicationIT extends Specification {
	@LocalServerPort
	private int port

	@Autowired
	GameFacade gameFacade

	@Autowired
	UserFacade userFacade

	def "REST API is up and answers"() {
		given: 'dumb url'
		def testUrl = "http://localhost:${port}/api/game/greeting".toURL()

		when: 'invoked'
		testUrl.text

		then: 'returns 418 - I am a teapot'
		def ex = thrown(IOException)
		ex.message == "Server returned HTTP response code: 418 for URL: http://localhost:${port}/api/game/greeting"
	}

	def "add Player"() {
		given:
		def httpBuilder = HttpBuilder.configure {
			request.uri = "http://localhost:${port}"
		}

		when:
		def result = httpBuilder.post {
			request.uri.path = "/api/user/"
			request.uri.query = [screenName: 'kirk']
		}

		then:
		result != null
	}
}
