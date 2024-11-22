import java.util.Scanner;

public class Fisica {

    private static final String MSG_VELOCIDAD_INICIAL = "Ingrese la velocidad inicial (m/s): ";
    private static final String MSG_SENTIDO = "Ingrese el sentido (izquierda/derecha/arriba/abajo): ";
    private static final String MSG_FUERZA = "Ingrese la magnitud de la fuerza (%s) (N): ";
    private static final String MSG_MASA = "Ingrese la masa del objeto (kg): ";
    private static final String MSG_TIEMPO = "Ingrese el tiempo (s): ";
    private static final String MSG_COEF_FRICCION_ESTATICO = "Ingrese el coeficiente de fricción estática (μs): ";
    private static final String MSG_COEF_FRICCION_CINETICO = "Ingrese el coeficiente de fricción cinético (μk): ";
    private static final double GRAVEDAD = 9.81;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // Entrada de datos
            double velocidadInicial = solicitarDato(scanner, MSG_VELOCIDAD_INICIAL);
            String sentidoVelocidad = solicitarSentido(scanner, MSG_SENTIDO);

            double masa = solicitarDato(scanner, MSG_MASA);
            double tiempo = solicitarDato(scanner, MSG_TIEMPO);

            double coefFriccionEstatico = solicitarDato(scanner, MSG_COEF_FRICCION_ESTATICO);
            double coefFriccionCinetico = solicitarDato(scanner, MSG_COEF_FRICCION_CINETICO);

            // Cálculo de fuerzas
            double[] fuerzaAplicada = solicitarFuerzas(scanner, "aplicada");

            // Cálculos relacionados con el peso y la fuerza normal
            double peso = masa * GRAVEDAD;
            double fuerzaNormal = calcularFuerzaNormal(fuerzaAplicada[1], peso);

            // Rozamiento máximo y cinético
            double fuerzaRozamientoMaxima = coefFriccionEstatico * fuerzaNormal;
            double fuerzaRozamientoCinetica = coefFriccionCinetico * fuerzaNormal;

            // Cálculo de fuerzas netas
            double fuerzaNetaX = fuerzaAplicada[0]; // Fuerzas en X determinan el movimiento
            double fuerzaRozamientoX = Math.abs(fuerzaNetaX) <= fuerzaRozamientoMaxima ? 0 : fuerzaRozamientoCinetica;

            // Verificar si el objeto se mueve
            if (Math.abs(fuerzaNetaX) <= fuerzaRozamientoMaxima) {
                System.out.println("El objeto no se mueve. Fuerza neta insuficiente para superar la fricción estática.");
                return;
            }

            double fuerzaEfectivaX = fuerzaNetaX - Math.signum(fuerzaNetaX) * fuerzaRozamientoX;

            // Aceleración
            double aceleracionX = fuerzaEfectivaX / masa;

            // Verificar tiempo y calcular resultados
            double desplazamientoX = 0;
            double velocidadFinalX = 0;

            if (tiempo > 0) {
                desplazamientoX = velocidadInicial * tiempo + 0.5 * aceleracionX * tiempo * tiempo;
                velocidadFinalX = velocidadInicial + aceleracionX * tiempo;
            } else {
                System.out.println("Advertencia: El tiempo ingresado es 0. No se calculan desplazamientos ni velocidades finales.");
            }

            // Mostrar resultados
            mostrarResultados(peso, fuerzaNormal, fuerzaRozamientoMaxima, fuerzaRozamientoCinetica, fuerzaNetaX, aceleracionX, desplazamientoX, velocidadFinalX, tiempo);

        } catch (Exception e) {
            System.out.println("Error: Entrada no válida. Por favor, inténtelo de nuevo.");
        } finally {
            scanner.close();
        }
    }

    private static double solicitarDato(Scanner scanner, String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return scanner.nextDouble();
            } catch (Exception e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.next(); // Limpiar buffer
            }
        }
    }

    private static String solicitarSentido(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String sentido = scanner.next().toLowerCase();
            if (sentido.equals("izquierda") || sentido.equals("derecha") || sentido.equals("arriba") || sentido.equals("abajo")) {
                return sentido;
            }
            System.out.println("Dirección inválida. Intente de nuevo (izquierda/derecha/arriba/abajo).");
        }
    }

    private static double[] solicitarFuerzas(Scanner scanner, String tipoFuerza) {
        double fuerzaX = 0, fuerzaY = 0;
        while (true) {
            double magnitud = solicitarDato(scanner, String.format(MSG_FUERZA, tipoFuerza));
            String direccion = solicitarSentido(scanner, MSG_SENTIDO);

            if (direccion.equals("izquierda")) {
                fuerzaX -= magnitud;
            } else if (direccion.equals("derecha")) {
                fuerzaX += magnitud;
            } else if (direccion.equals("arriba")) {
                fuerzaY -= magnitud; // Arriba reduce la fuerza normal
            } else if (direccion.equals("abajo")) {
                fuerzaY += magnitud; // Abajo aumenta la fuerza normal
            }

            System.out.print("¿Desea ingresar otra fuerza " + tipoFuerza + "? (s/n): ");
            if (scanner.next().equalsIgnoreCase("n")) break;
        }
        return new double[]{fuerzaX, fuerzaY};
    }

    private static double calcularFuerzaNormal(double fuerzaVertical, double peso) {
        return peso + fuerzaVertical; // Suma vertical para determinar la normal
    }

    private static void mostrarResultados(double peso, double fuerzaNormal, double fuerzaRozamientoMaxima, double fuerzaRozamientoCinetica,
                                          double fuerzaNetaX, double aceleracionX, double desplazamientoX, double velocidadFinalX, double tiempo) {
        System.out.println("\n--- Resultados ---");
        System.out.printf("Peso: %.2f N%n", peso);
        System.out.printf("Fuerza normal: %.2f N%n", fuerzaNormal);
        System.out.printf("Fuerza de rozamiento máxima: %.2f N%n", fuerzaRozamientoMaxima);
        System.out.printf("Fuerza de rozamiento cinético: %.2f N%n", fuerzaRozamientoCinetica);
        System.out.printf("Fuerza neta en X: %.2f N%n", fuerzaNetaX);
        System.out.printf("Aceleración en X: %.2f m/s²%n", aceleracionX);
        if (tiempo > 0) {
            System.out.printf("Desplazamiento en X: %.2f m%n", desplazamientoX);
            System.out.printf("Velocidad final en X: %.2f m/s%n", velocidadFinalX);
        }
    }
}
