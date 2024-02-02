package PSO;

public class PSO_particle {

    private double[] position;
    private double[] velocity;
    private double[] bestPosition;
    private double bestCost;
    private double cost;
    private int particleIndex;

    public PSO_particle(int dimension, int particleId) {
        particleIndex = particleId;
        position = this.generateRandomPosition(dimension, PSO_config.getMinposition(), PSO_config.getMaxposition());
        velocity = this.generateRandomPosition(dimension, PSO_config.getMinvelocity(), PSO_config.getMaxvelocity());
        bestPosition = position.clone();
        bestCost = Double.MAX_VALUE;
        cost = Double.MAX_VALUE;
    }

    /*
     * Gera uma posição aleatória para a partícula dentro de um intervalo
     */
    private double[] generateRandomPosition(int dimension, double lowerBound, double upperBound) {
        double[] randomPosition = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            randomPosition[i] = lowerBound + Math.random() * (upperBound - lowerBound);
        }
        return randomPosition;
    }

    /*
     * Cria a representação da partícula em forma de string
     */
    public String toString() {
        String particle = "Partícula " + particleIndex + " | Custo: " + cost + "\n";
        particle += "Posição: [";   
        for (int i = 0; i < position.length -1; i++) {
            particle += position[i] + " ;";
        }
        particle += position[position.length - 1] + "]\n";

        return particle;
    }


    /*
     * Define a posição da partícula
     */
    public void setPosition(double[] position) {
        this.position = position;
    }

    /*
     * Define a velocidade da partícula
     */
    public void setVelocity(double[] velocity) {
        this.velocity = velocity;
    }

    /*
     * Define a melhor posição da partícula
     */
    public void setBestPosition(double[] bestPosition) {
        this.bestPosition = bestPosition;
    }

    /*
     * Define o melhor custo da partícula
     */
    public void setBestCost(double bestCost) {
        this.bestCost = bestCost;
    }

    /*
     * Define o custo da partícula
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /*
     * Retorna a posição da partícula
     */
    public double[] getPosition() {
        return position;
    }

    /*
     * Retorna a velocidade da partícula
     */
    public double[] getVelocity() {
        return velocity;
    }

    /*
     * Retorna a melhor posição da partícula
     */
    public double[] getBestPosition() {
        return bestPosition;
    }

    /*
     * Retorna o melhor custo da partícula
     */
    public double getBestCost() {
        return bestCost;
    }

    /*
     * Retorna o custo da partícula
     */
    public double getCost() {
        return cost;
    }
}
