package com.maestria.gestion.autenticacion.usuarios.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maestria.gestion.autenticacion.usuarios.configuration.JwtProvider;
import com.maestria.gestion.autenticacion.usuarios.dto.KiraResponseDTO;
import com.maestria.gestion.autenticacion.usuarios.service.KIRA.KiraApiService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private KiraApiService kiraApiService;

    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> request) {
        String firebaseToken = request.get("token");
        String mockRole = request.getOrDefault("mockRole", "ROLE_COORDINADOR"); // Rol por defecto si no se envía

        KiraResponseDTO usuario = null;

        // Intentamos consultar KIRA (Flujo normal)
        String correo = "dev@unicauca.edu.co"; // En producción se extrae del Firebase Token
        try {
             usuario = kiraApiService.obtenerUsuarioPorCorreo(correo);
        } catch (Exception e) {
             // KIRA falló o no está disponible.
        }

        // Si KIRA no devuelve nada o falla (muy común en desarrollo local), construimos un usuario Mock
        if (usuario == null) {
            usuario = new KiraResponseDTO();
            usuario.setNombres("Dev");
            usuario.setApellidos("User");
            usuario.setCorreo(correo);
            usuario.setRol(mockRole); // Usamos el rol que pide el front
            usuario.setCodigoAcademico("12345");
            usuario.setTipoIdentificacion("CC");
            usuario.setNumeroIdentificacion("987654321");
        } else {
             // Si KIRA funciona, pero queremos forzar el rol para desarrollo:
             usuario.setRol(mockRole);
        }

        String jwt = jwtProvider.generateToken(usuario);

        Map<String, String> response = new HashMap<>();
        response.put("token", jwt);
        response.put("tokenOriginal", jwt);

        return ResponseEntity.ok(response);
    }
}
