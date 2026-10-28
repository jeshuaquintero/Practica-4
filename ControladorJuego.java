import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class ControladorJuego {
    private final Tablero tableroJugador;
    private final Tablero tableroMaquina;
    private final GestorBombas gestorBombas;
    private final VistaJuego vista;
    private final Random random;

    private final String[] nombresBarcos = {
            "Portaaviones",
            "Acorazado",
            "Submarino",
            "Destructor",
            "Patrullero"
    };

    private final String[] simbolosBarcos = {"P", "A", "S", "D", "F"};
    private final int[] tamanosBarcos = {4, 3, 3, 2, 2};

    private final String[] coloresBarcos = {
            "\u001B[35m", // Magenta
            "\u001B[33m", // Amarillo
            "\u001B[32m", // Verde
            "\u001B[36m", // Cyan
            "\u001B[91m"  // Rojo claro
    };

    public ControladorJuego(VistaJuego vista) {
        this.tableroJugador = new Tablero();
        this.tableroMaquina = new Tablero();
        this.gestorBombas = new GestorBombas();
        this.vista = vista;
        this.random = new Random();
    }

    public void iniciarJuego() {
        int opcion = vista.mostrarMenuPosicionamiento();
        if (opcion == 1) {
            posicionarBarcosManual();
        } else {
            posicionarBarcosAleatorio(tableroJugador);
            vista.mostrarMensaje("Los 5 barcos fueron posicionados aleatoriamente.");
        }

        posicionarBarcosAleatorio(tableroMaquina);

        boolean juegoActivo = true;
        while (juegoActivo) {
            vista.mostrarTablerosParalelos(tableroJugador, tableroMaquina);

            ejecutarTurnoJugador();

            if (tableroMaquina.todaLaFlotaHundida()) {
                vista.mostrarMensaje("¡Felicidades! Has destruido toda la flota enemiga.");
                juegoActivo = false;
                break;
            }

            ejecutarTurnoMaquina();

            if (tableroJugador.todaLaFlotaHundida()) {
                vista.mostrarMensaje("Has sido derrotado. La máquina destruyó tu flota.");
                juegoActivo = false;
            }
        }
    }

    private void posicionarBarcosManual() {
        for (int i = 0; i < tamanosBarcos.length; i++) {
            boolean colocado = false;
            while (!colocado) {
                vista.mostrarMensaje("Posicionando barco " + (i + 1) + " de " + tamanosBarcos.length +
                        ": " + nombresBarcos[i] + " [" + simbolosBarcos[i] + "] (Tamaño: " + tamanosBarcos[i] + ")");
                tableroJugador.mostrarTablero(false);
                int[] coord = vista.pedirCoordenadas();
                int orientacion = vista.pedirOrientacion();
                colocado = intentarColocarBarco(tableroJugador, nombresBarcos[i], simbolosBarcos[i], coloresBarcos[i], tamanosBarcos[i], coord[0], coord[1], orientacion == 1);
                if (!colocado) {
                    vista.mostrarMensaje("Posición inválida, fuera de rango o traslape. Intente de nuevo.");
                }
            }
        }
    }

    private void posicionarBarcosAleatorio(Tablero tablero) {
        for (int i = 0; i < tamanosBarcos.length; i++) {
            boolean colocado = false;
            while (!colocado) {
                int fila = random.nextInt(10);
                int col = random.nextInt(10);
                boolean horizontal = random.nextBoolean();
                colocado = intentarColocarBarco(tablero, nombresBarcos[i], simbolosBarcos[i], coloresBarcos[i], tamanosBarcos[i], fila, col, horizontal);
            }
        }
    }

    private boolean intentarColocarBarco(Tablero tablero, String nombre, String simbolo, String color, int tamano, int fila, int col, boolean horizontal) {
        ArrayList<Casilla> objetivo = new ArrayList<>();
        for (int k = 0; k < tamano; k++) {
            int r = horizontal ? fila : fila + k;
            int c = horizontal ? col + k : col;

            if (r < 0 || r >= 10 || c < 0 || c >= 10) return false;
            Casilla cas = tablero.getCasilla(r, c);
            if (cas == null || cas.tieneBarco()) return false;
            objetivo.add(cas);
        }

        Barco barco = new Barco(nombre, simbolo, color);
        for (Casilla c : objetivo) {
            barco.agregarCasilla(c);
        }
        tablero.getFlota().add(barco);
        return true;
    }

    private void ejecutarTurnoJugador() {
        int opcion = vista.mostrarMenuTurno(gestorBombas.getIntentosRestantes());

        if (opcion == 2) {
            if (gestorBombas.getIntentosRestantes() <= 0) {
                vista.mostrarMensaje("La opción de bomba está deshabilitada (0 intentos restantes). Se realiza disparo normal.");
                disparoNormalJugador();
                return;
            }

            String resultado = gestorBombas.tirarRuletaBomba();
            procesarResultadoBomba(resultado);
        } else {
            disparoNormalJugador();
        }
    }

    private void disparoNormalJugador() {
        boolean disparoValido = false;
        while (!disparoValido) {
            int[] coord = vista.pedirCoordenadas();
            Casilla objetivo = tableroMaquina.getCasilla(coord[0], coord[1]);

            if (objetivo != null) {
                if (objetivo.fueAtacada()) {
                    vista.mostrarMensaje("Esa casilla ya fue atacada previamente. Por favor, elija otra coordenada.");
                } else {
                    objetivo.marcarAtacada();
                    disparoValido = true;
                    if (objetivo.tieneBarco()) {
                        Barco barcoAfectado = objetivo.getBarco();
                        if (barcoAfectado != null && barcoAfectado.estaHundido()) {
                            vista.mostrarMensaje("¡IMPACTO DIRECTO! ¡Y HAS HUNDIDO EL " + barcoAfectado.getNombre().toUpperCase() + " ENEMIGO! 💥");
                            vista.mostrarMensaje("BARCOS RESTANTES ENEMIGOS: " + tableroMaquina.obtenerBarcosRestantes());
                        } else {
                            vista.mostrarMensaje("¡Impacto directo!");
                        }
                    } else {
                        vista.mostrarMensaje("Agua...");
                    }
                }
            }
        }
    }

    private void procesarResultadoBomba(String resultado) {
        if (resultado.startsWith("ACTIVADA:")) {
            String bomba = resultado.split(":")[1];
            vista.mostrarMensaje("¡Ruleta activada! Obtuviste: " + bomba + ".");
            ejecutarEfectoBomba(bomba);
        } else if (resultado.startsWith("DESHABILITADA:")) {
            String bomba = resultado.split(":")[1];
            vista.mostrarMensaje("Salió " + bomba + ", pero YA HABÍA SIDO ACTIVADA previamente. Pierdes la bomba en este turno.");
        } else if (resultado.equals("NINGUNA")) {
            vista.mostrarMensaje("Salió 'NINGUNA'. No obtuviste ningún poder en esta tirada.");
        }
    }

    private void ejecutarEfectoBomba(String tipoBomba) {
        ArrayList<Casilla> disponibles = tableroMaquina.obtenerCasillasDisponibles();
        if (disponibles.isEmpty()) return;

        Collections.shuffle(disponibles);

        ArrayList<Casilla> objetivoAtaque = new ArrayList<>();
        int cantidadImpactos = 0;

        switch (tipoBomba) {
            case "BOMBA_1":
                cantidadImpactos = 2;
                break;
            case "BOMBA_2":
                cantidadImpactos = 3;
                break;
            case "BOMBA_3":
                cantidadImpactos = 4;
                break;
        }

        for (int i = 0; i < Math.min(cantidadImpactos, disponibles.size()); i++) {
            objetivoAtaque.add(disponibles.get(i));
        }

        tableroMaquina.aplicarAtaqueArea(objetivoAtaque);
        vista.mostrarMensaje("¡La bomba impactó aleatoriamente en " + objetivoAtaque.size() + " casillas!");

        ArrayList<Barco> hundidosNotificados = new ArrayList<>();
        for (Casilla c : objetivoAtaque) {
            if (c.tieneBarco() && c.getBarco() != null) {
                Barco b = c.getBarco();
                if (b.estaHundido() && !hundidosNotificados.contains(b)) {
                    hundidosNotificados.add(b);
                    vista.mostrarMensaje("💥 ¡LA BOMBA HA HUNDIDO EL " + b.getNombre().toUpperCase() + " ENEMIGO!");
                    vista.mostrarMensaje("BARCOS RESTANTES ENEMIGOS: " + tableroMaquina.obtenerBarcosRestantes());
                }
            }
        }
    }

    private void ejecutarTurnoMaquina() {
        ArrayList<Casilla> disponibles = tableroJugador.obtenerCasillasDisponibles();
        if (!disponibles.isEmpty()) {
            Casilla objetivo = disponibles.get(random.nextInt(disponibles.size()));
            objetivo.marcarAtacada();
            char letraCol = (char) ('A' + objetivo.getColumna());

            if (objetivo.tieneBarco()) {
                Barco barcoImpactado = objetivo.getBarco();
                if (barcoImpactado != null && barcoImpactado.estaHundido()) {
                    System.out.println("\n[MÁQUINA]: Atacó la posición (" + objetivo.getFila() + ", " + letraCol + ") ¡Y HUNDIÓ TU " + barcoImpactado.getNombre().toUpperCase() + "! 💥");
                    System.out.println("[SISTEMA]: TUS BARCOS RESTANTES: " + tableroJugador.obtenerBarcosRestantes());
                } else {
                    System.out.println("\n[MÁQUINA]: Atacó la posición (" + objetivo.getFila() + ", " + letraCol + ") ¡Y dio en el blanco!");
                }
            } else {
                System.out.println("\n[MÁQUINA]: Atacó la posición (" + objetivo.getFila() + ", " + letraCol + ") y cayó en agua.");
            }
        }
    }
}