package TDAs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ABB<T extends Comparable<T>> {

    private Nodo<T> raiz;

    public ABB() {
        raiz = null;
    }

    // Recorridos

    private void preorden(Nodo<T> nodo) {
        if (nodo == null) return;
        System.out.print(nodo.elemento + "  ");  // raíz → izq → der
        preorden(nodo.izq);
        preorden(nodo.der);
    }

    private void inorden(Nodo<T> nodo) {
        if (nodo == null) return;
        inorden(nodo.izq);
        System.out.print(nodo.elemento + "  ");  // izq → raíz → der
        inorden(nodo.der);
    }

    private void postorden(Nodo<T> nodo) {
        if (nodo == null) return;
        postorden(nodo.izq);
        postorden(nodo.der);
        System.out.print(nodo.elemento + "  ");  // izq → der → raíz
    }

    // Arbol vacio
    public boolean esVacio() {
        return raiz == null;
    }

    // get Raiz
    public T obtenerRaiz() {
        if (raiz == null) return null;
        return raiz.elemento;
    }

    // ---------------------------------------------------------------
    // AUDITORÍA — recorrido post-orden
    //
    // Marca visitado=true en los nodos que NO fueron auditados en los
    // últimos 30 días (es decir, fechaUltimaAuditoria es null, o está
    // a más de 30 días del momento actual).
    // ---------------------------------------------------------------

    public void auditarPendientes() {
        LocalDateTime hace30Dias = LocalDateTime.now().minusDays(30);
        auditarPostOrden(raiz, hace30Dias);
    }

    private void auditarPostOrden(Nodo<T> nodo, LocalDateTime umbral) {
        if (nodo == null) return;

        // Post-orden: izquierda → derecha → raíz
        auditarPostOrden(nodo.izq, umbral);
        auditarPostOrden(nodo.der, umbral);

        boolean necesitaAuditoria =
                nodo.fechaUltimaAuditoria == null ||
                        nodo.fechaUltimaAuditoria.isBefore(umbral);

        if (necesitaAuditoria) {
            nodo.visitado = true;
            System.out.println("[AUDITORÍA] Depósito marcado: " + nodo.elemento);
        }
    }

    // ---------------------------------------------------------------
    // REPORTE POR NIVELES
    //
    // Imprime los nodos del nivel N (0 = raíz).
    // ---------------------------------------------------------------

    public void imprimirNivel(int nivel) {
        List<Nodo<T>> nodosEnNivel = new ArrayList<>();
        recolectarNivel(raiz, nivel, 0, nodosEnNivel);

        if (nodosEnNivel.isEmpty()) {
            System.out.println("Nivel " + nivel + ": (vacío)");
        } else {
            System.out.print("Nivel " + nivel + ": ");
            for (Nodo<T> n : nodosEnNivel) {
                System.out.print(n.elemento + "  ");
            }
            System.out.println();
        }
    }

    private void recolectarNivel(Nodo<T> nodo, int nivelBuscado,
                                 int nivelActual, List<Nodo<T>> lista) {
        if (nodo == null) return;
        if (nivelActual == nivelBuscado) {
            lista.add(nodo);
            return;
        }
        recolectarNivel(nodo.izq, nivelBuscado, nivelActual + 1, lista);
        recolectarNivel(nodo.der, nivelBuscado, nivelActual + 1, lista);
    }

    /** Imprime todos los niveles del árbol de corrido. */
    public void imprimirTodosLosNiveles() {
        int h = altura(raiz);
        for (int i = 0; i < h; i++) {
            imprimirNivel(i);
        }
    }

    // ---------------------------------------------------------------
    // EJERCICIOS
    // ---------------------------------------------------------------
    /*
    [ABB] A1. Recorrido in-order filtrado 
    Implementá un método que recorra el ABB en in-order e imprima solo los depósitos con visitado = false. 
    Nota: Los métodos deben ser eficientes. Justificá la complejidad temporal.
    */
    private void inordenNoVisitados(Nodo<T> nodo) {
        if (nodo == null) return;
        inordenNoVisitados(nodo.izq);
        if (!nodo.visitado) {
            System.out.print(nodo.elemento + "  ");
        }
        inordenNoVisitados(nodo.der);
    }

    /*
    [ABB] A2. Inserción en el ABB 
    Implementá insertar(Deposito d) usando el ID como clave de ordenamiento. 
    Nota: Indicá la complejidad en caso promedio y peor caso.
    */
    public void insertar(T elem) {
        raiz = insertar(raiz, elem);
    }

    private Nodo<T> insertar(Nodo<T> nodo, T elem) {
        if (nodo == null) return new Nodo<>(elem);

        int cmp = elem.compareTo(nodo.elemento);
        if (cmp < 0) {
            nodo.izq = insertar(nodo.izq, elem);
        }
        else if (cmp > 0) {
            nodo.der = insertar(nodo.der, elem);
        }
        return nodo;
    }

    /*
    [ABB] A3. Búsqueda por ID 
    Implementá buscar(int idDeposito) que retorne el nodo o null si no existe. 
    Nota: ¿Qué ventaja ofrece el ABB respecto a una lista enlazada?
    */
    public boolean buscar(T elem) {
        return buscar(raiz, elem);
    }

    private boolean buscar(Nodo<T> nodo, T elem) {
        if (nodo == null) return false;
        int cmp = elem.compareTo(nodo.elemento);
        if (cmp == 0) return true;
        if (cmp < 0) return buscar(nodo.izq, elem);
        return buscar(nodo.der, elem);
    }

    /*
    [ABB] A4. Auditoría post-orden 
    Implementá auditarDepositos() que recorra en post-orden y marque visitado = true en depósitos no auditados en los últimos 30 días. 
    Nota: Usá LocalDateTime. Justificá el uso de post-orden.
    */
    public void auditarDepositos() {
        auditarDepositos(raiz);
    }

    private void auditarDepositos(Nodo<T> nodo) {
        if (nodo == null) return;

        // Post-orden: izquierda → derecha → raíz
        auditarDepositos(nodo.izq);
        auditarDepositos(nodo.der);

        boolean necesitaAuditoria =
                nodo.fechaUltimaAuditoria == null ||
                        nodo.fechaUltimaAuditoria.isBefore(LocalDateTime.now().minusDays(30));

        if (necesitaAuditoria) {
            nodo.visitado = true;
            System.out.println("[AUDITORÍA] Depósito marcado: " + nodo.elemento);
        }
    }

    /*
    [ABB] A5. Depósitos en nivel N 
    Implementá imprimirNivel(int n) que imprima los IDs de todos los depósitos en el nivel N (raíz = nivel 0).
    Nota: ¿Qué estructura auxiliar es útil para este recorrido?
    */
    private void imprimirNivel(Nodo<T> nodo, int n, int nivelActual) {
        if (nodo == null) return;

        if (nivelActual == n) {
            System.out.println(nodo.elemento);
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
    public void eliminar(T elem) {
        raiz = eliminar(raiz, elem);
    }

    private Nodo<T> eliminar(Nodo<T> nodo, T elem) {
        if (nodo == null) return null; // no existe

        int cmp = elem.compareTo(nodo.elemento);
        if (cmp < 0) {
            nodo.izq = eliminar(nodo.izq, elem);
        } else if (cmp > 0) {
            nodo.der = eliminar(nodo.der, elem);
        } else {
            // Caso 1: nodo hoja o con un solo hijo
            if (nodo.izq == null) return nodo.der;
            if (nodo.der == null) return nodo.izq;

            // Caso 2: nodo con dos hijos
            // Se reemplaza con el mínimo del subárbol derecho
            nodo.elemento = obtenerMinimo(nodo.der);
            nodo.der = eliminar(nodo.der, nodo.elemento);
        }
        return nodo;
    }

    /*
    [ABB] A7. Verificar balance 
    Implementá estaBalanceado() que retorne true si la diferencia de altura entre subárboles de cada nodo es <= 1. 
    Nota: ¿Cuál es la complejidad? ¿Se puede mejorar?
    */
    private boolean estaBalanceado(Nodo<T> nodo) {
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

    private T obtenerMinimo(Nodo<T> nodo) {
        if (nodo == null) return null;
        if (nodo.izq == null) return nodo.elemento;

        return obtenerMinimo(nodo.izq);
    }

    private T obtenerMaximo(Nodo<T> nodo) {
        if (nodo == null) return null;
        if (nodo.der == null) return nodo.elemento;

        return obtenerMaximo(nodo.der);
    }

    /*
    [ABB] A9. Contar depósitos sin auditar 
    Implementá contarSinAuditar() que retorne la cantidad de depósitos con fechaUltimaAuditoria > 30 días, sin modificar nodos.
    Nota: Indicá qué tipo de recorrido elegís y por qué.
    Se eligió pre-order porque permite detectar tempranamente nodos sin auditar desde la raíz, aunque para este caso particular de conteo el orden de recorrido no altera el resultado. 
    */
    private int contarSinAuditar(Nodo<T> nodo) {
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
    private int altura(Nodo<T> nodo) {
    
        if (nodo == null) return 0;

        return 1 + Math.max(altura(nodo.izq), altura(nodo.der));
    }

    // Ejercicios

    /*
    Contar cantidad de nodos.
    Contar cantidad de hojas.
    Obtener el valor máximo del árbol.
    Devolver el máximo de las hojas.
    Contar nodos cuyo valor está dentro de un rango [min, max].
    Devolver la longitud de la rama más corta desde la raíz a una hoja.
    Cambiar el nodo actual con el mayor valor de sus hijos, siempre que ambos no sean nulos.
     */

    // Contar cantidad de nodos.

    public int contarNodos(){
        return contadorNodos(raiz);
    }

    private int contadorNodos(Nodo<T> nodo) {
        if (nodo == null) return 0;
        return 1 + contadorNodos(nodo.izq) + contadorNodos(nodo.der);
    }

    // Contar cantidad de hojas

    public int contarHojas(){
        return contarHojas(raiz);
    }

    private int contarHojas(Nodo<T> nodo) {
        // Caso base
        if (nodo == null) return 0;
        if (nodo.izq == null && nodo.der == null) return 1;
        // Caso recursivo
        return contarHojas(nodo.der) + contarHojas(nodo.izq);
    }

    // Devolver el máximo de las hojas


    // Contar nodos cuyo valor está dentro de un rango [min, max].

    // Devolver la longitud de la rama más corta desde la raíz a una hoja.

    // Cambiar el nodo actual con el mayor valor de sus hijos, siempre que ambos no sean nulos.

}

