package PSO;

import java.util.Random;

import Main.Simulation;
import Manager.FolderManager;
import Manager.SimulationResults;

public class PSO_swarm {
    
    private PSO_particle[] particles;
    private double[] bestPosition;
    private double bestCost;
    private PSO_particle bestParticle;

    private Random rand;

    private int generation;

    /*
     * Construtor da classe PSO_swarm. Inicializa as partículas da população com posições aleatórias.
     */
    public PSO_swarm(Simulation simulation, double networkload, int seed) throws Exception {
        particles = new PSO_particle[PSO_config.getNumberofparticles()];
        bestPosition = new double[PSO_config.getDimension()];
        bestCost = Double.MAX_VALUE;
        for (int i = 0; i < PSO_config.getNumberofparticles(); i++) {
            particles[i] = new PSO_particle(PSO_config.getDimension(), i);
        }
        rand = new Random();

        generation = 0;
        
        System.out.println("Executando o treinamento do PSR com o MSCL Combinado: " + 0 + " de " + PSO_config.getMaxiterations());
        // Executa a simulação com a população inicial para obter o custo
        this.run(simulation, networkload, seed);

    }

    /*
     * Executa a simulação com a partícula selecionada para obter o custo
     */
    public void run(Simulation simulation, double networkload, int seed) throws Exception {
        
        // Percorre todas as partículas da população para atualizar o custo de cada uma
        for (PSO_particle particle : this.getParticles()) {

            // Executa a simulação com a partícula selecionada para obter o custo
            SimulationResults simulationResults = new SimulationResults(networkload, 1, seed);
            simulationResults = simulation.runSingleLoad_PSR(networkload, simulationResults, particle);

            // Atualiza o custo da partícula
            particle.setCost(simulationResults.getProbabilityBlocking());
        }

        this.updateBestParticle();

        // Salvando os resultados da simulação em um arquivo .csv
        this.saveCost(simulation.getFolderManager());

        this.saveBestSolution(simulation.getFolderManager());

        this.generation++;

    }

    private void saveBestSolution(FolderManager folderManager) {
        
        String content = "";

        if (this.generation == 0) {
            content = "Generation;BestCost;BestPosition\n";
        }

        content += this.generation + ";" + this.getBestCost() + ";[";
        for (int i = 0; i < PSO_config.getDimension() - 1; i++) {
            content += this.getGBestPosition()[i] + "|";
        }
        content += this.getGBestPosition()[PSO_config.getDimension() - 1] + "]\n";

        folderManager.writeFile("PSO_best_solution.csv", content);

    }

    private void saveCost(FolderManager folderManager) {
        
        String content = "";
        if (this.generation == 0) {
            content = "Generation;";
            for (int i = 0; i < PSO_config.getNumberofparticles() - 1; i++) {
                content += "Particle_" + i + ";";
            }
            content += "Particle_" + (PSO_config.getNumberofparticles() - 1) + "\n";
        }

        content += this.generation + ";";
        for (int i = 0; i < PSO_config.getNumberofparticles() - 1; i++) {
            content += this.getParticles()[i].getCost() + ";";
        }
        content += this.getParticles()[PSO_config.getNumberofparticles() - 1].getCost() + "\n";


        folderManager.writeFile("PSO_cost.csv", content);

    }

    /*
     * Atualiza a melhor posição e o melhor custo da população
     * A melhor partícula é aquela que possui o menor custo
     */
    public void updateBestParticle() {
        for (PSO_particle particle : this.getParticles()) {
            if (particle.getCost() < this.getBestCost()) {
                this.setBestCost(particle.getCost());
                this.setBestPosition(particle.getPosition());
                this.setBestParticle(particle);
            }
        }
    }

    /*
     * Atualiza a velocidade e a posição de cada partícula pelo método de global best
     */
    public void updateVelocityAndPosition_global() {
        
        double W = PSO_config.getW();
        double C1 = PSO_config.getC1();
        double C2 = PSO_config.getC2();

        double[] gBestPosition = this.getGBestPosition();

        // Atualiza a velocidade e a posição de cada partícula pelo método de global best
        for (PSO_particle particle : this.getParticles()) {

            // Inicializa a nova velocidade e a nova posição da partícula
            double[] newVelocity = new double[PSO_config.getDimension()];
            double[] newPosition = new double[PSO_config.getDimension()];

            double[] oldVelocity = particle.getVelocity();
            double[] oldPosition = particle.getPosition();
            double[] particleBestPosition = particle.getBestPosition();


            // Percorre as dimensões da partícula
            for (int d = 0; d < PSO_config.getDimension(); d++) {

                // Atualiza a velocidade da partícula pela fórmula do PSO global best na dimensão d
                newVelocity[d] = W * oldVelocity[d]
                        + C1 * this.rand.nextDouble() * (particleBestPosition[d] - oldPosition[d])
                        + C2 * this.rand.nextDouble() * (gBestPosition[d] - oldPosition[d]);

                // Define a velocidade mínima e máxima
                if (newVelocity[d] < PSO_config.getMinvelocity()) {
                    newVelocity[d] = PSO_config.getMinvelocity();
                } else if (newVelocity[d] > PSO_config.getMaxvelocity()) {
                    newVelocity[d] = PSO_config.getMaxvelocity();
                }

                // Atualiza a posição da partícula pela fórmula do PSO global best na dimensão d
                newPosition[d] = oldPosition[d] + newVelocity[d];

                // Define a posição mínima e máxima
                if (newPosition[d] < PSO_config.getMinposition()) {
                    newPosition[d] = PSO_config.getMinposition();
                } else if (newPosition[d] > PSO_config.getMaxposition()) {
                    newPosition[d] = PSO_config.getMaxposition();
                }
            }
            particle.setVelocity(newVelocity);
            particle.setPosition(newPosition);

        }

    }

    /*
     * Retorna as partículas da população
     */
    public PSO_particle[] getParticles() {
        return particles;
    }

    /* 
     * Retorna a melhor partícula da população
     */
    public PSO_particle getBestParticle() {
        return bestParticle;
    }

    /*
     * Atribui a melhor partícula da população
     */
    private void setBestParticle(PSO_particle bestParticle) {
        this.bestParticle = bestParticle;
    }

    /*
     * Retorna a melhor posição da população
     */
    private double[] getGBestPosition() {
        return bestPosition;
    }

    /*
     * Atribui a melhor posição da população
     */
    private void setBestPosition(double[] bestPosition) {
        this.bestPosition = bestPosition;
    }

    /*
     * Retorna o melhor custo da população
     */
    private double getBestCost() {
        return bestCost;
    }

    /*
     * Atribui o melhor custo da população
     */
    private void setBestCost(double bestCost) {
        this.bestCost = bestCost;
    }
}
