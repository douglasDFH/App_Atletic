package com.club.atletismo.usuario;

import com.club.atletismo.usuario.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public Usuario getUsuarioActual() {
        String correo = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public PerfilResponse getPerfil() {
        return toPerfilResponse(getUsuarioActual());
    }

    @Transactional
    public PerfilResponse editarPerfil(EditarPerfilRequest req) {
        Usuario u = getUsuarioActual();
        u.setNombreCompleto(req.getNombreCompleto());
        if (!u.getCorreo().equals(req.getEmail()) &&
            usuarioRepository.existsByCorreo(req.getEmail())) {
            throw new IllegalArgumentException("El correo ya está en uso");
        }
        u.setCorreo(req.getEmail());
        return toPerfilResponse(usuarioRepository.save(u));
    }

    @Transactional
    public void cambiarContrasena(CambiarContrasenaRequest req) {
        Usuario u = getUsuarioActual();
        if (!passwordEncoder.matches(req.getContrasenaActual(), u.getContrasenaHash())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }
        u.setContrasenaHash(passwordEncoder.encode(req.getNuevaContrasena()));
        usuarioRepository.save(u);
    }

    @Transactional
    public void registrarFcmToken(String token) {
        Usuario u = getUsuarioActual();
        u.setFcmToken(token);
        usuarioRepository.save(u);
    }

    @Transactional
    public PerfilResponse subirFoto(MultipartFile foto) throws IOException {
        Usuario u = getUsuarioActual();
        String dir = System.getProperty("user.dir") + "/uploads/fotos/";
        Files.createDirectories(Paths.get(dir));
        String ext = foto.getOriginalFilename() != null
                && foto.getOriginalFilename().contains(".")
                ? foto.getOriginalFilename().substring(foto.getOriginalFilename().lastIndexOf("."))
                : ".jpg";
        String nombre = UUID.randomUUID() + ext;
        Path ruta = Paths.get(dir + nombre);
        Files.write(ruta, foto.getBytes());
        u.setFotoUrl("/uploads/fotos/" + nombre);
        return toPerfilResponse(usuarioRepository.save(u));
    }

    @Transactional(readOnly = true)
    public List<AtletaInfoDto> getAtletas() {
        return usuarioRepository.findByRolAndActivo(Rol.ATLETA, true).stream()
                .map(u -> new AtletaInfoDto(u.getId(), u.getNombreCompleto(), u.getDisciplina()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AtletaDetalleDto getAtleta(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Atleta no encontrado"));
        return AtletaDetalleDto.builder()
                .id(u.getId())
                .nombreCompleto(u.getNombreCompleto())
                .email(u.getCorreo())
                .disciplina(u.getDisciplina())
                .categoria(u.getCategoria())
                .grupoNombre(u.getGrupo() != null ? u.getGrupo().getNombre() : null)
                .estado(u.isActivo() ? "ACTIVO" : "INACTIVO")
                .build();
    }

    private PerfilResponse toPerfilResponse(Usuario u) {
        return PerfilResponse.builder()
                .id(u.getId())
                .nombreCompleto(u.getNombreCompleto())
                .email(u.getCorreo())
                .rol(u.getRol().name())
                .fechaRegistro(u.getCreadoEn() != null ? u.getCreadoEn().toString() : null)
                .grupoId(u.getGrupo() != null ? u.getGrupo().getId() : null)
                .grupoNombre(u.getGrupo() != null ? u.getGrupo().getNombre() : null)
                .fotoUrl(u.getFotoUrl())
                .build();
    }
}
