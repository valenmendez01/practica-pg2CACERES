package TDAs;

import model.Deposito;

 // Cada vértice representa un Deposito (identificado por su id).
 // Cada arista representa una ruta con una distancia (peso) entre dos depósitos.

public class GrafoDepositos {

    // Clases internas de la estructura

    private static class NodoGrafo {
        Deposito deposito;
        NodoArista arista;
        NodoGrafo sigNodo;
    }

    private static class NodoArista {
        int distancia;          // peso de la ruta (distancia en km, etc.)
        NodoGrafo nodoDestino;
        NodoArista sigArista;
    }

    // Inicialización

    private NodoGrafo origen; // referencia al primer vértice de la lista de vértices

    public GrafoDepositos() {
        origen = null;
    }

    // Operaciones sobre vértices

    public void agregarDeposito(Deposito dep) {
        NodoGrafo aux = new NodoGrafo();
        aux.deposito = dep;
        aux.arista = null;
        aux.sigNodo = origen;
        origen = aux;
    }

    /**
     * Elimina el vértice correspondiente al depósito con el id dado,
     * y también elimina todas las aristas que apunten hacia él.
     */
    public void eliminarDeposito(int idDeposito) {
        // Caso especial: el vértice a eliminar es el primero
        if (origen != null && origen.deposito.getId() == idDeposito)
            origen = origen.sigNodo;

        NodoGrafo aux = origen;
        while (aux != null) {
            // Elimina toda arista que apunte al depósito eliminado
            eliminarAristaNodo(aux, idDeposito);
            // Elimina el nodo de la lista si es el siguiente
            if (aux.sigNodo != null && aux.sigNodo.deposito.getId() == idDeposito)
                aux.sigNodo = aux.sigNodo.sigNodo;
            aux = aux.sigNodo;
        }
    }

    /**
     * Devuelve true si existe un vértice con ese id de depósito.
     */
    public boolean existeDeposito(int idDeposito) {
        return dep2Nodo(idDeposito) != null;
    }

    // ---------------------------------------------------------------
    // Operaciones sobre aristas (rutas)
    // ---------------------------------------------------------------

    /**
     * Agrega una ruta (arista) entre dos depósitos con una distancia dada.
     * Grafo no dirigido: se agrega en ambas direcciones.
     */
    public void agregarRuta(int idOrigen, int idDestino, int distancia) {
        NodoGrafo n1 = dep2Nodo(idOrigen);
        NodoGrafo n2 = dep2Nodo(idDestino);

        if (n1 == null || n2 == null)
            throw new IllegalArgumentException("Uno o ambos depósitos no existen en el grafo.");

        insertarArista(n1, n2, distancia);
        insertarArista(n2, n1, distancia); // no dirigido
    }

    /**
     * Elimina la ruta entre dos depósitos (en ambas direcciones).
     */
    public void eliminarRuta(int idOrigen, int idDestino) {
        NodoGrafo n1 = dep2Nodo(idOrigen);
        NodoGrafo n2 = dep2Nodo(idDestino);
        if (n1 != null) eliminarAristaNodo(n1, idDestino);
        if (n2 != null) eliminarAristaNodo(n2, idOrigen);
    }

    /**
     * Devuelve true si existe una ruta directa entre los dos depósitos.
     */
    public boolean existeRuta(int idOrigen, int idDestino) {
        NodoGrafo n1 = dep2Nodo(idOrigen);
        if (n1 == null) return false;
        NodoArista aux = n1.arista;
        while (aux != null && aux.nodoDestino.deposito.getId() != idDestino)
            aux = aux.sigArista;
        return aux != null;
    }

    /**
     * Devuelve la distancia de la ruta entre dos depósitos.
     * Lanza excepción si no existe la ruta.
     */
    public int distanciaRuta(int idOrigen, int idDestino) {
        NodoGrafo n1 = dep2Nodo(idOrigen);
        if (n1 == null)
            throw new IllegalArgumentException("Depósito origen no existe.");
        NodoArista aux = n1.arista;
        while (aux != null && aux.nodoDestino.deposito.getId() != idDestino)
            aux = aux.sigArista;
        if (aux == null)
            throw new IllegalArgumentException("No existe ruta entre " + idOrigen + " y " + idDestino);
        return aux.distancia;
    }

    // ---------------------------------------------------------------
    // Consultas generales
    // ---------------------------------------------------------------

    /**
     * Imprime por consola todos los depósitos y sus rutas directas.
     */
    public void mostrarGrafo() {
        NodoGrafo nodo = origen;
        while (nodo != null) {
            System.out.print(nodo.deposito + " -> ");
            NodoArista arista = nodo.arista;
            while (arista != null) {
                System.out.print("[" + arista.nodoDestino.deposito + ", dist=" + arista.distancia + "] ");
                arista = arista.sigArista;
            }
            System.out.println();
            nodo = nodo.sigNodo;
        }
    }

    // ---------------------------------------------------------------
    // Métodos privados auxiliares
    // ---------------------------------------------------------------

    /**
     * Busca y devuelve el NodoGrafo correspondiente al id de depósito dado.
     * Devuelve null si no existe.
     */
    private NodoGrafo dep2Nodo(int idDeposito) {
        NodoGrafo aux = origen;
        while (aux != null && aux.deposito.getId() != idDeposito)
            aux = aux.sigNodo;
        return aux;
    }

    /**
     * Inserta una arista desde el nodo origen hacia el nodo destino con la distancia dada.
     * Se inserta al inicio de la lista de aristas del nodo origen.
     */
    private void insertarArista(NodoGrafo nodoOrigen, NodoGrafo nodoDestino, int distancia) {
        NodoArista nueva = new NodoArista();
        nueva.distancia = distancia;
        nueva.nodoDestino = nodoDestino;
        nueva.sigArista = nodoOrigen.arista;
        nodoOrigen.arista = nueva;
    }

    /**
     * Elimina la arista del nodo dado que apunta al depósito con idDestino.
     * Si no existe, no hace nada.
     */
    private void eliminarAristaNodo(NodoGrafo nodo, int idDestino) {
        NodoArista aux = nodo.arista;
        if (aux == null) return;

        // Caso especial: la arista a eliminar es la primera
        if (aux.nodoDestino.deposito.getId() == idDestino) {
            nodo.arista = aux.sigArista;
            return;
        }

        // Caso general: recorrer hasta encontrarla
        while (aux.sigArista != null && aux.sigArista.nodoDestino.deposito.getId() != idDestino)
            aux = aux.sigArista;

        // Si la encontró, la elimina por circunvalación
        if (aux.sigArista != null)
            aux.sigArista = aux.sigArista.sigArista;
    }
}