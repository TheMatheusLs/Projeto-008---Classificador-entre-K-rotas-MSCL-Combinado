package PSO;

public class PSO_config {
    
    final static int PSRTerms = 2; // Número de termos no somatório do PSR
    final static int PSRVariable = 2; // Número de variáveis no somatório do PSR
    
    final static int dimension = 25; // (N ^ 2 + 1) * PSRVariable
    final static int numberOfParticles = 50;
    final static int maxIterations = 500;
    final static double w = 0.5;
    final static double c1 = 2.05;
    final static double c2 = 2.05;
    final static double minVelocity = -0.3;
    final static double maxVelocity = 0.3;
    final static double minPosition = 0;
    final static double maxPosition = 1;
    final static double networkLoad = 280;
    
    /*
     * Retorna o número de termos no somatório do PSR
     */
    public static int getPsrterms() {
        return PSRTerms;
    }

    /*
     * Retorna a carga da rede
     */
    public static double getNetworkload() {
        return networkLoad;
    }

    /*
     * Retorna a dimensão do problema
     */
    public static int getDimension() {
        return dimension;
    }

    /*
     * Retorna o número de partículas
     */
    public static int getNumberofparticles() {
        return numberOfParticles;
    }

    /*
     * Retorna o número máximo de iterações
     */
    public static int getMaxiterations() {
        return maxIterations;
    }

    /*
     * Retorna o valor do coeficiente de inércia (w)
     */
    public static double getW() {
        return w;
    }

    /*
     * Retorna o valor do coeficiente cognitivo (c1)
     */
    public static double getC1() {
        return c1;
    }

    /*
     * Retorna o valor do coeficiente social (c2)
     */
    public static double getC2() {
        return c2;
    }

    /*
     * Retorna a velocidade mínima de uma partícula
     */
    public static double getMinvelocity() {
        return minVelocity;
    }

    /*
     * Retorna a velocidade máxima de uma partícula
     */
    public static double getMaxvelocity() {
        return maxVelocity;
    }

    /*
     * Retorna a posição mínima de uma partícula
     */
    public static double getMinposition() {
        return minPosition;
    }

    /*
     * Retorna a posição máxima de uma partícula
     */
    public static double getMaxposition() {
        return maxPosition;
    }
}
