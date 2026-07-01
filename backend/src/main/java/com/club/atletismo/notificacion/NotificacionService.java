package com.club.atletismo.notificacion;

import com.club.atletismo.notificacion.dto.NotificacionDto;
import com.club.atletismo.usuario.Usuario;
import com.club.atletismo.usuario.UsuarioRepository;
import com.club.atletismo.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final FcmService fcmService;

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public List<NotificacionDto> getMisNotificaciones() {
        Usuario u = usuarioService.getUsuarioActual();
        return notificacionRepository.findByUsuarioIdOrderByFechaDesc(u.getId())
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void marcarLeida(Long id) {
        Notificacion n = notificacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));
        n.setLeida(true);
        notificacionRepository.save(n);
    }

    @Transactional
    public void marcarTodasLeidas() {
        Usuario u = usuarioService.getUsuarioActual();
        notificacionRepository.marcarTodasLeidas(u.getId());
    }

    @Transactional
    public void crearParaTodos(String tipo, String titulo, String mensaje) {
        usuarioRepository.findAll().forEach(u -> crear(u, tipo, titulo, mensaje));
    }

    // REQUIRES_NEW: cada notificación corre en su PROPIA transacción. Así, si el
    // guardado de la notificación o el envío FCM falla, NO contamina ni revierte la
    // transacción de quien la invoca (p. ej. la creación de una sesión).
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void crear(Usuario usuario, String tipo, String titulo, String mensaje) {
        Notificacion n = Notificacion.builder()
                .usuario(usuario)
                .tipo(tipo)
                .titulo(titulo)
                .mensaje(mensaje)
                .build();
        notificacionRepository.save(n);
        // Solo envía push FCM si el usuario tiene habilitado ese tipo de notificación.
        // null = no configurado → activo por defecto. Un fallo de FCM no debe tumbar
        // la notificación ya guardada.
        if (debeRecibirPush(usuario, tipo)) {
            try {
                fcmService.sendToToken(usuario.getFcmToken(), titulo, mensaje);
            } catch (Exception e) {
                log.warn("Envío FCM falló para usuario {}: {}", usuario.getId(), e.toString());
            }
        }
    }

    private boolean debeRecibirPush(Usuario u, String tipo) {
        return switch (tipo) {
            case "SESION"      -> !Boolean.FALSE.equals(u.getNotifSesiones());
            case "COMPETENCIA" -> !Boolean.FALSE.equals(u.getNotifCompetencias());
            case "RESULTADO"   -> !Boolean.FALSE.equals(u.getNotifResultados());
            default            -> true; // CATEGORIA y otros siempre se envían
        };
    }

    private NotificacionDto toDto(Notificacion n) {
        return NotificacionDto.builder()
                .id(n.getId())
                .tipo(n.getTipo())
                .titulo(n.getTitulo())
                .mensaje(n.getMensaje())
                .fecha(n.getFecha().format(DT))
                .leida(n.isLeida())
                .build();
    }
}
