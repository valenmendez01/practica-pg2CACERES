package TDAs;

import model.Deposito;

import java.util.*;

// Cada vértice representa un Deposito (identificado por su id).
// Cada arista representa una ruta con una distancia (peso) entre dos depósitos.

public class GrafoDepositos {

    // Clases internas de la estructura

    private static class NodoGrafo {
        Deposito nodo;
        NodoArista arista;
        NodoGrafo sigNodo;
    }

    private static class NodoArista {
        int distancia;          // peso de la arista (ruta) (distancia en km, etc.)
        NodoGrafo nodoDestino;
        NodoArista sigArista;
    }

    // Inicialización

    private NodoGrafo origen; // referencia al primer vértice de la lista de vértices

    public GrafoDepositos() {
        origen = null;
    }

    // Operaciones sobre vértices

    public void agregarVertice(Deposito dep) {
        NodoGrafo aux = new NodoGrafo();
        aux.nodo = dep;
        aux.arista = null;
        aux.sigNodo = origen;
        origen = aux;
    }

    public void eliminarVertice(Deposito dep) {
        if (origen.nodo.getId() == dep.getId())
            origen = origen.sigNodo;

        NodoGrafo aux = origen;
        while (aux != null) {
            this.eliminarAristaNodo(aux, dep);
            if (aux.sigNodo != null && aux.sigNodo.nodo.getId() == dep.getId())
                aux.sigNodo = aux.sigNodo.sigNodo;
            aux = aux.sigNodo;
        }
    }

    public Set<Deposito> vertices() {
        Set<Deposito> conjunto = new HashSet<>();
        NodoGrafo aux = origen;
        while (aux != null) {
            conjunto.add(aux.nodo);
            aux = aux.sigNodo;
        }
        return conjunto;
    }

    // Operaciones sobre aristas (rutas)

    public void agregarArista(Deposito dep1, Deposito dep2, int peso) {
        NodoGrafo n1 = vert2Nodo(dep1); // nodo origen
        NodoGrafo n2 = vert2Nodo(dep2); // nodo destino
        // Dirección dep1 -> dep2
        NodoArista aux1 = new NodoArista();
        aux1.distancia = peso;
        aux1.nodoDestino = n2;
        aux1.sigArista = n1.arista;
        n1.arista = aux1;

        // Dirección dep2 -> dep1
        NodoArista aux2 = new NodoArista();
        aux2.distancia = peso;
        aux2.nodoDestino = n1;
        aux2.sigArista = n2.arista;
        n2.arista = aux2;
    }

    public void eliminarArista(Deposito dep1, Deposito dep2) {
        NodoGrafo n1 = vert2Nodo(dep1);
        eliminarAristaNodo(n1, dep2);
    }

    public int pesoArista(Deposito dep1, Deposito dep2) {
        NodoGrafo n1 = vert2Nodo(dep1);
        NodoArista aux = n1.arista;
        while (aux.nodoDestino.nodo.getId() != dep2.getId())
            aux = aux.sigArista;
        return aux.distancia;
    }

    public boolean existeArista(Deposito dep1, Deposito dep2) {
        NodoGrafo n1 = vert2Nodo(dep1);
        NodoArista aux = n1.arista;
        while (aux != null && aux.nodoDestino.nodo.getId() != dep2.getId())
            aux = aux.sigArista;
        return (aux != null);
    }

    // Métodos auxiliares

    private NodoGrafo vert2Nodo(Deposito dep) {
        NodoGrafo aux = origen;
        while (aux != null && aux.nodo.getId() != dep.getId())
            aux = aux.sigNodo;
        return aux;
    }

    private void eliminarAristaNodo(NodoGrafo nodo, Deposito dep) {
        NodoArista aux = nodo.arista;
        if (aux != null) {
            if (aux.nodoDestino.nodo.getId() == dep.getId()) {
                nodo.arista = aux.sigArista;
            } else {
                while (aux.sigArista != null && aux.sigArista.nodoDestino.nodo.getId() != dep.getId())
                    aux = aux.sigArista;
                if (aux.sigArista != null)
                    aux.sigArista = aux.sigArista.sigArista;
            }
        }
    }

    public Deposito idToDeposito(int id) {
        for (Deposito dep : this.vertices()) {
            if (dep.getId() == id)
                return dep;
        }
        return null; // id no existe en el grafo
    }

    /*
    [BFS] G1. Recorrido BFS desde un depósito
    Implementá un recorrido BFS desde un idDeposito dado e imprimí el orden de visita.
    Nota: Marcá los nodos visitados para evitar ciclos. Indicá la complejidad.
    Complejidad cuadrática O(n2)
     */
    public void recorrerBfs(int idDeposito) {
        Deposito inicio = idToDeposito(idDeposito);
        if (inicio == null) return;

        Set<Integer> visitados = new HashSet<>();
        Queue<Deposito> queue = new LinkedList<>();

        visitados.add(inicio.getId());
        queue.add(inicio);

        while (!queue.isEmpty()) {
            Deposito actual = queue.poll();
            System.out.println(actual);

            NodoGrafo nodoActual = vert2Nodo(actual);
            NodoArista aristaActual = nodoActual.arista;

            while (aristaActual != null) {
                Deposito vecino = aristaActual.nodoDestino.nodo;

                if (!visitados.contains(vecino.getId())) {
                    visitados.add(vecino.getId());
                    queue.add(vecino);
                }

                // Pasamos al siguiente vecino en la lista de adyacencia
                aristaActual = aristaActual.sigArista;
            }
        }
    }

    /*
    [BFS] G2. Cantidad de saltos entre depósitos
    Implementá cantidadSaltos(int origen, int destino) que retorne la cantidad mínima de saltos entre dos depósitos.
    Nota: BFS garantiza el mínimo de saltos. ¿Por qué DFS no lo garantiza?
    BFS garantiza el mínimo porque explora nivel por nivel.
    DFS no garantiza el mínimo porque se hunde por una rama hasta el fondo antes de explorar otras.
    Puede encontrar el destino por un camino largo antes de haber considerado uno más corto.
     */
    public int cantidadSaltos(int idOrigen, int idDestino) {
        Deposito inicio = idToDeposito(idOrigen);
        Deposito fin = idToDeposito(idDestino);
        if (inicio == null || fin == null) return -1;
        if (idOrigen == idDestino) return 0;

        Set<Integer> visitados = new HashSet<>();
        Queue<Deposito> queue = new LinkedList<>();
        Queue<Integer> saltos = new LinkedList<>();

        visitados.add(inicio.getId());
        queue.add(inicio);
        saltos.add(0);

        while (!queue.isEmpty()) {
            Deposito actual = queue.poll();
            int nivelActual = saltos.poll();

            NodoGrafo nodoActual = vert2Nodo(actual);
            NodoArista aristaActual = nodoActual.arista;

            while (aristaActual != null) {
                Deposito vecino = aristaActual.nodoDestino.nodo;

                if (vecino.getId() == idDestino)
                    return nivelActual + 1; // encontré el destino

                if (!visitados.contains(vecino.getId())) {
                    visitados.add(vecino.getId());
                    queue.add(vecino);
                    saltos.add(nivelActual + 1);
                }
                aristaActual = aristaActual.sigArista; // avanzar al siguiente
            }
        }
        return -1; // no hay camino
    }

    /*
    [BFS] G3. Depósitos a distancia N saltos
    Implementá depositosADistancia(int origen, int n) que retorne todos los depósitos a exactamente N saltos del origen.
    Nota: Usá BFS con control de nivel. ¿Cuál es la complejidad en V y E?
     */
    public Set<Deposito> depositosADistancia(int idOrigen, int n) {
        Deposito inicio = idToDeposito(idOrigen);
        if (inicio == null) return new HashSet<>();

        Set<Deposito> resultado = new HashSet<>();
        Set<Integer> visitados = new HashSet<>();
        Queue<Deposito> queue = new LinkedList<>();
        Queue<Integer> saltos = new LinkedList<>();

        visitados.add(inicio.getId());
        queue.add(inicio);
        saltos.add(0);

        while (!queue.isEmpty()) {
            Deposito actual = queue.poll();
            int nivelActual = saltos.poll();

            if (nivelActual == n) {
                resultado.add(actual);
                continue;
            }

            if (nivelActual > n) break;

            NodoGrafo nodoActual = vert2Nodo(actual);
            NodoArista aristaActual = nodoActual.arista;

            while (aristaActual != null) {
                Deposito vecino = aristaActual.nodoDestino.nodo;
                if (!visitados.contains(vecino.getId())) {
                    visitados.add(vecino.getId());
                    queue.add(vecino);
                    saltos.add(nivelActual + 1);
                }
                aristaActual = aristaActual.sigArista;
            }
        }
        return resultado;
    }

    /*
    [BFS] G4. Verificar conexión entre depósitos
    Implementá estaConectado(int origen, int destino) que retorne true si existe algún camino entre los dos depósitos.
    Nota: ¿Qué representa que el grafo no sea conexo en el contexto logístico?
    Que hay depósitos que no pueden comunicarse entre sí — ninguna ruta los conecta.
    En la práctica significaría que un camión que sale de un depósito A nunca podría llegar al depósito B, lo cual es un problema crítico de distribución.
     */
    public boolean estaConectado(int idOrigen, int idDestino) {
        Deposito inicio = idToDeposito(idOrigen);
        Deposito fin = idToDeposito(idDestino);
        if (inicio == null || fin == null) return false;
        if (idOrigen == idDestino) return true;

        Set<Integer> visitados = new HashSet<>();
        Queue<Deposito> queue = new LinkedList<>();

        visitados.add(inicio.getId());
        queue.add(inicio);

        while (!queue.isEmpty()) {
            Deposito actual = queue.poll();

            NodoGrafo nodoActual = vert2Nodo(actual);
            NodoArista aristaActual = nodoActual.arista;

            while (aristaActual != null) {
                Deposito vecino = aristaActual.nodoDestino.nodo;

                if (vecino.getId() == idDestino)
                    return true;

                if (!visitados.contains(vecino.getId())) {
                    visitados.add(vecino.getId());
                    queue.add(vecino);
                }
                aristaActual = aristaActual.sigArista;
            }
        }
        return false;
    }

    /*
    [BFS] G5. Camino más corto con distancia
    Extendé cantidadSaltos para que, en un grafo ponderado, retorne la distancia mínima en km entre dos depósitos
    (Dijkstra simplificado con cola de prioridad).
    Nota: ¿Qué diferencia hay entre minimizar saltos y minimizar distancia?
     */
    public int distanciaMinima(int idOrigen, int idDestino) {
        Deposito inicio = idToDeposito(idOrigen);
        if (inicio == null) return -1;
        if (idOrigen == idDestino) return 0;

        Map<Integer, Integer> distancias = new HashMap<>();
        for (Deposito dep : vertices()) {
            distancias.put(dep.getId(), Integer.MAX_VALUE);
        }
        distancias.put(idOrigen, 0);

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.add(new int[]{idOrigen, 0});

        while (!pq.isEmpty()) {
            int[] actual = pq.poll();
            int idActual = actual[0];
            int distActual = actual[1];

            if (distActual > distancias.get(idActual)) continue;
            if (idActual == idDestino) return distActual;

            Deposito depActual = idToDeposito(idActual);

            NodoGrafo nodoActual = vert2Nodo(depActual);
            NodoArista aristaActual = nodoActual.arista;

            while (aristaActual != null) {
                Deposito vecino = aristaActual.nodoDestino.nodo;
                // Usamos la distancia directamente de la arista
                int nuevaDist = distActual + aristaActual.distancia;

                if (nuevaDist < distancias.get(vecino.getId())) {
                    distancias.put(vecino.getId(), nuevaDist);
                    pq.add(new int[]{vecino.getId(), nuevaDist});
                }
                aristaActual = aristaActual.sigArista;
            }
        }
        return -1;
    }

    /*
    [DFS] G6. Recorrido DFS desde un depósito
    Implementá un recorrido DFS (iterativo con Stack o recursivo) desde un idDeposito dado e imprimí el orden de visita.
    Nota: ¿En qué se diferencia el orden de visita respecto al BFS?
    BFS usa una Queue (FIFO) y explora nivel por nivel — primero todos los vecinos directos, luego los vecinos de los vecinos.
    DFS usa un Stack (LIFO) y se hunde lo más profundo posible por cada rama antes de retroceder.
     */
    public void recorrerDfs(int idDeposito) {
        Deposito inicio = idToDeposito(idDeposito);
        if (inicio == null) return;

        Set<Integer> visitados = new HashSet<>();
        Stack<Deposito> stack = new Stack<>();

        stack.push(inicio);

        while (!stack.isEmpty()) {
            Deposito actual = stack.pop();

            if (!visitados.contains(actual.getId())) {
                visitados.add(actual.getId());
                System.out.println(actual);

                NodoGrafo nodoActual = vert2Nodo(actual);
                NodoArista aristaActual = nodoActual.arista;

                while (aristaActual != null) {
                    Deposito vecino = aristaActual.nodoDestino.nodo;
                    if (!visitados.contains(vecino.getId())) {
                        stack.push(vecino);
                    }
                    aristaActual = aristaActual.sigArista;
                }
            }
        }
    }

    /*
    [DFS] G7. Detectar ciclo en el grafo
    Implementá tieneCiclo() que retorne true si el grafo contiene algún ciclo, usando DFS.
    Nota: Usá tres estados: NO_VISITADO, EN_PROCESO, VISITADO.
     */
    public boolean tieneCiclo() {
        Map<Integer, String> estados = new HashMap<>();

        // inicializamos todos en NO_VISITADO
        for (Deposito dep : vertices()) {
            estados.put(dep.getId(), "NO_VISITADO");
        }

        // lanzamos DFS desde cada nodo no visitado
        for (Deposito dep : vertices()) {
            if (estados.get(dep.getId()).equals("NO_VISITADO")) {
                // Pasamos -1 como idPadre inicial (no tiene padre)
                if (dfsCiclo(dep, -1, estados)) return true;
            }
        }

        return false;
    }

    private boolean dfsCiclo(Deposito actual, int idPadre, Map<Integer, String> estados) {
        estados.put(actual.getId(), "EN_PROCESO");

        NodoGrafo nodoActual = vert2Nodo(actual);
        NodoArista aristaActual = nodoActual.arista;

        while (aristaActual != null) {
            Deposito vecino = aristaActual.nodoDestino.nodo;

            // Ignoramos la ruta de vuelta inmediata hacia el nodo del que vinimos
            if (vecino.getId() != idPadre) {
                if (estados.get(vecino.getId()).equals("EN_PROCESO"))
                    return true;

                if (estados.get(vecino.getId()).equals("NO_VISITADO"))
                    if (dfsCiclo(vecino, actual.getId(), estados)) return true;
            }

            aristaActual = aristaActual.sigArista;
        }

        estados.put(actual.getId(), "VISITADO");
        return false;
    }

    /*
    [DFS] G8. Imprimir todos los caminos
    Implementá imprimirCaminos(int origen, int destino) que imprima todos los caminos simples entre dos depósitos
    usando DFS con backtracking.
    Nota: Usá una lista para acumular el camino actual y backtrack al salir.
     */
    public void imprimirCaminos(int idOrigen, int idDestino) {
        Deposito inicio = idToDeposito(idOrigen);
        Deposito fin = idToDeposito(idDestino);
        if (inicio == null || fin == null) return;

        List<Deposito> caminoActual = new ArrayList<>();
        Set<Integer> visitados = new HashSet<>();

        dfsCaminos(inicio, fin, caminoActual, visitados);
    }

    private void dfsCaminos(Deposito actual, Deposito destino, List<Deposito> caminoActual, Set<Integer> visitados) {
        visitados.add(actual.getId());
        caminoActual.add(actual);

        if (actual.getId() == destino.getId()) {
            System.out.println(caminoActual);
        } else {
            NodoGrafo nodoActual = vert2Nodo(actual);
            NodoArista aristaActual = nodoActual.arista;

            while (aristaActual != null) {
                Deposito vecino = aristaActual.nodoDestino.nodo;
                if (!visitados.contains(vecino.getId())) {
                    dfsCaminos(vecino, destino, caminoActual, visitados);
                }
                aristaActual = aristaActual.sigArista;
            }
        }

        // backtrack — compatible con todas las versiones de Java
        caminoActual.remove(caminoActual.size() - 1);
        visitados.remove(actual.getId());
    }

    /*
    [DFS] G9. Verificar si el grafo es conexo
    Implementá esConexo() que retorne true si todos los depósitos son alcanzables desde cualquier nodo inicial.
    Nota: Si visitados == total de nodos → conexo.
     */
    public boolean esConexo() {
        Set<Deposito> todos = vertices();
        if (todos.isEmpty()) return true;

        Deposito primero = todos.iterator().next(); // agarrás cualquier nodo
        Set<Integer> visitados = new HashSet<>();
        dfsConexo(primero, visitados);

        return visitados.size() == todos.size();
    }

    private void dfsConexo(Deposito actual, Set<Integer> visitados) {
        visitados.add(actual.getId());

        NodoGrafo nodoActual = vert2Nodo(actual);
        NodoArista aristaActual = nodoActual.arista;

        while (aristaActual != null) {
            Deposito vecino = aristaActual.nodoDestino.nodo;
            if (!visitados.contains(vecino.getId())) {
                dfsConexo(vecino, visitados);
            }
            aristaActual = aristaActual.sigArista;
        }
    }

    /*
    [DFS] G10. Componentes conexas
    Implementá cantidadComponentesConexas() que retorne cuántas componentes conexas tiene el grafo.
    Nota: Iterar sobre nodos no visitados y lanzar DFS desde cada uno.
     */
    public int cantidadComponentesConexas() {
        Set<Deposito> todos = vertices();
        if (todos.isEmpty()) return 0;

        Set<Integer> visitados = new HashSet<>();
        int componentes = 0;

        for (Deposito dep : todos) {
            if (!visitados.contains(dep.getId())) {
                dfsConexo(dep, visitados); // marca todos los alcanzables desde dep
                componentes++;             // cada DFS nuevo es una componente nueva
            }
        }

        return componentes;
    }

}