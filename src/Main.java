import TDAs.ABB;
import model.Deposito;

public class Main {

    public static void main(String[] args) {

        ABB arbol = new ABB();

        // Agregar depósitos
        arbol.insertar(new Deposito(10));
        arbol.insertar(new Deposito(5));
        arbol.insertar(new Deposito(15));
        arbol.insertar(new Deposito(3));
        arbol.insertar(new Deposito(7));
        arbol.insertar(new Deposito(20));

        // Reporte por niveles
        System.out.println("=== REPORTE POR NIVELES ===");
        //arbol.imprimirTodosLosNiveles();

        // Auditoría: marca los depósitos no auditados en los últimos 30 días
        // (todos, porque fechaUltimaAuditoria es null en todos)
        System.out.println("\n=== AUDITORÍA ===");
        //arbol.auditarPendientes();
    }
}