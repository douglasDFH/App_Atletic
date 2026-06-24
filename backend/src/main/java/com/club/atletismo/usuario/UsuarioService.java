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
import java.time.LocalDate;
import java.time.Period;
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
        if (!passwordEncoder.matches(req.getContrasenaActual(), u.getContrasenaHash())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }
        if (!u.getCorreo().equals(req.getEmail()) &&
            usuarioRepository.existsByCorreo(req.getEmail())) {
            throw new IllegalArgumentException("El correo ya está en uso");
        }
        u.setCorreo(req.getEmail());
        if (req.getTelefono() != null && !req.getTelefono().isBlank()) {
            u.setTelefono(req.getTelefono());
        }
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

    /** El entrenador sube/cambia la foto de un atleta específico (HU-12). */
    @Transactional
    public PerfilResponse subirFotoAtleta(Long atletaId, MultipartFile foto) throws IOException {
        Usuario u = usuarioRepository.findById(atletaId)
                .orElseThrow(() -> new IllegalArgumentException("Atleta no encontrado"));
        String dir = System.getProperty("user.dir") + "/uploads/fotos/";
        Files.createDirectories(Paths.get(dir));
        String ext = foto.getOriginalFilename() != null && foto.getOriginalFilename().contains(".")
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

    /** Alta de atleta por el entrenador (CU-02). Crea la cuenta con contraseña inicial. */
    @Transactional
    public void crearAtleta(AtletaCrearRequest req) {
        if (usuarioRepository.existsByCorreo(req.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        LocalDate fechaNac = parseFecha(req.getFechaNacimiento());
        validarTutorSiMenor(fechaNac, req.getTutorNombre(), req.getTutorParentesco(), req.getTutorTelefono());

        Usuario u = Usuario.builder()
                .nombreCompleto(req.getNombreCompleto())
                .correo(req.getCorreo())
                .contrasenaHash(passwordEncoder.encode(req.getContrasena()))
                .rol(Rol.ATLETA)
                .disciplina(req.getDisciplina())
                .categoria(req.getCategoria())
                .fechaNacimiento(fechaNac)
                .tutorNombre(req.getTutorNombre())
                .tutorParentesco(req.getTutorParentesco())
                .tutorTelefono(req.getTutorTelefono())
                .activo(true)
                .emailVerificado(true)  // cuenta creada por entrenador, no requiere verificación
                .build();
        usuarioRepository.save(u);
    }

    /** Edición del perfil de un atleta por el entrenador (RF-04, HU-12). */
    @Transactional
    public AtletaDetalleDto editarAtleta(Long id, AtletaEditarRequest req) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Atleta no encontrado"));
        if (u.getRol() != Rol.ATLETA) {
            throw new IllegalArgumentException("La cuenta no es un atleta");
        }
        if (req.getNombreCompleto() != null && !req.getNombreCompleto().isBlank()) {
            u.setNombreCompleto(req.getNombreCompleto());
        }
        u.setDisciplina(req.getDisciplina());
        u.setCategoria(req.getCategoria());
        LocalDate fechaNac = parseFecha(req.getFechaNacimiento());
        if (fechaNac != null) u.setFechaNacimiento(fechaNac);
        validarTutorSiMenor(u.getFechaNacimiento(), req.getTutorNombre(),
                req.getTutorParentesco(), req.getTutorTelefono());
        u.setTutorNombre(req.getTutorNombre());
        u.setTutorParentesco(req.getTutorParentesco());
        u.setTutorTelefono(req.getTutorTelefono());
        usuarioRepository.save(u);
        return getAtleta(id);
    }

    /** Activa/desactiva un atleta sin borrar su historial (HU-12). */
    @Transactional
    public void cambiarEstadoAtleta(Long id, boolean activo) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Atleta no encontrado"));
        if (u.getRol() != Rol.ATLETA) {
            throw new IllegalArgumentException("La cuenta no es un atleta");
        }
        u.setActivo(activo);
        usuarioRepository.save(u);
    }

    private LocalDate parseFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) return null;
        try {
            return LocalDate.parse(fecha);
        } catch (Exception e) {
            throw new IllegalArgumentException("Fecha de nacimiento inválida (formato AAAA-MM-DD)");
        }
    }

    private void validarTutorSiMenor(LocalDate fechaNac, String tNombre, String tParent, String tTel) {
        boolean menor = fechaNac != null && Period.between(fechaNac, LocalDate.now()).getYears() < 18;
        if (menor && (esVacio(tNombre) || esVacio(tParent) || esVacio(tTel))) {
            throw new IllegalArgumentException(
                    "El atleta es menor de edad: se requieren nombre, parentesco y teléfono del tutor");
        }
    }

    private boolean esVacio(String s) { return s == null || s.isBlank(); }

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

    /** Actualiza las preferencias de notificaciones push del usuario autenticado (HU-11). */
    @Transactional
    public void actualizarPreferenciasNotif(NotifPreferenciasRequest req) {
        Usuario u = getUsuarioActual();
        if (req.getSesiones() != null)     u.setNotifSesiones(req.getSesiones());
        if (req.getCompetencias() != null) u.setNotifCompetencias(req.getCompetencias());
        if (req.getResultados() != null)   u.setNotifResultados(req.getResultados());
        usuarioRepository.save(u);
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
                .telefono(u.getTelefono())
                .atletaVinculadoId(hijo != null ? hijo.getId() : null)
                .atletaVinculadoNombre(hijo != null ? hijo.getNombreCompleto() : null)
                .notifSesiones(u.getNotifSesiones() != null ? u.getNotifSesiones() : true)
                .notifCompetencias(u.getNotifCompetencias() != null ? u.getNotifCompetencias() : true)
                .notifResultados(u.getNotifResultados() != null ? u.getNotifResultados() : true)
                .build();
    }
}
