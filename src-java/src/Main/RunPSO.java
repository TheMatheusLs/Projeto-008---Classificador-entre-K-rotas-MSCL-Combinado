package Main;

import GeneralClasses.AuxiliaryFunctions;
import Manager.FolderManager;
import Manager.SimulationResults;
import PSO.PSO_config;
import PSO.PSO_particle;
import PSO.PSO_swarm;

public class RunPSO {
    
    public static void main(String[] args) throws Exception {
        
        // Calcula o tempo inicial da simulação
        final long geralInitTime = System.currentTimeMillis(); 

        //Importa as configurações da simulação
        if (!AuxiliaryFunctions.isParametersSimulationOK()){
            throw new Exception("Os parâmetros de simulação não estão corretos! Revise antes de iniciar uma nova simulação!");
        }

        // Cria a pasta para armazenar os resultados e as configurações da simulação
        FolderManager folderManager = new FolderManager("PSO");

        // Cria a simulação
        Simulation simulation = new Simulation(folderManager);

        //TODO: Treinamento do PSR com o MSCL Combinado

        // Cria a população de partículas
        PSO_swarm swarm = new PSO_swarm(simulation, PSO_config.getNetworkload(), 42);

        // Percorre todas as interações do PSO
        for (int i = 0; i < PSO_config.getMaxiterations(); i++) {

            System.out.println("Executando o treinamento do PSR com o MSCL Combinado: " + (i + 1) + " de " + PSO_config.getMaxiterations());

            // Atualiza a velocidade e a posição de cada partícula
            swarm.updateVelocityAndPosition_global();
            
            // Executa a simulação com a partícula selecionada para obter o custo
            swarm.run(simulation, PSO_config.getNetworkload(), 42);

            // Escreve na tela os resultados da melhor partícula
            PSO_particle gBestParticle = swarm.getBestParticle();

            System.out.println(gBestParticle.toString());
        }

        // Escreve na tela os resultados
        System.out.println("\n** Resultado final:");
        PSO_particle gBestParticle = swarm.getBestParticle();
        System.out.println(gBestParticle.toString());

        
        //TODO: Teste do PSR com o MSCL Combinado
        SimulationResults simulationResults = new SimulationResults(PSO_config.getNetworkload(), 1, 42);

        PSO_particle particle = swarm.getBestParticle();

        simulationResults = simulation.runSingleLoad_PSR(PSO_config.getNetworkload(), simulationResults, particle);

        // Escreve na tela os resultados
        System.out.println(simulationResults);

        simulationResults = simulation.runSingleLoad_PSR(PSO_config.getNetworkload(), simulationResults, particle);

        // Escreve na tela os resultados
        System.out.println(simulationResults);

        // Calcula o tempo final da simulação
        final long geralEndTime = System.currentTimeMillis();

        // Calcula o tempo total da simulação
        final long geralTotalTime = geralEndTime - geralInitTime;

        folderManager.writeDone(geralTotalTime);
        System.out.println("Simulação finalizada com o tempo de " + geralTotalTime + " ms!");

    }

}
