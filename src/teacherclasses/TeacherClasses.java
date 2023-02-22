/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package teacherclasses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author saplab
 */
public class TeacherClasses {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // Read data from excel
        Data data = Data.readDataFromFile();
        
        GeneticAlgorithmImplementer ga = new GeneticAlgorithmImplementer(data);
        
        for(int i=1;i<=15;i++){
            //Start algorithms
        ArrayList<Solution> result = ga.implementGA();
        
        //Write results to excel        
        GeneticAlgorithmImplementer.writeSolutions(result, data, ga.time,"Sheet"+i);
        GeneticAlgorithmImplementer.writeSolutionAsTimetable(result.get(result.size()-1), data, "Schedule-"+i);
        }
        
    }
}
