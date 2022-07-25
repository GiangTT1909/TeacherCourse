/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacherclasses;

import java.util.ArrayList;

/**
 *
 * @author ACER
 */
public class NSGA {

    public Data data;

    public NSGA(Data data) {
        this.data = data;
    }

    public void fast_nondominated_sort(Population population) {
        population.fronts = new ArrayList<ArrayList<Solution>>();
        ArrayList<Solution> lst = new ArrayList<Solution>();
        for (Solution individual : population.population) {
            individual.domination_count = 0;
            individual.dominated_solution = new ArrayList<Solution>();

            for (Solution other_individual : population.population) {
                if (individual.dominates(other_individual)) {
                    individual.dominated_solution.add(other_individual);
                } else {
                    if (other_individual.dominates(individual)) {
                        individual.domination_count++;
                    }
                }
            }

            if (individual.domination_count == 0) {
                individual.rank = 0;
                lst.add(individual);

            }

        }
        population.fronts.add(0, lst);

        int i = 0;
        while (population.fronts.get(i).size() > 0) {
            lst = new ArrayList<Solution>();
            for (Solution individual : population.fronts.get(i)) {
                for (Solution other_individual : individual.dominated_solution) {

                    other_individual.domination_count--;

                    if (other_individual.domination_count == 0) {
                        other_individual.rank = i + 1;
                        lst.add(other_individual);
                    }
                }
                i++;
                population.fronts.add(i, lst);
            }
        }
    }

    public void calculate_crowding_distance(ArrayList<Solution> front) {
        if (front.size() > 0) {
            int solution_num = front.size();

            for (Solution individual : front) {
                individual.crowding_distance = 0;
            }

            for (int i = 0; i < front.get(0).objectives.length; i++) {
                final int idx = i;
                front.sort((o1, o2) -> {
                    int flag = 0;
                    if (o1.objectives[idx] < o2.objectives[idx]) {
                        flag = -1;
                    }
                    if (o1.objectives[idx] > o2.objectives[idx]) {
                        flag = 1;
                    }
                    return flag;
                });

                front.get(0).crowding_distance = Math.pow(10, 9);
                front.get(solution_num - 1).crowding_distance = Math.pow(10, 9);

                double max = -1;
                double min = -1;
                for (int j = 0; j < front.size(); j++) {
                    double curr = front.get(j).objectives[i];
                    if ((max == -1) || (curr > max)) {
                        max = curr;
                    }

                    if ((min == -1) || (curr < min)) {
                        min = curr;
                    }
                }

                double scale = max - min;
                if (scale == 0) {
                    scale = 1;
                }
                for (int j = 1; j < solution_num - 1; j++) {
                    front.get(j).crowding_distance += (front.get(j + 1).objectives[idx] - front.get(j - 1).objectives[idx]) / scale;
                }

            }
        }
    }

}
