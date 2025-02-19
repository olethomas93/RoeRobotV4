package TSP;

import static java.lang.Math.abs;
import RoeRobot.Coordinate;

/**
 * This class does a Genetic Algorithm
 * @author Yngve
 */
public class GAnew {

    private static final double mutationRate = 0.01;
    private static final int tournamentSize = 9;
    private static final boolean elitism = true;

    public GAnew() {
    }

    /**
     * // Create a new population witch will contain the new and improved
     * chromosomes. // Select two parants for mating. This two can be selected
     * by random or select the fittest of the initial population Evolve for
     * given number of iterations.
     *
     * @param evolutions
     * @param pop
     * @return
     */
    public Population evolvePopulation(long evolutions, Population pop, Tour originalTour) {
        Population newPopulation = new Population(pop.getNrOfTours());
        Coordinate startCoord = pop.getFittest().getCoordinate(0);        
        Tour currentFittestTour = pop.getFittest();
        
        
        // Result for stesting. 
         double[] result; 
        // Boolen for detectiong no improvement. 
        boolean noImprovement = false;
        int noImprovemnetRange = 0;

        int cnt = 0;

        // Keep our best individual if elitism is enabled
        int elitismOffset = 0;
        if (elitism) {
            newPopulation.saveTour(0, pop.getFittest());
            newPopulation.saveTour(1, originalTour);
            elitismOffset = 2;
        }

        int j = 0;
        while (evolutions > j++ && !noImprovement) {
            for (int i = elitismOffset; i < newPopulation.getNrOfTours(); i++) {
                // Chose pair of chromosmes for amtiong
                Tour parentOne = this.turnamentSelection(pop);
                Tour parentTwo = this.turnamentSelection(pop);
                if (parentOne.getList().contains(null) || parentTwo.getList().contains(null)) {
                    System.out.println("Fuck You parrent");
                }
                // Preforme crossover with Pc possebility 
                // Tour child = this.crossover(parrentOne, parrentTwo, parrentOne.tourSize());
                Tour childOne = this.crossoverWithStartCoord(parentOne, parentTwo, parentOne.tourSize(), startCoord);
                Tour childTwo = this.crossoverWithStartCoord(parentOne, parentTwo, parentOne.tourSize(), startCoord);
                // System.out.println(startCoord);
                // Add child to new population
                newPopulation.saveTour(i++, childOne);
                newPopulation.saveTour(i, childTwo);
            }
//
            // Mutate the new population a bit to add some new genetic material
            for (int i = 0; i < newPopulation.getNrOfTours(); i++) {
                mutateWithStartCoord(newPopulation.getTour(i));
            }


            // Update population. 
            pop = newPopulation;
            
        }

        return newPopulation;
    }

    /**
     * Croosover Arranged Crossover Will mace a child of two parrents. It tacker
     * a random range of coordiantes and add them to a child. The missing
     * coordinates are set by the other parrant by checkin if the child are
     * missing a coordinate. If the child misses a coordinate it will be plased
     * at an empti sopot.
     */
    private Tour crossover(Tour parrentOne, Tour parrentTwo, int tourSize) {
        Tour child = new Tour(tourSize);
        // Get start and end sub tour positions for parent1's tour
        int startCrossoverPoint = (int) (abs(Math.random() * tourSize));
        int endCrossoverPoint = (int) (abs(Math.random() * tourSize));

        // Loop and add the sub tour from parent1 to our child
        for (int i = 0; i < tourSize; i++) {
            // If our start position is less than the end position
            if (startCrossoverPoint < endCrossoverPoint && i > startCrossoverPoint && i < endCrossoverPoint) {
                child.setCoordinate(i, parrentOne.getCoordinate(i));
            } // If our start position is larger
            else if (startCrossoverPoint > endCrossoverPoint) {
                if (!(i < startCrossoverPoint && i > endCrossoverPoint)) {
                    child.setCoordinate(i, parrentOne.getCoordinate(i));
                }
            }
        }
        // If the child have an empty spot. 
        if (child.getList().contains(null)) {
            // Loop throug all positions in the child 
            for (int i = 0; i < child.tourSize(); i++) {
                // Check if the position is empty 
                if (child.getCoordinate(i) == null) {
                    // Loop throug all coodinates holded by parrent two
                    for (int k = 0; k < parrentTwo.tourSize(); k++) {
                        // Find a coodrinate which the child are missing 
                        if (!child.containsCoordinate(parrentTwo.getCoordinate(k))) {
                            // add the missing child. 
                            child.setCoordinate(i, parrentTwo.getCoordinate(k));
                            break;
                        }
                    }
                }
            }
        }

        return child;
    }

    /**
     * Croosover Arranged Crossover
     */
    private Tour crossoverWithStartCoord(Tour parentOne, Tour parentTwo, int tourSize, Coordinate startCoord) {
        Tour child = new Tour(tourSize);
        child.setCoordinate(0, startCoord);
        // Get start and end sub tour positions for parent1's tour
        int startCrossoverPoint = (int) (abs(Math.random() * tourSize - 1) + 1);
        int endCrossoverPoint = (int) (abs(Math.random() * tourSize - 1) + 1);

        // Loop and add the sub tour from parent1 to our child
        for (int i = 1; i < tourSize; i++) {
            // If our start position is less than the end position
            if (startCrossoverPoint < endCrossoverPoint && i > startCrossoverPoint && i < endCrossoverPoint) {
                child.setCoordinate(i, parentOne.getCoordinate(i));
            } // If our start position is larger
            else if (startCrossoverPoint > endCrossoverPoint) {
                if (!(i < startCrossoverPoint && i > endCrossoverPoint)) {
                    child.setCoordinate(i, parentOne.getCoordinate(i));
                }
            }
        }
        // Loop through all parentTwo's coordinates
        for (int i = 1; i < child.tourSize(); i++) {
            // If the child have an empty spot. 
            if (child.getList().contains(null)) {
                if (child.getCoordinate(i) == null) {
                    for (int k = 0; k < parentTwo.tourSize(); k++) {
                        if (!child.containsCoordinate(parentTwo.getCoordinate(k))) {
                            child.setCoordinate(i, parentTwo.getCoordinate(k));
                            break;
                        }
                    }
                }
            }

        }
        if (child.getList().contains(null)) {
            System.out.println("Fuck You child");
            this.crossoverWithStartCoord(parentOne, parentTwo, tourSize, startCoord);
        }
        return child;
    }

    /**
     * Mutate a tour using swap mutation The start coordinate will ceaped ass
     * first in list.
     */
    private static void mutateWithStartCoord(Tour tour) {
        // Loop through tour cities
        for (int tourPos1 = 1; tourPos1 < tour.tourSize(); tourPos1++) {
            // Apply mutation rate
            double randNr = Math.random();

            if (randNr < mutationRate) {
                // Get a second random position in the tour
                int tourPos2 = (int) (abs(Math.random() * tour.tourSize() - 1) + 1);
                // Get the coordinates at target position in tour
                Coordinate coord1 = tour.getCoordinate(tourPos1);
                Coordinate coord2 = tour.getCoordinate(tourPos2);
                // Swap them around
                tour.setCoordinate(tourPos2, coord1);
                tour.setCoordinate(tourPos1, coord2);
                //  System.out.println("I mutated the shit out of you. ");
            }
        }
    }

    /**
     * *
     * Rouletwheel selection.
     */
    
    
    /**
     * Turnement selection will retun
     */
    private Tour turnamentSelection(Population pop) {

        // Create a tournament population
        Population turnamentPop = new Population(this.tournamentSize);
        // For each place in the tournament get a random candidate tour and
        // add it
        for (int i = 0; i < this.tournamentSize; i++) {
            int randCromosomId = (int) (Math.random() * pop.getNrOfTours());
            // Return fittest tour for turnement. 
            turnamentPop.saveTour(i, pop.getTour(randCromosomId));
        }
        return turnamentPop.getFittest();
    }

}
