import java.util.ArrayList;
import java.util.stream.Collectors;

public class Tablero {
    private final int filas = 10;
    private final int columnas = 10;
    private final ArrayList<Casilla> listaCasillas = new ArrayList<>();
    private final ArrayList<Barco> flota = new ArrayList<>();

    // Constantes de colores ANSI
    public static final String RESET = "\u001B[0m";
    public static final String COLOR_AZUL = "\u001B[34m";    // Para letras A-J y números 0-9
    public static final String COLOR_NEGRO = "\u001B[30m";   // Para las llaves del agua [ ] (Gris oscuro / Negro brillante)
    public static final String COLOR_IMPACTO = "\u001B[31m"; // Rojo para impactos [X]
    public static final String COLOR_FALLO = "\u001B[37m";   // Blanco para agua atacada [O]

    public Tablero() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                listaCasillas.add(new Casilla(i, j));
            }
        }
    }

    public Casilla getCasilla(int fila, int columna) {
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            return null;
        }
        return listaCasillas.get(fila * columnas + columna);
    }

    public ArrayList<Barco> getFlota() { return flota; }

    public long obtenerBarcosRestantes() {
        return flota.stream().filter(b -> !b.estaHundido()).count();
    }

    public void aplicarAtaqueArea(ArrayList<Casilla> casillasObjetivo) {
        casillasObjetivo.forEach(Casilla::marcarAtacada);
    }

    public ArrayList<Casilla> obtenerCasillasDisponibles() {
        return listaCasillas.stream()
                .filter(c -> !c.fueAtacada())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean todaLaFlotaHundida() {
        return !flota.isEmpty() && flota.stream().allMatch(Barco::estaHundido);
    }

    public void mostrarTablero(boolean ocultarBarcos) {
        System.out.print("     ");
        for (int j = 0; j < columnas; j++) {
            char letraColumna = (char) ('A' + j);
            System.out.print("  " + COLOR_AZUL + letraColumna + RESET + "  ");
        }
        System.out.println();

        for (int i = 0; i < filas; i++) {
            System.out.printf(COLOR_AZUL + "%2d" + RESET + " | ", i);
            for (int j = 0; j < columnas; j++) {
                Casilla c = getCasilla(i, j);
                if (c != null) {
                    if (c.fueAtacada()) {
                        if (c.tieneBarco()) {
                            System.out.print(COLOR_IMPACTO + " [X] " + RESET);
                        } else {
                            System.out.print(COLOR_FALLO + " [O] " + RESET);
                        }
                    } else if (!ocultarBarcos && c.tieneBarco()) {
                        Barco b = c.getBarco();
                        String colorBarco = (b != null) ? b.getColor() : RESET;
                        String simbolo = (b != null) ? b.getSimbolo() : "B";
                        System.out.print(colorBarco + " [" + simbolo + "] " + RESET);
                    } else {
                        System.out.print(COLOR_NEGRO + " [ ] " + RESET);
                    }
                }
            }
            System.out.println();
        }
    }
}