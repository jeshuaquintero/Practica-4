import java.util.Scanner;

public class VistaJuego {
    private final Scanner scanner = new Scanner(System.in);

    public int mostrarMenuPosicionamiento() {
        int opcion = -1;
        while (opcion != 1 && opcion != 2) {
            System.out.println("\n==================================");
            System.out.println("     CONFIGURACIÓN DE FLOTA       ");
            System.out.println("==================================");
            System.out.println("1. Posicionar barcos manualmente");
            System.out.println("2. Posicionar barcos aleatoriamente");
            System.out.print("Seleccione una opción (1 o 2): ");
            opcion = pedirEnteroValido();
            if (opcion != 1 && opcion != 2) {
                System.out.println("Opción inválida. Ingrese 1 o 2.");
            }
        }
        return opcion;
    }

    public int mostrarMenuTurno(int intentosBomba) {
        int opcion = -1;
        while (opcion != 1 && opcion != 2) {
            System.out.println("\n----------------------------------");
            System.out.println("          OPCIONES DE TURNO       ");
            System.out.println("----------------------------------");
            System.out.println("1. Disparo normal");
            if (intentosBomba > 0) {
                System.out.println("2. Activar bomba (Aleatorio) [Intentos restantes: " + intentosBomba + "]");
            } else {
                System.out.println("2. [Activar bomba DESACTIVADA - Sin intentos]");
            }
            System.out.print("Seleccione una acción (1 o 2): ");
            opcion = pedirEnteroValido();
            if (opcion != 1 && opcion != 2) {
                System.out.println("Opción inválida. Ingrese 1 o 2.");
            }
        }
        return opcion;
    }

    public void mostrarTablerosParalelos(Tablero jugador, Tablero maquina) {
        System.out.println("\n                  TU TABLERO                                              TABLERO ENEMIGO");

        // Encabezado de letras para el jugador (en color AZUL)
        System.out.print("     ");
        for (int j = 0; j < 10; j++) {
            System.out.print("  " + Tablero.COLOR_AZUL + (char)('A' + j) + Tablero.RESET + "  ");
        }

        // Separador central
        System.out.print("               ");

        // Encabezado de letras para el enemigo (en color AZUL)
        for (int j = 0; j < 10; j++) {
            System.out.print("  " + Tablero.COLOR_AZUL + (char)('A' + j) + Tablero.RESET + "  ");
        }
        System.out.println();

        for (int i = 0; i < 10; i++) {
            // --- Tablero Jugador (Número en AZUL) ---
            System.out.printf(Tablero.COLOR_AZUL + "%2d" + Tablero.RESET + " | ", i);
            for (int j = 0; j < 10; j++) {
                imprimirCasillaFormateada(jugador.getCasilla(i, j), false);
            }

            // Separador central entre ambos tableros
            System.out.print("    ||    ");

            // --- Tablero Máquina (Número en AZUL) ---
            System.out.printf(Tablero.COLOR_AZUL + "%2d" + Tablero.RESET + " | ", i);
            for (int j = 0; j < 10; j++) {
                imprimirCasillaFormateada(maquina.getCasilla(i, j), true);
            }

            System.out.println();
        }
    }

    private void imprimirCasillaFormateada(Casilla c, boolean ocultarBarcos) {
        if (c != null) {
            if (c.fueAtacada()) {
                if (c.tieneBarco()) {
                    System.out.print(Tablero.COLOR_IMPACTO + " [X] " + Tablero.RESET);
                } else {
                    System.out.print(Tablero.COLOR_FALLO + " [O] " + Tablero.RESET);
                }
            } else if (!ocultarBarcos && c.tieneBarco()) {
                Barco b = c.getBarco();
                String colorBarco = (b != null) ? b.getColor() : Tablero.RESET;
                String simbolo = (b != null) ? b.getSimbolo() : "B";
                System.out.print(colorBarco + " [" + simbolo + "] " + Tablero.RESET);
            } else {
                // Llaves del agua en color NEGRO / GRIS OSCURO
                System.out.print(Tablero.COLOR_NEGRO + " [ ] " + Tablero.RESET);
            }
        }
    }

    public int[] pedirCoordenadas() {
        int f = -1;
        while (f < 0 || f > 9) {
            System.out.print("Ingrese fila (0-9): ");
            f = pedirEnteroValido();
            if (f < 0 || f > 9) {
                System.out.println("Fila inválida. Debe ser un número entre 0 y 9.");
            }
        }

        int c = -1;
        while (c < 0 || c > 9) {
            System.out.print("Ingrese columna (A-J): ");
            String entrada = scanner.next().trim().toUpperCase();
            if (!entrada.isEmpty()) {
                char letra = entrada.charAt(0);
                c = letra - 'A';
            }
            if (c < 0 || c > 9) {
                System.out.println("Columna inválida. Ingrese una letra entre la A y la J.");
            }
        }
        return new int[]{f, c};
    }

    public int pedirOrientacion() {
        int orientacion = -1;
        while (orientacion != 1 && orientacion != 2) {
            System.out.print("Orientación (1: Horizontal, 2: Vertical): ");
            orientacion = pedirEnteroValido();
            if (orientacion != 1 && orientacion != 2) {
                System.out.println("Orientación inválida. Ingrese 1 (Horizontal) o 2 (Vertical).");
            }
        }
        return orientacion;
    }

    public void mostrarMensaje(String msg) {
        System.out.println("[SISTEMA]: " + msg);
    }

    private int pedirEnteroValido() {
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Entrada inválida. Ingrese un número: ");
        }
        return scanner.nextInt();
    }
}