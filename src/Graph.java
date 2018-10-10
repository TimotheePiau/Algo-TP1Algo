import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class Graph<Label> 
{
	// Classe Edge defini les arcs
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

    private int cardinal;								// Nombre de litteraux
    private ArrayList<LinkedList<Edge>> incidency;		// Liste des listes d'adjacence de chacun des sommets

    
    // Contructeur de la classe Graph
    public Graph(int size) 
    {
        cardinal = size;
        incidency = new ArrayList<LinkedList<Edge>>(size+1);  				
        for (int i = 0;i<cardinal;i++) 
        {
            incidency.add(i, new LinkedList<Edge>());
        }
    }
    
    // "Get" de cardinal
    public int order() 
    {
        return cardinal;
    }
    
    // Méthode gérant l'ajout d'un nouvel arc au graphe
    public void addArc(int source, int dest, Label label) 
    {
        incidency.get(source).addLast(new Edge(source,dest,label));
    }
    
    // Méthode de formatage du graph en données lisibles (Renvoie un string contenant un arc par ligne" 
    public String toString() 
    {
        String result = new String("");
        result=result.concat(cardinal + "\n");
        for (int i = 0; i<cardinal;i++) 
        {
            for (Edge e : incidency.get(i)) 
            {
                result=result.concat(e.source + " -->" + e.destination + " " + e.label.toString() + "\n");          
            }
        }
        return result;

    }
    
    // Methode d'indexation
    // Fait correspondre un litteral a un index de la liste incidency
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
	
    // Methode Inverse de literalToIndex
    // Retourne le literral originel correspondant à l'index
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
    // Methode de contruction du graphe
	// Renvoie le graphe fini
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
    
    // Methode du parcours en profondeur classique
    // Renvoie une matrice de taille [cardinal] x [2] contenant les dates de découverte et de fin
    // Les lignes correspondent à chacun des sommet sur la même inexation que le graphe
    // La première colonne [0] correspond a la date de decouverte
    // La seconde colonne [1] correspond a la date de fin
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
    
    // Méthode d'exploration recursive utilise par la méthode parcoursProfondeur
    // Check si le sommet est découvert, si oui sort de la méthode, 
    // si non enregiste la date de decouverte dans parcours et se relance recucivement sur les sommets adjacents
    public void exploreAndDate(int currentIndex,int[] currentDate, int[][] parcours)	
	{
		if(parcours[currentIndex][0]!=-1)
			return;
		parcours[currentIndex][0]=currentDate[0];
		currentDate[0]++;
		for(Edge e : incidency.get(currentIndex))
		{
			exploreAndDate(e.destination, currentDate, parcours);
		}
		parcours[currentIndex][1]=currentDate[0];
		currentDate[0]++;
	}
    
    // Methode de transposition du graphe courrant
    // Retourne un nouveau graphe correspondant à la transposé du graph initial
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
    	
    	return transposedGraph;
    }
    
    // Methode determinant les Composantes fortements conexes du graph
    // Retourne une liste de composantes conexes elles même sous la forme d'une liste de sommets
    public ArrayList<ArrayList<Integer>> composantesConexes()
    {
    	ArrayList<ArrayList<Integer>> composantesConexes = new ArrayList<ArrayList<Integer>>(cardinal);			// Variables utilisée pour le retour
    	
    	int dateParcours[][] = parcoursProfondeur(0);
    	
    	ArrayList<Integer> summitByResolvingDate = new ArrayList<Integer>(cardinal);			// Assimillable à une to do list qui contiendra les sommets par ordre de date de fin decroissante
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
    	
    	boolean discoveredSummit[] = new boolean[cardinal];							// Tableau definissant si un sommet à déjà été découvert
    	for (int i=0; i<cardinal; i++)
    	{
    		discoveredSummit[i] = false;
    	}
    	
    	while( !summitByResolvingDate.isEmpty() )
		{
    		
    		ArrayList<Integer> arbre = new ArrayList<Integer>();									// Liste qui receuillera les differents sommets d'une composante fortement conexe
    		explore( summitByResolvingDate.get(0), discoveredSummit, arbre, transposedGarph);
    		composantesConexes.add(arbre);
    		
    		for(Integer dS : arbre)																	// Les sommets traité sont enlevé de la 'to do liste'
    		{
    			summitByResolvingDate.remove(dS);
    		}
    		
		}
    	
    	return composantesConexes;
    }
    
    // Méthode d'exploration sans date
    // Utilisé pour découvrir les composantes fortement conexes à partir du graphe transposé
    // arbre corespond la composante conexe en train d'être explorée
    private void explore(int startIndex, boolean[] discoveredSummit, ArrayList<Integer> arbre, Graph<Label> studiedGraph)
	{
		int currentIndex = startIndex;
    	if(discoveredSummit[currentIndex])
			return;
		discoveredSummit[currentIndex]=true;
		arbre.add(currentIndex);
		for(Edge e : studiedGraph.incidency.get(currentIndex))
		{
			explore(e.destination, discoveredSummit, arbre, studiedGraph);
		}
	}
    
    // Methode qui retourne si la formule 2 SAT est satisfiable
    // Defini un tableau ou chaque case correspond à un litteral et à sa négation
    // Parcours chacun des litteraux d'une composante conexe et compte dans la bonne case les occurence de chaque littéraux
    // Si un compte est superieur a un cela signifie que un litteral et son contraire sont présent dans la même composante fortement conexe => Non satisfiable
    // Affiche dans la console si la formule est satisfiable ou non
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

