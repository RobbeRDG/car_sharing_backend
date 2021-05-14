package be.kul.rideservice.utils.security;


import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/**").hasAuthority("SCOPE_ride:service")
                .antMatchers("/admin/**").hasAuthority("SCOPE_administration")
                .anyRequest().authenticated()
                .and().oauth2ResourceServer().jwt();
    }
}
