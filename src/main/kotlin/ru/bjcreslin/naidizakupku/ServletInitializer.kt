package ru.bjcreslin.naidizakupku

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

class ServletInitializer : SpringBootServletInitializer() {

	override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
		return application.sources(NaidiZakupkuApplication::class.java)
	}

}
