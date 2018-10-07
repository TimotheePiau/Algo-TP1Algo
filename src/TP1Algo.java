import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class TP1Algo 
{

	public static void main(String[] args) throws FileNotFoundException
	{
		Graph<String> formulasGraph;
		formulasGraph = graphFromFormula("./src/TP1/Formula2.txt");
		System.out.print(formulasGraph.toString());
	}
	
	
	public static Graph<String> graphFromFormula(String fileName) throws FileNotFoundException
	{
		
		File formulaFile = new File(fileName);
		Scanner formula = new Scanner(formulaFile);
		
		int size = formula.nextInt()*2;
		
		Graph<String> formulasGraph = new Graph<String>(size);
		
	    while (formula.hasNextInt()) 
	    {
	    	int literal1 = formula.nextInt(); 
	    	int literal2 = formula.nextInt();
	    	
	    	formulasGraph.addArc( literalToIndex(-literal1), literalToIndex(literal2), "");
	    	formulasGraph.addArc( literalToIndex(-literal2), literalToIndex(literal1), "");
	    	
	    }
	    formula.close();
	    return formulasGraph;
	}
	
	public static int literalToIndex(int literal)
	{
		if(literal<0)
		{
			return (-2*literal-1);
		}
		else
		{
			return (2*(literal-1));
		}		
	}

}
