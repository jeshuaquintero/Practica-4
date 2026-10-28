public class Main {
    public static void main(String[] args) {
        VistaJuego vista = new VistaJuego();
        ControladorJuego juego = new ControladorJuego(vista);
        juego.iniciarJuego();
    }
}