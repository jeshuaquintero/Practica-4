public class Casilla {
    private final int fila;
    private final int columna;
    private boolean atacada;
    private boolean tieneBarco;
    private Barco barco;

    public Casilla(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.atacada = false;
        this.tieneBarco = false;
        this.barco = null;
    }

    public int getFila() { return fila; }
    public int getColumna() { return columna; }
    public boolean fueAtacada() { return atacada; }
    public void marcarAtacada() { this.atacada = true; }
    public boolean tieneBarco() { return tieneBarco; }

    public void setBarco(Barco barco) {
        this.barco = barco;
        this.tieneBarco = (barco != null);
    }

    public Barco getBarco() { return barco; }
}