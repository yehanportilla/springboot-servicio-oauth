package com.formacionbdi.springboot.app.oauth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.formacionbdi.springboot.app.usuarios.commons.models.entity.Usuario;

@FeignClient(name = "servicio-usuarios") // nombre del recurso al cual queremos comunicarnos
public interface UsuarioFeignClient {

	/**
	 * Metodo anotado con getmapping para definir el endpoint al cual vamos abuscar
	 * el usuario por el username
	 * 
	 * @param username
	 * @return
	 */
	@GetMapping("usuarios/search/buscar-username")
	public Usuario findByUsername(@RequestParam String username);

}
