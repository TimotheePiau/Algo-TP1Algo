import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Graph<Label> 
{
	// Classe Edge defini les arretes
    private class Edge 
    {
        public int source;
        public int destination;
        public Label label;

        public Edge(int from, int to, Label label) 
        {
            this.source = from;
            this.destination = to;
            this.label = label;
        }
    }

    
    public int cardinal;
    private ArrayList<LinkedList<Edge>> incidency;


    public Graph(int size) 
    {
        cardinal = size;
        incidency = new ArrayList<LinkedList<Edge>>(size+1);  				//size+1???
        for (int i = 0;i<cardinal;i++) 
        {
            incidency.add(i, new LinkedList<Edge>());
        }
    }

    public int order() 
    {
        return cardinal;
    }

    public void addArc(int source, int dest, Label label) 
    {
        incidency.get(source).addLast(new Edge(source,dest,label));
    }

    public String toString() 
    {
        String result = new String("");
        result=result.concat(cardinal + "\n");
        for (int i = 0; i<cardinal;i++) 
        {
            for (Edge e : incidency.get(i)) 
            {
                result=result.concat(e.source + " " + e.destination + " " + e.label.toString() + "\n");          
            }
        }
        return result;

    }

    public interface ArcFunction<Label,K> 
    {
        public K apply(int source, int dest, Label label, K accu);
    }

    public interface ArcConsumer<Label> 
    {
        public void apply(int source, int dest, Label label);
    }

    public <K> K foldEdges(ArcFunction<Label,K> f, K init) 
    {
        for (LinkedList<Edge> adj : this.incidency) 
        {
            for (Edge e : adj) 
            {
                init = f.apply(e.source, e.destination, e.label, init);
            }
        };
        return init;
    }

    public void iterEdges(ArcConsumer<Label> f) 
    {
        for (LinkedList<Edge> adj : this.incidency) 
        {
            for (Edge e : adj) 
            {
                f.apply(e.source, e.destination, e.label);
            }
        }
    }
    
    private static int literalToIndex(int literal)
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
	
	private int indexToLiteral(int index)
	{
		if(index%2==0)
		{
			return index/2+1;
		}
		else
		{
			return (index+1)/(-2);
		}
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
    // Modifier le sens de prioritée des listes?
    
    public int[][] parcoursProfondeur(int startIndex)
	{
		int currentDate[] = new int[1];
		currentDate[0]=1;
		
		int parcours[][] = new int[this.cardinal][2];
		
		for(int initIndex=0; initIndex<this.cardinal; initIndex++)
		{
			parcours[initIndex][0]=-1;
			parcours[initIndex][1]=-1;
		}
		
		exploreAndDate(startIndex, currentDate, parcours);
		
		for(int i=0; i<this.cardinal; i++)
		{
			exploreAndDate(i, currentDate, parcours);
		}
		
		return parcours;
	}
    
    public void exploreAndDate(int currentIndex,int[] currentDate, int[][] parcours)	// Vive les wrappers qui servent à rien
	{
		if(parcours[currentIndex][0]!=-1)
			return;
		else
		{
			parcours[currentIndex][0]=currentDate[0];
			currentDate[0]++;
			for(Edge e : incidency.get(currentIndex))
			{
				exploreAndDate(e.destination, currentDate, parcours);
			}
			parcours[currentIndex][1]=currentDate[0];
			currentDate[0]++;
		}
	}
    
    public Graph<Label> graphT()
    {
    	Graph<Label> transposedGraph = new Graph<Label>(cardinal);
    	for(int i=0; i<cardinal; i++)
    	{
    		for(Edge e : incidency.get(i))
    		{
    			transposedGraph.incidency.get(e.destination).addLast(new Edge(e.destination, e.source, e.label));
    		}
    	}
    	
    	// methode de tri
    	
    	return transposedGraph;
    }
    
    //Methode Composantes fortements conexes
    public ArrayList<ArrayList<Integer>> composantesConexes()
    {
    	ArrayList<ArrayList<Integer>> composantesConexes = new ArrayList<ArrayList<Integer>>(cardinal);
    	
    	int dateParcours[][] = parcoursProfondeur(0);
    	
    	ArrayList<Integer> summitByResolvingDate = new ArrayList<Integer>(cardinal);
    	int maxValue = cardinal * 2 + 1;
    	
    	for(int i=0; i<cardinal; i++)
    	{
    		int actualMax = 0;
    		int actualMaxIndex=-1;
    		for(int j=0; j<cardinal; j++)
    		{
    			if( (dateParcours[j][1]>actualMax) && (dateParcours[j][1]<maxValue))
    			{
    				actualMax=dateParcours[j][1];
    				actualMaxIndex=j;
    			}
    		}
    		summitByResolvingDate.add(actualMaxIndex);
			maxValue=dateParcours[actualMaxIndex][1];
    	}
    	
    	Graph<Label> transposedGarph = this.graphT();
    	
    	boolean discoveredSummit[] = new boolean[cardinal];
    	for (int i=0; i<cardinal; i++)
    	{
    		discoveredSummit[i] = false;
    	}
    	
    	while( !summitByResolvingDate.isEmpty() )
		{
    		
    		ArrayList<Integer> arbre = new ArrayList<Integer>();
    		explore( summitByResolvingDate.get(0), discoveredSummit, arbre, transposedGarph);
    		composantesConexes.add(arbre);
    		
    		for(Integer dS : arbre)
    		{
    			summitByResolvingDate.remove(dS);
    		}
    		
		}
    	
    	return composantesConexes;
    }
    
    private void explore(int startIndex, boolean[] discoveredSummit, ArrayList<Integer> arbre, Graph<Label> studiedGraph)
	{
		int currentIndex = startIndex;
    	if(discoveredSummit[currentIndex])
			return;
		else
		{
			discoveredSummit[currentIndex]=true;
			arbre.add(currentIndex);
			for(Edge e : studiedGraph.incidency.get(currentIndex))
			{
				explore(e.destination, discoveredSummit, arbre, studiedGraph);
			}
		}
	}
    
    public void verifySAT(ArrayList<ArrayList<Integer>> composantesConexes)
    {
    	int litCheck[] = new int[cardinal/2]; 
    	
    	
    	for(ArrayList<Integer> cc : composantesConexes)
    	{
    		for (int i=0; i<(cardinal/2); i++)
        	{
        		litCheck[i]=0;
        	}
    		
    		for (int lit : cc)
    		{
    			int litMem = this.indexToLiteral(lit);
    			litCheck[Math.abs(litMem)-1]++;
    			if(litCheck[Math.abs(litMem)-1]>1)
    			{
    				System.out.print("La formule Sat n'est pas satifaisable");
    				return;
    			}
    		}
    	}
    	
    	System.out.print("La formule SAT est satifaisable");
    }
   
}

