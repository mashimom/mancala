package org.shimomoto.mancala.model.util

import spock.lang.Specification

class PublicIdSupplierSpec extends Specification {

	def "Multiple calls do not generate duplicates"() {
		expect:
		(1..1_000).collect { PublicIdSupplier.get() }.toSet().size() == 1_000
	}
}
