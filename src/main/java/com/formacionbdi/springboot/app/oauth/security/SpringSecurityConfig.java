package com.formacionbdi.springboot.app.oauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService usuarioServicio;

	/**
	 * Metodo que encripta la contraseña con Bean para que se guarde en el
	 * contenedor de spring
	 * 
	 * @return
	 */
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Methodo sobre escrito, para registrar el usuarioServicio en el
	 * autenticationManager
	 */
	@Override
	@Autowired // para que se pueda pasar inyectar mediante el metodo.
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(this.usuarioServicio).passwordEncoder(passwordEncoder());

	}

	/**
	 * Methodo para inyectar en la configuracion del servidor de autorizacion de
	 * oauth
	 */
	@Override
	@Bean
	protected AuthenticationManager authenticationManager() throws Exception {

		return super.authenticationManager();
	}

}
