package com.club.atletismo.usuario;

import com.club.atletismo.grupo.Grupo;
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
        Usuario padre = usuarioRepository.findFirstByAtletaVinculadoId(id).orElse(null);
        return AtletaDetalleDto.builder()
                .id(u.getId())
                .nombreCompleto(u.getNombreCompleto())
                .email(u.getCorreo())
                .disciplina(u.getDisciplina())
                .categoria(u.getCategoria())
                .grupoNombre(u.getGrupo() != null ? u.getGrupo().getNombre() : null)
                .estado(u.isActivo() ? "ACTIVO" : "INACTIVO")
                .fechaNacimiento(u.getFechaNacimiento() != null ? u.getFechaNacimiento().toString() : null)
                .edad(u.getEdad())
                .esMenor(u.isMenorDeEdad())
                .tutorNombre(u.getTutorNombre())
                .tutorParentesco(u.getTutorParentesco())
                .tutorTelefono(u.getTutorTelefono())
                .tutorVinculadoId(padre != null ? padre.getId() : null)
                .tutorVinculadoNombre(padre != null ? padre.getNombreCompleto() : null)
                .build();
    }

    /** Lista de cuentas PADRE/Tutor con el hijo que tienen vinculado (para el entrenador). */
    @Transactional(readOnly = true)
    public List<PadreDto> getPadres() {
        return usuarioRepository.findByRol(Rol.PADRE).stream()
                .map(p -> PadreDto.builder()
                        .id(p.getId())
                        .nombreCompleto(p.getNombreCompleto())
                        .email(p.getCorreo())
                        .hijoId(p.getAtletaVinculado() != null ? p.getAtletaVinculado().getId() : null)
                        .hijoNombre(p.getAtletaVinculado() != null
                                ? p.getAtletaVinculado().getNombreCompleto() : null)
                        .build())
                .collect(Collectors.toList());
    }

    /** El entrenador vincula una cuenta PADRE con un atleta (hijo). Un hijo por padre. */
    @Transactional
    public void vincularHijo(Long padreId, Long atletaId) {
        Usuario padre = usuarioRepository.findById(padreId)
                .orElseThrow(() -> new IllegalArgumentException("Padre no encontrado"));
        if (padre.getRol() != Rol.PADRE) {
            throw new IllegalArgumentException("La cuenta indicada no es un padre/tutor");
        }
        Usuario atleta = usuarioRepository.findById(atletaId)
                .orElseThrow(() -> new IllegalArgumentException("Atleta no encontrado"));
        if (atleta.getRol() != Rol.ATLETA) {
            throw new IllegalArgumentException("La cuenta a vincular no es un atleta");
        }
        padre.setAtletaVinculado(atleta);
        usuarioRepository.save(padre);
    }

    /** El entrenador desvincula al hijo de una cuenta PADRE. */
    @Transactional
    public void desvincularHijo(Long padreId) {
        Usuario padre = usuarioRepository.findById(padreId)
                .orElseThrow(() -> new IllegalArgumentException("Padre no encontrado"));
        padre.setAtletaVinculado(null);
        usuarioRepository.save(padre);
    }

    private PerfilResponse toPerfilResponse(Usuario u) {
        // Para un PADRE, el grupo/contexto mostrado es el de su hijo vinculado
        Usuario hijo = u.getRol() == Rol.PADRE ? u.getAtletaVinculado() : null;
        Grupo grupoCtx = hijo != null ? hijo.getGrupo() : u.getGrupo();
        return PerfilResponse.builder()
                .id(u.getId())
                .nombreCompleto(u.getNombreCompleto())
                .email(u.getCorreo())
                .rol(u.getRol().name())
                .fechaRegistro(u.getCreadoEn() != null ? u.getCreadoEn().toString() : null)
                .grupoId(grupoCtx != null ? grupoCtx.getId() : null)
                .grupoNombre(grupoCtx != null ? grupoCtx.getNombre() : null)
                .fotoUrl(u.getFotoUrl())
                .atletaVinculadoId(hijo != null ? hijo.getId() : null)
                .atletaVinculadoNombre(hijo != null ? hijo.getNombreCompleto() : null)
                .build();
    }
}
