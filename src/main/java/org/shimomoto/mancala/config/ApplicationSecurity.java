package org.shimomoto.mancala.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Profile("dev")
@Configuration
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**");
    }
}
