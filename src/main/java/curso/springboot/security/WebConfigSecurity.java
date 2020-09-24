package curso.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private ImplementacaoUserDetailService implementacaoUserDetailService;

	@Override // configura as solicitações de acesso por http
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable() // desativa as configurações padrao
				.authorizeRequests().antMatchers(HttpMethod.GET, "/").permitAll().anyRequest().authenticated().and()
				.formLogin().permitAll().and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	}

	@Override // cria a autenticação do usuario com o banco de dados
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(implementacaoUserDetailService)
		.passwordEncoder(NoOpPasswordEncoder.getInstance());
		//.passwordEncoder(new BCryptPasswordEncoder());


	}

	@Override // ignora url especificas
	public void configure(WebSecurity web) throws Exception {

		web.ignoring().antMatchers("/materialize/**");
	}

}