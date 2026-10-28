import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GestorBombas {
    private int intentosRestantes = 3;
    private final ArrayList<String> poolBombas = new ArrayList<>(Arrays.asList("BOMBA_1", "BOMBA_2", "BOMBA_3", "NINGUNA"));
    private final ArrayList<String> bombasUsadas = new ArrayList<>();
    private final Random random = new Random();

    public String tirarRuletaBomba() {
        if (intentosRestantes <= 0) {
            return "SIN_INTENTOS";
        }

        intentosRestantes--;
        String seleccion = poolBombas.get(random.nextInt(poolBombas.size()));

        if (seleccion.equals("NINGUNA")) {
            return "NINGUNA";
        }

        if (bombasUsadas.contains(seleccion)) {
            return "DESHABILITADA:" + seleccion;
        }

        bombasUsadas.add(seleccion);
        return "ACTIVADA:" + seleccion;
    }

    public int getIntentosRestantes() {
        return intentosRestantes;
    }
}