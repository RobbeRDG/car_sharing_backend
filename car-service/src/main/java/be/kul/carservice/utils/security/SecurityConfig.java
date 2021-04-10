package be.kul.carservice.utils.security;


import org.springframework.http.HttpMethod;
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
                .antMatchers(HttpMethod.GET, "/car-service/cars/available").permitAll()
                .antMatchers("/**").hasAuthority("SCOPE_car:service")
                .antMatchers("/admin/**").hasAuthority("SCOPE_administration")
                .anyRequest().authenticated()
                .and().oauth2ResourceServer().jwt();
    }
}
