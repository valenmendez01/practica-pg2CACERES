package TDAs;

import model.Deposito;

import java.time.LocalDateTime;

public class ABB {

    private static class Nodo {
        Deposito deposito;
        boolean visitado = false;
        LocalDateTime fechaUltimaAuditoria = null;
        Nodo izq;
        Nodo der;

        public Nodo(Deposito deposito) {
            this.deposito = deposito;
        }
    }

    private Nodo raiz;

    public ABB() {
        raiz = null;
    }

    // Recorridos

    private void preorden(Nodo nodo) {
        if (nodo == null) return;
        System.out.print(nodo.deposito.getId() + "  ");  // raíz → izq → der
        preorden(nodo.izq);
        preorden(nodo.der);
    }

    private void inorden(Nodo nodo) {
        if (nodo == null) return;
        inorden(nodo.izq);
        System.out.print(nodo.deposito.getId() + "  ");  // izq → raíz → der
        inorden(nodo.der);
    }

    private void postorden(Nodo nodo) {
        if (nodo == null) return;
        postorden(nodo.izq);
        postorden(nodo.der);
        System.out.print(nodo.deposito.getId() + "  ");  // izq → der → raíz
    }

    // Arbol vacio
    public boolean esVacio() {
        return raiz == null;
    }

    // get Raiz
    public Deposito obtenerRaiz() {
        if (raiz == null) return null;
        return raiz.deposito;
    }

    // ---------------------------------------------------------------
    // EJERCICIOS
    // ---------------------------------------------------------------
    /*
    [ABB] A1. Recorrido in-order filtrado 
    Implementá un método que recorra el ABB en in-order e imprima solo los depósitos con visitado = false. 
    Nota: Los métodos deben ser eficientes. Justificá la complejidad temporal.
    */
    private void inordenNoVisitados(Nodo nodo) {
        if (nodo == null) return;
        inordenNoVisitados(nodo.izq);
        if (!nodo.visitado) {
            System.out.print(nodo.deposito.getId() + "  ");
        }
        inordenNoVisitados(nodo.der);
    }

    /*
    [ABB] A2. Inserción en el ABB 
    Implementá insertar(Deposito d) usando el ID como clave de ordenamiento. 
    Nota: Indicá la complejidad en caso promedio y peor caso.
    */
    public void insertar(Deposito deposito) {
        raiz = insertar(raiz, deposito);
    }

    private Nodo insertar(Nodo nodo, Deposito deposito) {
        if (nodo == null) return new Nodo(deposito);

        int cmp = deposito.compareTo(nodo.deposito);
        if (cmp < 0) {
            nodo.izq = insertar(nodo.izq, deposito);
        }
        else if (cmp > 0) {
            nodo.der = insertar(nodo.der, deposito);
        }
        return nodo;
    }

    /*
    [ABB] A3. Búsqueda por ID 
    Implementá buscar(int idDeposito) que retorne el nodo o null si no existe. 
    Nota: ¿Qué ventaja ofrece el ABB respecto a una lista enlazada?
    */
    public Nodo buscar(int idDeposito) {
        return buscar(raiz, idDeposito);
    }

    private Nodo buscar(Nodo nodo, int idDeposito) {
        if (nodo == null) return null;

        if (idDeposito == nodo.deposito.getId()) return nodo;

        if (idDeposito < nodo.deposito.getId()) {
            return buscar(nodo.izq, idDeposito);
        } else {
            return buscar(nodo.der, idDeposito);
        }
    }

    /*
    [ABB] A4. Auditoría post-orden 
    Implementá auditarDepositos() que recorra en post-orden y marque visitado = true en depósitos no auditados en los últimos 30 días. 
    Nota: Usá LocalDateTime. Justificá el uso de post-orden.
    */
    public void auditarDepositos() {
        auditarDepositos(raiz);
    }

    private void auditarDepositos(Nodo nodo) {
        if (nodo == null) return;

        // Post-orden: izquierda → derecha → raíz
        auditarDepositos(nodo.izq);
        auditarDepositos(nodo.der);

        boolean necesitaAuditoria =
                nodo.fechaUltimaAuditoria == null ||
                        nodo.fechaUltimaAuditoria.isBefore(LocalDateTime.now().minusDays(30));

        if (necesitaAuditoria) {
            nodo.visitado = true;
            System.out.println("[AUDITORÍA] Depósito marcado: " + nodo.deposito.getId());
        }
    }

    /*
    [ABB] A5. Depósitos en nivel N 
    Implementá imprimirNivel(int n) que imprima los IDs de todos los depósitos en el nivel N (raíz = nivel 0).
    Nota: ¿Qué estructura auxiliar es útil para este recorrido?
    */
    public void imprimirNivel(int n) {
        imprimirNivel(raiz, n, 0);
    }

    private void imprimirNivel(Nodo nodo, int n, int nivelActual) {
        if (nodo == null) return;

        if (nivelActual == n) {
            System.out.println(nodo.deposito.getId() + " ");
            return;
        }

        imprimirNivel(nodo.izq, n, nivelActual + 1);
        imprimirNivel(nodo.der, n, nivelActual + 1);
    }

    /*
    [ABB] A6. Eliminar un depósito 
    Implementá eliminar(int idDeposito) respetando los tres casos: hoja, un hijo, dos hijos. 
    Nota: Explicá qué sucesor/predecesor usás en el caso de dos hijos. 
    */
    public void eliminar(Deposito deposito) {
        raiz = eliminar(raiz, deposito);
    }

    private Nodo eliminar(Nodo nodo, Deposito deposito) {
        if (nodo == null) return null; // no existe

        int cmp = deposito.compareTo(nodo.deposito);
        if (cmp < 0) {
            nodo.izq = eliminar(nodo.izq, deposito);
        } else if (cmp > 0) {
            nodo.der = eliminar(nodo.der, deposito);
        } else {
            // Caso 1: nodo hoja o con un solo hijo
            if (nodo.izq == null) return nodo.der;
            if (nodo.der == null) return nodo.izq;

            // Caso 2: nodo con dos hijos
            // Se reemplaza con el mínimo del subárbol derecho
            nodo.deposito = obtenerMinimo(nodo.der);
            nodo.der = eliminar(nodo.der, nodo.deposito);
        }
        return nodo;
    }

    /*
    [ABB] A7. Verificar balance 
    Implementá estaBalanceado() que retorne true si la diferencia de altura entre subárboles de cada nodo es <= 1. 
    Nota: ¿Cuál es la complejidad? ¿Se puede mejorar?
    */
    public boolean estaBalanceado() {
        return estaBalanceado(raiz);
    }

    private boolean estaBalanceado(Nodo nodo) {
        if (nodo == null) return true;

        int alturaIzq = altura(nodo.izq);
        int alturaDer = altura(nodo.der);

        if (Math.abs(alturaIzq - alturaDer) > 1) return false;

        return estaBalanceado(nodo.izq) && estaBalanceado(nodo.der);
    }

    /*
    [ABB] A8. Mínimo y máximo 
    Implementá obtenerMinimo() y obtenerMaximo() sin recorrer todo el árbol. 
    Nota: ¿Cuál es la complejidad? ¿Por qué no es necesario visitar todos los nodos?
    */

    private Deposito obtenerMinimo(Nodo nodo) {
        if (nodo == null) return null;
        if (nodo.izq == null) return nodo.deposito;

        return obtenerMinimo(nodo.izq);
    }

    private Deposito obtenerMaximo(Nodo nodo) {
        if (nodo == null) return null;
        if (nodo.der == null) return nodo.deposito;

        return obtenerMaximo(nodo.der);
    }

    /*
    [ABB] A9. Contar depósitos sin auditar 
    Implementá contarSinAuditar() que retorne la cantidad de depósitos con fechaUltimaAuditoria > 30 días, sin modificar nodos.
    Nota: Indicá qué tipo de recorrido elegís y por qué.
    Se eligió pre-order porque permite detectar tempranamente nodos sin auditar desde la raíz, aunque para este caso particular de conteo el orden de recorrido no altera el resultado. 
    */
    private int contarSinAuditar(Nodo nodo) {
        if (nodo == null) return 0;

        boolean necesitaAuditoria =
                nodo.fechaUltimaAuditoria == null ||
                        nodo.fechaUltimaAuditoria.isBefore(LocalDateTime.now().minusDays(30));

        int cuenta = necesitaAuditoria ? 1 : 0;

        return cuenta + contarSinAuditar(nodo.izq) + contarSinAuditar(nodo.der);
    }

    /*
    [ABB] A10. Altura del árbol
    Implementá altura() que retorne la altura del ABB. Aclará la convención para árbol vacío.
    Nota: ¿Cómo se relaciona la altura con la eficiencia de las operaciones?
    A mayor altura, más lentas las operaciones.
    Porque cada búsqueda/inserción/eliminación recorre la altura del árbol de arriba a abajo. Si está balanceado son ~log₂(n) pasos, si está torcido (degenerado) son hasta n pasos.
     */
    private int altura(Nodo nodo) {
    
        if (nodo == null) return 0;

        return 1 + Math.max(altura(nodo.izq), altura(nodo.der));
    }

}

