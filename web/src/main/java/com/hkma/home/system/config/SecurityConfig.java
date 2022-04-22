package com.hkma.home.system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.hkma.home.system.service.SystemUserDetailsService;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
    private SystemUserDetailsService userDetailsService;
	
	@Bean
    public PersistentTokenRepository persistentTokenRepository() {
        InMemoryTokenRepositoryImpl memory = new InMemoryTokenRepositoryImpl();
        return memory;
    }
	
	@Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
	        .authorizeRequests()
	        //.antMatchers(HttpMethod.GET, "/**").authenticated()
	        .antMatchers("/jquery/**").permitAll()
	        .antMatchers("/bootstrap/**").permitAll()
	        .antMatchers("/highcharts/**").permitAll()
	        .antMatchers("/css/**").permitAll()
	        .antMatchers("/icon/**").permitAll()
	        .antMatchers("/js/**").permitAll()
	        .antMatchers("/**/include/**").permitAll()
	        .antMatchers("/**/dashboard/**").permitAll()
	        .antMatchers("/**/table/**").permitAll()
	        .antMatchers("/**/list/**").permitAll()
	        .antMatchers("/**/popup/**").permitAll()
	        .antMatchers("/**/data").permitAll()
	        .antMatchers("/").permitAll()
	        .antMatchers("/index").permitAll()
	        .antMatchers("/login").permitAll()
	        .anyRequest().authenticated()
	        .and()
	        //關閉對CSRF（跨站請求偽造）攻擊的防護。這樣Security機制才不會拒絕外部直接對API發出的請求，如Postman 與前端
	        //.csrf().disable()
	        //啟用內建的登入畫面
	        //.formLogin().defaultSuccessUrl("/index");
			.formLogin()
				.loginPage("/login")
				.defaultSuccessUrl("/index")
				.and()
			.rememberMe()
				.rememberMeParameter("remember-me")
				//.tokenValiditySeconds(2000)
				//.userDetailsService(userDetailsService)
				//.rememberMeServices(rememberMeServices)
				.tokenRepository(persistentTokenRepository())
				.and()
			.logout()
				.permitAll()
				.logoutSuccessUrl("/index")
				//開啟CSRF導致logout POST會需要token, 故強制改用GET
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
    }
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth
	    //    .userDetailsService(userDetailsService)
	    //    .passwordEncoder(new BCryptPasswordEncoder());
		//auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
        	//.withUser("admin").password("$2a$10$Q21imUyxDeshQ2tQBUfJKuBHbmuyTsZYoCMRmGi5UcOIavevauZwS").roles("USER");
		//	.withUser("hank770721").password(new BCryptPasswordEncoder().encode("770721")).roles("USER");
		auth
			.userDetailsService(userDetailsService)
			.passwordEncoder(new BCryptPasswordEncoder());
    }
}
