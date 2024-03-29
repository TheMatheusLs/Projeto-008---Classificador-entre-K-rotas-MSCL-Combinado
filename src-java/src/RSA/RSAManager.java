package RSA;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import CallRequests.CallRequest;
import Config.ParametersSimulation;
import Manager.FolderManager;
import Network.Structure.OpticalLink;
import RSA.Routing.Route;
import RSA.Routing.RoutesManager;
import RSA.Spectrum.Algorithms.FirstFit;
import RSA.Spectrum.Algorithms.FirstLastFit;
import RSA.Spectrum.Algorithms.SpectrumAlgorithm;
import Types.GeneralTypes.KSortedRoutesByType;
import Types.GeneralTypes.RSAOrderType;
import Types.GeneralTypes.RoutingAlgorithmType;
import Types.GeneralTypes.SpectralAllocationAlgorithmType;
import Types.GeneralTypes.TopologyType;

public class RSAManager {

    /**
     * Algoritmo de alocação do espectro
     */
    private SpectrumAlgorithm spectrumAlgorithm;
    /**
     * Tipo de algoritmo de alocação do espectro
     */
    private RoutingAlgorithmType routingOption;
    /**
     * Classe de gerenciamento do Roteamento
     */
    private RoutesManager routesManager;
    /**
     * Rota selecionada pelo algoritmo de roteamento
     */
    private Route route;
    /**
     * Conjunto de slots selecionados pelo algoritmo de alocação do espectro
     */
    private List<Integer> fSlots;
    /**
     * Alocação do espectro em RSA ou SAR
     */
    private RSAOrderType rsaOrderType;
    /**
     * Armazena os ciclos MSCL
     */
    private long cycleMSCL;
    
    private FolderManager folderManager;

    private Random randomGeneration;
    /**
     * Construtor da classe RSAManager
     * 
     * @param routesManager Classe para gerenciar o roteamento
     */
    public RSAManager(RoutesManager routesManager, FolderManager folderManager, Random randomGeneration) {
        this.routesManager = routesManager;
        this.randomGeneration = randomGeneration;
        this.cycleMSCL = 0;

        this.folderManager = folderManager;

        this.route = null;
        this.fSlots = new ArrayList<Integer>();

        this.spectrumAlgorithm = null;
        rsaOrderType = ParametersSimulation.getRSAOrderType();

        this.routingOption = ParametersSimulation.getRoutingAlgorithmType();
        SpectralAllocationAlgorithmType spectrumOption = ParametersSimulation.getSpectralAllocationAlgorithmType();

        // Verifica se o Spectrum escolhido é o FF
        if (spectrumOption.equals(SpectralAllocationAlgorithmType.FirstFit)){
            this.spectrumAlgorithm = new FirstFit();
        }

        if (spectrumOption.equals(SpectralAllocationAlgorithmType.FirstLastFit)){
            this.spectrumAlgorithm = new FirstLastFit();
        }
    }

    /**
     * Método para gerenciar os algoritmos de roteamento e alocação do espectro.
     * 
     * @param source Origem 
     * @param destination Destino
     * @param callRequest Requisição
     * @throws Exception
     */
    public void findRouteAndSlots(int source, int destination, CallRequest callRequest) throws Exception {
        
        if (this.rsaOrderType == RSAOrderType.None){
            // Captura as rotas para o par origem destino
            List<Route> routeSolution;
            if (ParametersSimulation.getTopologyType().equals(TopologyType.IRoutes)){
                routeSolution = this.routesManager.getRoutesForOD(0, source);
            } else {
                routeSolution = this.routesManager.getRoutesForOD(source, destination);
            }

            List<Route> allRoutes = new ArrayList<>();
            allRoutes.addAll(this.routesManager.getAllRoutes().get(0));
            allRoutes.addAll(this.routesManager.getAllRoutes().get(1));
            allRoutes.addAll(this.routesManager.getAllRoutes().get(2));
            allRoutes.addAll(this.routesManager.getAllRoutes().get(3));

            MSCLAlgorithm msclAlgorithm = new MSCLAlgorithm(this.folderManager);
            if (routingOption == RoutingAlgorithmType.MSCLSequencial){
                msclAlgorithm.findMSCLSequencial(routeSolution, callRequest, allRoutes);
            }
            if (routingOption == RoutingAlgorithmType.MSCLCombinado){
                msclAlgorithm.findMSCLCombinado(routeSolution, callRequest);
            }
            this.route = msclAlgorithm.getRoute();
            this.fSlots = msclAlgorithm.getSlotsIndex();
            this.cycleMSCL = msclAlgorithm.getCyclesMSCL();
        }

        if (this.rsaOrderType == RSAOrderType.Routing_SA){
            this.findRoutingSA(source, destination, callRequest);
        }

        if (this.rsaOrderType == RSAOrderType.SA_Routing){
            this.findSARouting(source, destination, callRequest);
        }
    }

    /**
     * Método para realizar o roteamento por SAR. 
     * 
     * Esse algoritmo testa o primeiro slot e tenta uma alocação no seu espectro usando o algoritmo FF. Caso não seja possível alocar na primeiro slot da primera rota, busca no primeiro slot das próximas rotas. Depois testa o segundo slot e assim sucessivamente. 
     * 
     * @param source Origem 
     * @param destination Destino
     * @param callRequest Requisição
     * @throws Exception
     */
    public void findSARouting(int source, int destination, CallRequest callRequest) throws Exception {
        
        final int numberMaxSlotsPerLink = ParametersSimulation.getNumberOfSlotsPerLink();

        // Captura as rotas para o par origem destino
        List<Route> routeSolution;

        if (ParametersSimulation.getTopologyType().equals(TopologyType.IRoutes)){
            routeSolution = this.routesManager.getRoutesForOD(0, source);
        } else {
            routeSolution = this.routesManager.getRoutesForOD(source, destination);
        }


        LOOP_SLOT:for (int firstIndexSlot = 0; firstIndexSlot < numberMaxSlotsPerLink;) {

            LOOP_ROUTE:for (Route currentRoute : routeSolution){

                if (currentRoute == null){
                    continue LOOP_ROUTE;
                }

                // Calcula o tamanho da requisição
                int reqNumbOfSlots = currentRoute.getReqSize(callRequest.getSelectedBitRate());

                // Verifica se é possível alocar a requisição
                for (int indexSlot = firstIndexSlot; indexSlot < firstIndexSlot + reqNumbOfSlots; indexSlot++){
                    if (indexSlot >= numberMaxSlotsPerLink){
                        continue LOOP_ROUTE;
                    }
                    
                    if (!currentRoute.isSlotAvailable(indexSlot)){
                        continue LOOP_ROUTE;
                    }
                }

                this.route = currentRoute;
                this.fSlots = this.spectrumAlgorithm.findFrequencySlots(reqNumbOfSlots, currentRoute);
                callRequest.setReqNumbOfSlots(reqNumbOfSlots);

                break LOOP_SLOT;
            }

            firstIndexSlot++;
        }
    }

    /**
     * Método para realizar o roteamento por RSA. 
     * 
     * Esse algoritmo testa a primeira rota e tenta uma alocação no seu espectro usando o algoritmo selecionado. Caso não seja possível alocar na primeira rota, busca na próximas. 
     * 
     * @param source Origem 
     * @param destination Destino
     * @param callRequest Requisição
     * @throws Exception
     */
    public void findRoutingSA(int source, int destination, CallRequest callRequest) throws Exception {
        // Captura as rotas para o par origem destino
        List<Route> routeSolution;

        if (ParametersSimulation.getTopologyType().equals(TopologyType.IRoutes)){
            routeSolution = this.routesManager.getRoutesForOD(0, source);
        } else {
            routeSolution = this.routesManager.getRoutesForOD(source, destination);
        }

        // Realiza a ordenacao do conjunto de rotas conforme seleção
        if (ParametersSimulation.getKSortedRoutesByType() != KSortedRoutesByType.None){
            //TODO: Ordenar as rotas por ocupação e demais formas
        }

        this.route = null;
        this.fSlots = null;

        // Algoritmo para o RSA
        for (Route currentRoute : routeSolution){

            // Calcula o tamanho da requisição
            int reqNumbOfSlots = currentRoute.getReqSize(callRequest.getSelectedBitRate());
            
            List<Integer> slots = this.spectrumAlgorithm.findFrequencySlots(reqNumbOfSlots, currentRoute);
            
            if(!slots.isEmpty() && slots.size() == reqNumbOfSlots){
                this.route = currentRoute;
                this.fSlots = slots;
                callRequest.setReqNumbOfSlots(reqNumbOfSlots);
                break;
            } 
        }
    }

    /**
     * Método para retornar a rota encontrada após utilizar o algoritmo de roteamento.
     * 
     * @return Retorna a rota encontrada
     */
    public Route getRoute() {
        return this.route;
    }

    /**
     * Método para retornar o conjunto dos slots encontrado
     * 
     * @return O conjunto dos slots encontrado
     */
    public List<Integer> getSlotsIndex() {
        return this.fSlots;
    }

    /**
     * Método para retornar o valor do Cycles MSCL
     * 
     * @return O conjunto dos slots encontrado
     */
    public long getCyclesMSCL() {
        return this.cycleMSCL;
    }
}
