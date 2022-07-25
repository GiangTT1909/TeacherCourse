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
        // TODO code application logic here
        Data data = Data.readDataFromFile();
        
        DistributedRandomNumberGenerator rnd = new DistributedRandomNumberGenerator();
        //ArrayList<Solution> solution = new ArrayList<>();
        GeneticAlgorithmImplementer Ga = new GeneticAlgorithmImplementer(data);
        ArrayList<Solution> result = Ga.implementGA();
//        for (int i = 0; i < 40; i++) {
//            data.w1 = getRandomNumber(1, 9);
//            data.w2 = getRandomNumber(1, 9);
//            data.w3 = getRandomNumber(1, 9);
//            data.w4 = getRandomNumber(1, 9);
//            data.w5 = getRandomNumber(1, 9);
//            data.w6 = getRandomNumber(1, 9);
//            GeneticAlgorithmImplementer Ga = new GeneticAlgorithmImplementer(data);
//            ArrayList<Solution> result = Ga.implementGA();
//            solution.add(result.get(result.size() - 1));
//        }

        for (Solution solution : result) {
            System.out.println(solution.all_Courses_Contraints);
            System.out.println(solution.single_Teacher_Courses_Contraints);
            System.out.println(solution.single_Slot_Contraints);
            System.out.println(solution.inRange_Slot_Contraints);
            System.out.println(solution.student_Rating_Constraint);
            System.out.println(solution.self_Rating_Constraint);
            System.out.println(solution.slot_Rating_Constraint);

            //System.out.println(solution.sum);
            System.out.println(solution.cal_Fitness(data));
        }
        GeneticAlgorithmImplementer.writeSolutions(result, data);
          GeneticAlgorithmImplementer.writeSolutionAsTimetable(result.get(result.size()-1), data);
       
    }

    public static double getRandomNumber(int min, int max) {
        return ((Math.random() * (max - min)) + min);
    }

}
