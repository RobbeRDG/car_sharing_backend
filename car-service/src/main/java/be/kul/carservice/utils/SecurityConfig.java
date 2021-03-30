package be.kul.carservice.utils;


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
                .antMatchers(HttpMethod.POST, "/car-service/cars").hasAuthority("SCOPE_car:service:add_car")
                .antMatchers(HttpMethod.POST, "/car-service/cars/batch").hasAuthority("SCOPE_car:service:add_car")
                .antMatchers("/**").hasAuthority("SCOPE_car:service")
                .anyRequest().authenticated()
                .and().oauth2ResourceServer().jwt();

    }

}
