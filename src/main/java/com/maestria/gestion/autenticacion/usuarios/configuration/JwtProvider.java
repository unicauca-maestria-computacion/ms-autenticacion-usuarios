package com.maestria.gestion.autenticacion.usuarios.configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.maestria.gestion.autenticacion.usuarios.dto.KiraResponseDTO;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtProvider {

    @Value("${app.jwtSecret:secretKey123}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs:3600000}")
    private int jwtExpirationMs;

    public String generateToken(KiraResponseDTO usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", usuario.getNombres() + " " + usuario.getApellidos());
        claims.put("correo", usuario.getCorreo());
        claims.put("rol", new String[]{usuario.getRol()}); // Frontend expects array
        claims.put("telefono", usuario.getTelefono());
        claims.put("codigoAcademico", usuario.getCodigoAcademico());
        claims.put("nombres", usuario.getNombres());
        claims.put("apellidos", usuario.getApellidos());
        claims.put("tipoIdentificacion", usuario.getTipoIdentificacion());
        claims.put("numeroIdentificacion", usuario.getNumeroIdentificacion());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getCorreo())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
