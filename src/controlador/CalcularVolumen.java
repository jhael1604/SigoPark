package controlador;

public class CalcularVolumen {
    public static double calcularVolumen(modelo.enums.tipoVehiculo tipo){
        // Dimensiones aproximada de vehiculos.
        double longitud, ancho, alturaFrente, alturaMedio, alturaTrasera;
        
        switch(tipo){
            case AUTO -> {
                longitud = 4.5;
                ancho = 1.8;
                alturaFrente = 1.2;
                alturaMedio = 1.5;
                alturaTrasera = 1.3;
            }
            case MOTO -> {
                longitud = 2.0;
                ancho = 0.8;
                alturaFrente = 1.1;
                alturaMedio = 1.2;
                alturaTrasera = 1.0;
            
            }
            default -> {
                longitud = 4.0;
                ancho = 1.8;
                alturaFrente = 1.3;
                alturaMedio = 1.5;
                alturaTrasera = 1.3;
            }
        }
        //Puntos de integracion (secciones del vehiculo)
        //A(x) = ancho * altura en cada uno
        double[] alturas = {alturaFrente, alturaMedio, alturaTrasera};
        double[] posiciones = {0, longitud / 2, longitud};
        //Metodo del trapecio compuesto
        double volumen = integralTrapecio(posiciones, alturas, ancho);
        return volumen;
    }
    
    public static double integralTrapecio(double[] x, double[] alturas, double ancho){
        double volumen = 0.0;
        for(int i = 0; i< x.length - 1; i++){
            double dx = x[i + 1] - x[i];
            double areaIzq = ancho * alturas[i];
            double areaDer = ancho * alturas[i + 1];
            //Area del trapecio * longitud del segmento
            volumen += (areaIzq + areaDer) / 2.0 * dx;
        }
        return volumen;
    }
    public static double calcularVolumenTotal(java.util.List<modelo.parqueo.Vehiculo> vehiculo){
        double total = 0.0;
        for(modelo.parqueo.Vehiculo v : vehiculo){
            total += calcularVolumen(v.getTipo());
        }
        return total;
    }
    public static String getDetalle(modelo.enums.tipoVehiculo tipo){
        double volumen = calcularVolumen(tipo);
        return String.format("%.2f m", volumen);
    }
}
