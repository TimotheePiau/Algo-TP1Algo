import java.util.ArrayList;
import java.util.LinkedList;

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
    
    // Modifier le sens de prioritée des listes?
    
    public int[][] parcoursProfondeur(int startIndex)
	{
		int currentDate = 1;
		
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
    
    public void exploreAndDate(int currentIndex,int currentDate, int[][] parcours)
	{
		if(parcours[currentIndex][1]!=-1)
			return;
		else
		{
			parcours[currentIndex][1]=currentDate;
			currentDate++;
			for(Edge e : incidency.get(currentIndex))
			{
				exploreAndDate(e.source, currentDate, parcours);
			}
			parcours[currentIndex][2]=currentDate;
			currentDate++;
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
    public ArrayList<ArrayList<Integer>> searchComposantesConexes()
    {
    	ArrayList<ArrayList<Integer>> composantesConexes = new ArrayList<ArrayList<Integer>>(cardinal);
    	
    	int dateParcours[][] = parcoursProfondeur(0);
    	
    	ArrayList<Integer> summitByResolvingDate = new ArrayList<Integer>(cardinal);
    	int maxValue = cardinal * 2 + 1;
    	
    	for(int i=0; i<cardinal; i++)
    	{
    		int actualMax = 0;
    		for(int j=0; j<cardinal; j++)
    		{
    			if( (dateParcours[j][2]>actualMax) && (dateParcours[j][2]<maxValue))
    			{
    				summitByResolvingDate.add(j);
    				maxValue=dateParcours[j][2];
    			}
    		}
    	}
    	
    	Graph<Label> transposedGarph = this.graphT();
    	
    	boolean discoveredSummit[] = new boolean[cardinal];
    	for (int i=0; i<cardinal; i++)
    	{
    		discoveredSummit[i] = false;
    	}
    	int arbreIndex = 0;
    	
    	while( !summitByResolvingDate.isEmpty() )
		{
    		
    		arbreIndex++;
    		ArrayList<Integer> arbre = new ArrayList<Integer>();
    		explore( summitByResolvingDate.get(0), discoveredSummit, arbre, transposedGarph);
    		composantesConexes.get(arbreIndex).add(arbre);
    		
    		for(int dS : arbre)
    		{
    			summitByResolvingDate.remove(dS);
    		}
    		
		}
    	
    	return composantesConexes;
    }
    
    public void explore(int startIndex, boolean[] discoveredSummit, ArrayList<Integer> arbre, Graph<Label> studiedGraph)
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
				explore(e.source, discoveredSummit, arbre, studiedGraph);
			}
		}
	}
}

