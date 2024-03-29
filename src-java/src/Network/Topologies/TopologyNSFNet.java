package Network.Topologies;

public class TopologyNSFNet extends TopologyGeneral{
    /**
     * Quantidade de Nós presentes na topologia
     */
    private static int numberOfNodes = 14;

    public TopologyNSFNet(){
        super(getLength());
    }

    /**
	 * Tamanho dos links da rede NSFNet
	 * 
     * @return
     */
    public static double[][] getLength(){
        //create network adjacency matrix
		double[][] lengths = new double[numberOfNodes][numberOfNodes];			
		for(int x = 0; x < numberOfNodes; x++){
			for(int y = 0 ; y < numberOfNodes; y++){
				lengths[x][y] = Double.MAX_VALUE;
			}
		}
		
		// lengths[0][1]   = 300;
		// lengths[1][0]   = 300;
		// lengths[0][2]   = 300;
		// lengths[2][0]   = 300;
		// lengths[0][3]   = 300;
		// lengths[3][0]   = 300;
		// lengths[1][2]   = 400;
		// lengths[2][1]   = 400;	
		// lengths[1][7]   = 800;
		// lengths[7][1]   = 800;		
		// lengths[2][5]   = 400;
		// lengths[5][2]   = 400;		
		// lengths[3][4]   = 200;
		// lengths[4][3]   = 200;		
		// lengths[3][10]  = 1000;
		// lengths[10][3]  = 1000;      
		// lengths[4][5]   = 300;
		// lengths[5][4]   = 300;
		// lengths[4][6]   = 200;
		// lengths[6][4]   = 200;
		// lengths[5][9]   = 600;
		// lengths[9][5]   = 600;
		// lengths[5][13]  = 700;
		// lengths[13][5]  = 700;
		// lengths[6][7]   = 200;
		// lengths[7][6]   = 200;		
		// lengths[7][8]   = 200;
		// lengths[8][7]   = 200;		
		// lengths[8][9]   = 700;
		// lengths[9][8]   = 700;	
		// lengths[8][11]  = 400;
		// lengths[11][8]  = 400;	
		// lengths[8][12]  = 500;
		// lengths[12][8]  = 500;		
		// lengths[10][11] = 300;
		// lengths[11][10] = 300;
		// lengths[10][12] = 500;
		// lengths[12][10] = 500;
		// lengths[11][13] = 500;
		// lengths[13][11] = 500;
		// lengths[12][13] = 300;
		// lengths[13][12] = 300;
		lengths[0][1]   = 1400;
		lengths[1][0]   = 1400;
		lengths[2][0]   = 800;
		lengths[0][2]   = 800;
		lengths[0][3]   = 1200;
		lengths[3][0]   = 1200;
		lengths[1][2]   = 2000;
		lengths[2][1]   = 2000;	
		lengths[7][1]   = 3400;		
		lengths[1][7]   = 3400;
		lengths[5][2]   = 2400;		
		lengths[2][5]   = 2400;
		lengths[3][4]   = 800;
		lengths[4][3]   = 800;		
		lengths[3][10]  = 2600;
		lengths[10][3]  = 2600;      
		lengths[4][5]   = 1700;
		lengths[5][4]   = 1700;
		lengths[4][6]   = 800;
		lengths[6][4]   = 800;
		lengths[5][9]   = 1300;
		lengths[9][5]   = 1300;
		lengths[5][13]  = 2300;
		lengths[13][5]  = 2300;
		lengths[6][7]   = 800;
		lengths[7][6]   = 800;		
		lengths[7][8]   = 800;
		lengths[8][7]   = 800;		
		lengths[8][9]   = 1100;
		lengths[9][8]   = 1100;	
		lengths[8][11]  = 500;
		lengths[11][8]  = 500;	
		lengths[8][12]  = 600;
		lengths[12][8]  = 600;		
		lengths[10][11] = 800;
		lengths[11][10] = 800;
		lengths[10][12] = 1000;
		lengths[12][10] = 1000;
		lengths[11][13] = 500;
		lengths[13][11] = 500;
		lengths[12][13] = 300;
		lengths[13][12] = 300;

		return lengths;
    }
}
