package com.formacionbdi.springboot.app.oauth.services;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.formacionbdi.springboot.app.oauth.clients.UsuarioFeignClient;
import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;

import feign.FeignException;

@Service
public class UsuarioService implements IUsuarioService, UserDetailsService {

	private static Logger log = LoggerFactory.getLogger(UsuarioService.class);

	@Autowired
	private UsuarioFeignClient client;

	
	/**
	 * Metodo encargado de obtener el usuario por el username usando el
	 * clienteHttp(UsuarioFeignClient)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		try {
			  Usuario usuario = client.findByUsername(username);

			 List<GrantedAuthority> authorities = usuario.getRoles().stream()
					.map(role -> new SimpleGrantedAuthority(role.getNombre()))
					.peek(authority -> log.info("Role: " + authority.getAuthority())).collect(Collectors.toList());

			 log.info("Usuario autenticado: " + username);

			 return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(), true, true, true,
					authorities);

		} catch (FeignException e) {
			log.error("Error en el login, no existe el usaurio '" + username + "' en el sistema");
			throw new UsernameNotFoundException("Error en el login, no existe el usaurio '" + username + "' en el sistema");
		}
	}

	/**
	 * Para obtener la informacion adicional del usuario (IUsuarioService)
	 */
	@Override
	public Usuario findByUsername(String username) {

		return client.findByUsername(username);
	}

	/**
	 * Metodo para actualizar intentos de login
	 */
	@Override
	public Usuario update(Usuario usuario, Long id) {

		return client.update(usuario, id);
	}

}
