package com.formacionbdi.springboot.app.oauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;



	/**
	 * Metodo que genera los permisos que va atener nuestros endpoint del servidor de autorizacion 
	 * de oauth2 generar el token y validar el token
	 */
	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
         
		security.tokenKeyAccess("permitAll()")//end poin para generar el token
		.checkTokenAccess("isAuthenticated()");// ruta para validar el token, que el cliente este autenticado
		
	}

	/**
	 * Metodo para registrar clientes(front end) que se van acomunicar con nuestro servicio
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		
		clients.inMemory().withClient("frontendapp") //identificador de nuestra aplicacion
		.secret(passwordEncoder.encode("12345")) // contraseña de nuestra aplicacion encriptada con passwordEncoder
		.scopes("read","write") // alcanze o permisos de nuestra aplicacion cliente
		.authorizedGrantTypes("password", "refresh_token") // tipo de consecion, como se optiene el token. con password
        .accessTokenValiditySeconds(3600) // tiempo de valides del token antes de que caduque
        .refreshTokenValiditySeconds(3600); // tiempo de refres token
	}

	/**
	 * Metodo para configurar el autentication manager, se encarga de generar el token
	 */
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

		endpoints.authenticationManager(authenticationManager)
		.tokenStore(tokenStore())
		.accessTokenConverter(accesTokenConverter());
	}

	/**
	 * metodo que crea el token
	 * @return
	 */
	@Bean
	private JwtTokenStore tokenStore() {
		return new JwtTokenStore(accesTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accesTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey("algun_codigo_secreto_aeiou");
		
		return tokenConverter;
	}

}
