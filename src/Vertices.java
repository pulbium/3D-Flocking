public class Vertices {

	float[][] vertices;
	int n;
	
	public Vertices(int n, float a) {
		this.n = n;
		double k = Math.pow(2, n);
		vertices = new float[(int)k][n];
		
		for(int i = 0;i < k;i++) {
			for(int j = 0;j < n;j++)
				vertices[i][j] = a;
			}
		createVertices();
	}
	
	void createVertices(){
		for(int i = 0;i < Math.pow(2,n);i++) {
			for(int j = 0;j < n;j++) {
				if(!(i%(int)(Math.pow(2, j+1))<(int)(Math.pow(2, j))))
					vertices[i][j]*=-1;
			}		
		}
	}
	public static void main(String[] args) {
		Vertices v = new Vertices(3, 1);
		
		for(int i = 0; i < 8; i++) {
			System.out.print("\n");
			for(int j = 0; j < 3 ;j++)
				System.out.print(v.vertices[i][j] + " ");
		}
	}
}
