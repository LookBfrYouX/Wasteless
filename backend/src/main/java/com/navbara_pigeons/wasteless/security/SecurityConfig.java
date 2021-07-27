package com.navbara_pigeons.wasteless.security;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final UserDetailsService userDetailsService;

  @Autowired
  public SecurityConfig(
      @Qualifier("basicUserDetailsServiceImpl") UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());

    return authProvider;
  }

  /**
   * This method configures the global AuthenticationManager by registering
   * authenticationProviders.
   *
   * @param auth
   */
  @Autowired
  protected void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(authenticationProvider());
  }

  /**
   * Use this method to secure Http endpoints.
   *
   * @param http HttpSecurityBuilder implementation that allows Http endpoints to be secured.
   * @throws Exception Throws generic exceptions.
   * @see <a href="https://docs.spring.io/spring-security/site/docs/5.4.5/api/">HttpSecurity</a>
   */
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers(HttpMethod.POST, "/login").permitAll()
        .antMatchers(HttpMethod.POST, "/users").permitAll()
        .antMatchers(HttpMethod.GET, "/users/{id}").authenticated()
        .antMatchers(HttpMethod.GET, "/users/search*").authenticated()
        .antMatchers(HttpMethod.PUT, "/users/{id}/makeAdmin").hasRole("ADMIN")
        .antMatchers(HttpMethod.PUT, "/users/{id}/revokeAdmin", "/admin").hasRole("ADMIN")
        .antMatchers(HttpMethod.POST, "/businesses").authenticated()
        .antMatchers(HttpMethod.GET, "/businesses/{id}").authenticated()
        .antMatchers(HttpMethod.GET, "/businesses/{id}/products").authenticated()
        .antMatchers(HttpMethod.POST, "/businesses/{id}/products").authenticated()
        .antMatchers(HttpMethod.POST, "/users/{id}/images").authenticated()
        .antMatchers(HttpMethod.POST, "/businesses/{businessId}/products/{productId}/images")
        .authenticated()
        .antMatchers(HttpMethod.GET, "/users/{id}/images").permitAll()
        .antMatchers(HttpMethod.POST, "/businesses/{id}/listings").authenticated()
        .antMatchers(HttpMethod.PUT, "/businesses/{businessId}/makeAdministrator").authenticated()
        .antMatchers(HttpMethod.GET, "/keywords").authenticated()
        .antMatchers(HttpMethod.PUT, "/businesses/{businessId}/removeAdministrator").authenticated()
        .antMatchers(HttpMethod.GET, "/keywords").permitAll()
        .antMatchers("/swagger-ui/**").permitAll()
        .antMatchers("/api-docs.yaml").permitAll()
        .antMatchers("/api-docs/**").permitAll()
        .anyRequest().authenticated()
        .and()
        .httpBasic() // comment out to prevent login prompts appearing on API calls when logged out
        .and()
        .cors()
        .and()
        .logout(logout -> logout // This prevents redirects on logout
            .permitAll()
            .logoutSuccessHandler((request, response, authentication) -> {
              response.setStatus(HttpServletResponse.SC_OK);
            }))
        .csrf().disable();
  }
}