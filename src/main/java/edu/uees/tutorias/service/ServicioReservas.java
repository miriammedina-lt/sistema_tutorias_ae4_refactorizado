package edu.uees.tutorias.service;

import edu.uees.tutorias.domain.Docente;
import edu.uees.tutorias.domain.Estudiante;
import edu.uees.tutorias.domain.HorarioTutoria;
import edu.uees.tutorias.domain.Materia;
import edu.uees.tutorias.domain.Reserva;
import edu.uees.tutorias.notification.Notificador;
import edu.uees.tutorias.patrones.builder.ReservaBuilder;

public class ServicioReservas {

    // Refactorización 2: Constantes estáticas (Extract Constant)
    private static final String PREFIJO_RESERVA_ID = "RES-";
    private static final String MENSAJE_CONFIRMACION  = "Tu reserva de tutoría ha sido registrada.";

    private final RepositorioReservas repositorio;
    private final Notificador notificador;

    public ServicioReservas(RepositorioReservas repositorio, Notificador notificador) {
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    public Reserva crearReserva(ReservaBuilder builder) {        
       // Refactorización 1: Guard Clause
        if (builder == null) {
            return null;
        }
        Reserva reserva = builder.build();        
        
        // comentado por refactorizacion 3
        // Refactorización 1: Guard Clause - Eliminación de Deep Nesting y validación de horario
        //if (reserva.getHorario() == null || !reserva.getHorario().estaDisponible()) {
        //    return reserva;
        //}

        // Refactorización 3: Validación modular (Extract Method)
        if (!esHorarioValido(reserva.getHorario())) {
            return reserva;
        }

        // Refactorización 4: Métodos de soporte extraídos (Extract Method)
        procesarYGuardarReserva(reserva);
        notificarEstudiante(reserva);

        //reserva.getHorario().reservarCupo();
        //repositorio.guardar(reserva);

        //if (notificador != null && reserva.getEstudiante() != null) {
        //    notificador.enviarMensaje(reserva.getEstudiante(), MENSAJE_CONFIRMACION);
        //}

        return reserva;
        
        //if (reserva.getHorario() != null && reserva.getHorario().estaDisponible()) {
        //    reserva.getHorario().reservarCupo();
        //    repositorio.guardar(reserva);
            
        //    if (notificador != null && reserva.getEstudiante() != null) {
        //        notificador.enviarMensaje(reserva.getEstudiante(), "Tu reserva de tutoría ha sido registrada.");
        //    }
        //}
        //return reserva;
    }

    public void crearReserva(Estudiante estudiante, Docente docente, Materia materia, HorarioTutoria horario, String motivo) {
        // Refactorización 1:Guard Clause: Validación contra horario nulo
        //if (horario == null || !horario.estaDisponible()) {
        //    return;
        //}

        // Refactorización 3: Validación modular (Extract Method)
        if (!esHorarioValido(horario)) {
            return;
        }

        ReservaBuilder builder = new ReservaBuilder(
            PREFIJO_RESERVA_ID + System.currentTimeMillis(),
            estudiante,
            docente,
            materia,
            horario
        ).observaciones(motivo);

        crearReserva(builder);

        //if (horario.estaDisponible()) {
        //    ReservaBuilder builder = new ReservaBuilder(
        //        "RES-" + System.currentTimeMillis(),
        //        estudiante,
        //        docente,
        //        materia,
        //        horario
        //    ).observaciones(motivo);

        //    crearReserva(builder);
        //}
    }

    // Refactorización 3: Método privado auxiliar extraído
    private boolean esHorarioValido(HorarioTutoria horario) {
        return horario != null && horario.estaDisponible();
    }
    
    // Refactorización 4: Métodos de soporte extraídos
    private void procesarYGuardarReserva(Reserva reserva) {
        reserva.getHorario().reservarCupo();
        repositorio.guardar(reserva);
    }

    private void notificarEstudiante(Reserva reserva) {
        if (notificador != null && reserva.getEstudiante() != null) {
            notificador.enviarMensaje(reserva.getEstudiante(), MENSAJE_CONFIRMACION);
        }
    }
}