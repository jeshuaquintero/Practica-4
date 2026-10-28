import java.util.ArrayList;

public class Barco {
    private final String nombre;
    private final String simbolo;
    private final String color;
    private final ArrayList<Casilla> casillas;

    public Barco(String nombre, String simbolo, String color) {
        this.nombre = nombre;
        this.simbolo = simbolo;
        this.color = color;
        this.casillas = new ArrayList<>();
    }

    public void agregarCasilla(Casilla casilla) {
        this.casillas.add(casilla);
        casilla.setBarco(this);
    }

    public String getNombre() { return nombre; }
    public String getSimbolo() { return simbolo; }
    public String getColor() { return color; }
    public ArrayList<Casilla> getCasillas() { return casillas; }

    // LAMBDA 1: Comprueba si todas las casillas del barco fueron atacadas
    public boolean estaHundido() {
        return casillas.stream().allMatch(Casilla::fueAtacada);
    }
}