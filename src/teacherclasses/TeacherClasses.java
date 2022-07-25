/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package teacherclasses;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author saplab
 */
public class TeacherClasses {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        ArrayList<Solution> results = new ArrayList<>();
        // TODO code application logic here
        for (int i = 0; i < 45; i++){
            Data data = Data.readDataFromFile();
            GeneticAlgorithmImplementer Ga = new GeneticAlgorithmImplementer(data);
            ArrayList<Solution> result = Ga.implementGA();
            results.add(result.get(result.size()-1));
            GeneticAlgorithmImplementer.addResult(result.get(result.size()-1), data);
        }
        
        //GeneticAlgorithmImplementer.writeSolutionAsTimetable(result.get(result.size()-1), data);
        //GeneticAlgorithmImplementer.writeErrCourseToExcel(result.get(result.size()-1), data);
        
    }

}
