package com.formacionbdi.springboot.app.oauth.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@RefreshScope // toma cambios cuando ejecutamos el endpint con atuator del archivo boostrap.properties
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private InfoAdicionalToken infoAdicionalToken;

    @Autowired
    private Environment env; // para leer variables del archivo de configuracion boostrap.properties

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
		
		clients.inMemory().withClient(env.getProperty("config.security.oauth.client.id")) //identificador de nuestra aplicacion
		.secret(passwordEncoder.encode(env.getProperty("config.security.oauth.client.secret"))) // contraseña de nuestra aplicacion encriptada con passwordEncoder
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

		// Unir los datos del token, la informacion adicional
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain(); 
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(infoAdicionalToken, accesTokenConverter()));
		
		endpoints.authenticationManager(authenticationManager)
		.tokenStore(tokenStore())
		.accessTokenConverter(accesTokenConverter())
		.tokenEnhancer(tokenEnhancerChain);
	}

	/**
	 * metodo que crea el token
	 * @return
	 */
	@Bean
	public JwtTokenStore tokenStore() {
		return new JwtTokenStore(accesTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accesTokenConverter() {
		JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
		tokenConverter.setSigningKey(env.getProperty("config.security.oauth.jwt.key"));
		
		return tokenConverter;
	}

}
