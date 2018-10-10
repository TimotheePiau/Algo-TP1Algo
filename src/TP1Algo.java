import java.io.FileNotFoundException;
import java.util.ArrayList;

public class TP1Algo 
{

	public static void main(String[] args) throws FileNotFoundException
	{
		Graph<String> formulasGraph;
		formulasGraph = Graph.graphFromFormula("./src/TP1/unsat1000.txt");
		//System.out.print(formulasGraph.toString());
		ArrayList<ArrayList<Integer>> formulaCC = new ArrayList<ArrayList<Integer>>();
		formulaCC = formulasGraph.composantesConexes();
		formulasGraph.verifySAT(formulaCC);
	}

}
