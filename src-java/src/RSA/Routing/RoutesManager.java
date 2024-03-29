package RSA.Routing;

import java.util.ArrayList;
import java.util.List;

import Config.ParametersSimulation;
import Manager.FolderManager;
import Network.TopologyManager;
import Network.Structure.OpticalLink;
import RSA.Routing.Algorithms.Dijkstra;
import RSA.Routing.Algorithms.YEN;
import Types.GeneralTypes.CallRequestType;
import Types.GeneralTypes.RoutingAlgorithmType;
import Types.GeneralTypes.SpectralAllocationAlgorithmType;
import Types.GeneralTypes.TopologyType;

public class RoutesManager {

    /**
     * Estrutura para armazenar todas as rotas encontradas para a topologia
     */
    private List<List<Route>> allRoutes;
    /**
     * Classe que controla a topologia escolhida
     */
    private TopologyManager networkTopology;
    /**
     * Número de nós presentes na topologia escolhida
     */
    private int numberOfNodesInTopology;
    /**
     * Tipo do algoritmo de roteamento selecionado
     */
    private RoutingAlgorithmType routingOption;
    /**
     * Número de rotas que o algoritmo YEN irá buscar.
     */
    private int numberOfRoutesToFind;
    
    /**
     * Construtor da classe RoutesManager
     * 
     * @param topology Topologia de rede
     * @throws Exception
     */
    public RoutesManager(TopologyManager topology) throws Exception {
        this.networkTopology = topology;
        this.numberOfNodesInTopology = topology.getNumberOfNodes();
        this.routingOption = ParametersSimulation.getRoutingAlgorithmType();

        // Cria a estrutura para armazenar todas as rotas
        this.allRoutes = this.createAllRoutes();


        // Inicializa o processo de roteamento estático
        if (this.routingOption.equals(RoutingAlgorithmType.Dijstra)) {
            this.numberOfRoutesToFind = 1;
            this.RoutingByDijkstra();
        } else {
            // Usado para o algoritmo de roteamento YEN
            this.numberOfRoutesToFind = ParametersSimulation.getKShortestRoutes();
            this.RoutingByYEN();
        }

        // Se a topologia utilizada for a 'IRoutes' é necessário remover algumas rotas. Somente devem permanecer as rotas com path igual a '1,2,3,4', '2,3,6,7', '3,4,5,6' e '5,6,7,8'.
        if (ParametersSimulation.getTopologyType().equals(TopologyType.IRoutes)){
            // Verifica se a configuração é unidirecional
            if (ParametersSimulation.getCallRequestType().equals(CallRequestType.Unidirectional)){
                this.allRoutes.clear();

                for (int i = 0; i < 4; i++){
                    List<Route> aux = new ArrayList<Route>();
                    this.allRoutes.add(aux);
                }

                int[] path = new int[]{0,1,2,3};

                // Cria uma lista de inteiros com os valores 0,1,2,3
                List<Integer> pathList = new ArrayList<Integer>();
                for (int i = 0; i < path.length; i++){
                    pathList.add(path[i]);
                }

                Route R1 = new Route(pathList, this.networkTopology);
                this.allRoutes.get(0).add(R1);

                path = new int[]{1,2,5,6};

                // Cria uma lista de inteiros com os valores 0,1,2,3
                pathList = new ArrayList<Integer>();
                for (int i = 0; i < path.length; i++){
                    pathList.add(path[i]);
                }

                R1 = new Route(pathList, this.networkTopology);
                this.allRoutes.get(1).add(R1);

                path = new int[]{2,3,4,5};

                // Cria uma lista de inteiros com os valores 0,1,2,3
                pathList = new ArrayList<Integer>();
                for (int i = 0; i < path.length; i++){
                    pathList.add(path[i]);
                }

                R1 = new Route(pathList, this.networkTopology);
                this.allRoutes.get(2).add(R1);

                path = new int[]{4,5,6,7};

                // Cria uma lista de inteiros com os valores 0,1,2,3
                pathList = new ArrayList<Integer>();
                for (int i = 0; i < path.length; i++){
                    pathList.add(path[i]);
                }

                R1 = new Route(pathList, this.networkTopology);
                this.allRoutes.get(3).add(R1);

            }
            else {
                throw new Exception("A topologia não pode ser utilizada no modo bidirecional");
            }
        }

        // Se o algoritmo precisa do junto de rotas interferentes, constroe o conjuto de rotas interferentes
        if (ParametersSimulation.getSpectralAllocationAlgorithmType() == SpectralAllocationAlgorithmType.MSCL){
            this.generateAllConflictRoutes();
        }

        // Imprime na tela as rotas
        //System.out.println(this);
    }

    /**
     * Método para remover todas as rotas que não são utilizadas na topologia IRoutes
     */
    private void removeRoutesIRoutes(){

        // Path de rotas que devem ser mantidas
        

    }


    /**
     * Verifica se todas as rotas foram liberadas corretamente.
     * 
     * @throws Exception
     */
    public void checkIfIsClean() throws Exception{

        final int numSlotsPerLink = ParametersSimulation.getNumberOfSlotsPerLink();

        for (List<Route> routes: this.allRoutes){
            for (Route route : routes){
                if (route != null){
                    for (int i = 0; i < numSlotsPerLink;i++){
                        if (route.getSlotValue(i) != 0){
                            throw new Exception("As rotas não foram limpas corretamente");
                        }
                    }
                }
            }
		}
    }

    private void generateAllConflictRoutes(){
        System.out.print("Criando a lista de rotas conflitantes...");

        for (List<Route> routesOD : this.allRoutes){
            for (Route mainRoutes : routesOD){
                if (mainRoutes == null){
                    continue;
                }

                List<Route> conflictRoutes = new ArrayList<Route>();
                int currentRouteID = mainRoutes.hashCode();

                //Se pelo menos um link for compartilhado pelas rotas há um conflito
                for (OpticalLink mainOpticalLink: mainRoutes.getUpLink()){
                    
                    // Percorre todas as Rotas
                    for (List<Route> routesODAux : this.allRoutes){

                        for (Route mainRoutesAux : routesODAux){

                            if (mainRoutesAux == null){
                                continue;
                            }

                            BREAK_LINK:for (OpticalLink linkAux: mainRoutesAux.getUpLink()){

                                if ((currentRouteID != mainRoutesAux.hashCode()) && (mainOpticalLink == linkAux)){

                                    for (Route routeAuxCon : conflictRoutes){
                                        if (routeAuxCon == mainRoutesAux){
                                            continue BREAK_LINK;
                                        }
                                    }

                                    conflictRoutes.add(mainRoutesAux);

                                    break BREAK_LINK;
                                }
                            }
                        }
                    }
                }

                mainRoutes.setConflitList(conflictRoutes);
            }
        }

        System.out.println(" Finalizado.");
    }


    /**
     * Método para criar a estrutura para armazenar todas as rotas separadas entre os pares origem destino 
     * 
     * @return Uma lista para cada par origem destino, contendo uma lista de k rotas entre eles
     */
    private List<List<Route>> createAllRoutes() {

        List<List<Route>> routesInit = new ArrayList<List<Route>>();

        for (int OD = 0; OD < (this.numberOfNodesInTopology * this.numberOfNodesInTopology); OD++) {

            List<Route> routeAux = new ArrayList<Route>();

            routesInit.add(routeAux);
        }

        return routesInit;
    }

    /**
     * Método para criar todas as K rotas para a topologia utilizando o algoritmo YEN
     */
    private void RoutingByYEN() {

        List<Route> routes;

        for(int orN = 0; orN < this.numberOfNodesInTopology; orN++){
            for(int deN = 0; deN < this.numberOfNodesInTopology; deN++){

                if(orN != deN){
                    routes = YEN.findRoute(orN, deN, this.networkTopology, this.numberOfRoutesToFind);
                    this.setRoutes(orN, deN, routes);
                } else{
                    routes = null;
                    this.setRoute(orN, deN, null);
                }
            }
        }
    }

    /**
     * Método para criar todas as rotas para a topologia utilizando o algoritmo Dijkstra
     */
    private void RoutingByDijkstra() {

        Route routeAux;

        for(int orN = 0; orN < this.numberOfNodesInTopology; orN++){
            for(int deN = 0; deN < this.numberOfNodesInTopology; deN++){

                if(orN != deN){
                    routeAux = Dijkstra.findRoute(orN, deN, this.networkTopology);
                    this.setRoute(orN, deN, routeAux);
                }
            }
        }
    }

    /**
     * Método para configurar uma rota a lista de rotas por par origem destino
     * 
     * @param orN Nó de origem 
     * @param deN Nó de destino
     * @param route Routa a ser adicionada
     */
    private void setRoute(int orN, int deN, Route route) {
        this.clearRoutes(orN, deN);
        this.addRoute(orN, deN, route);
    }

    /**
     * Método para limpar e adicionar uma lista de k rotas para um determinado origem destino
     * 
     * @param orN Nó de origem 
     * @param deN Nó de destino
     * @param routes Lista de k rotas
     */
    private void setRoutes(int orN, int deN, List<Route> routes) {
        this.clearRoutes(orN, deN);
        this.addRoutes(orN, deN, routes);
    }
    
    /**
     * Método para armazenar uma lista de k rotas para um determinado origem destino
     * 
     * @param orN Nó de origem 
     * @param deN Nó de destino
     * @param routes Lista de k rotas 
     */
    private void addRoutes(int orN, int deN, List<Route> routes) {
        
        if (routes != null) {
            for(Route it : routes)
                this.addRoute(orN, deN, it);
        } else {
            this.addRoute(orN, deN, null);
        }
    }

    /**
     * Método para limpar todas as k rotas entre um determinado para origem destino
     * 
     * @param orN Nó de origem 
     * @param deN Nó de destino
     */
    private void clearRoutes(int orN, int deN) {
        allRoutes.get(orN * this.numberOfNodesInTopology + deN).clear();
    }

    /**
     * Método para adicionar uma rota a lista de rotas por par origem destino
     * 
     * @param orN Nó de origem 
     * @param deN Nó de destino
     * @param route Routa a ser adicionada
     */
    private void addRoute(int orN, int deN, Route route) {
        allRoutes.get(orN * this.numberOfNodesInTopology + deN).add(route);
    }


    @Override
    public String toString() {

        String txt = "";

        for (List<Route> routes : allRoutes){
            for (Route route : routes){
                if (route != null){
                    txt += route;
                }
            }
        }

        return txt;
    }
    
    /**
     * Método para salvar todas as rotas em um arquivo de texto
     * 
     * @param folderManager
     */
    public void save(FolderManager folderManager) {
        folderManager.writeRoutes((String.valueOf(this)));
    }

    /**
     * Método para retornar a lista das K rotas para um determinado para origem destino.
     * 
     * @param source Nó de origem
     * @param destination Nó de destino
     * @return Uma lista com K rotas 
     */
    public List<Route> getRoutesForOD(int source, int destination) {
        return allRoutes.get(source * this.numberOfNodesInTopology + destination);
    }

    public List<List<Route>> getAllRoutes() {
        return allRoutes;
    }
}
