/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package teacherclasses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 *
 * @author saplab
 */
public class Solution {

    //Chromosome - a binary matrix with chromosomw[i][j] is teacher j at course i - Decision variable
    public int chromosome[][];

    //Data
    public Data data;

    //Contraitns all course have been assigned
    public boolean all_Courses_Contraints;

    //Contraints a course has only one teacher
    public boolean single_Teacher_Courses_Contraints;

    //Contrants a teacher has only one course at same slot
    public boolean single_Slot_Contraints;

    //Contraints a teacher has number of slots in range
    public boolean inRange_Slot_Contraints;

    //Contraints a teacher has enough rating from students with subject at course assigned
    public boolean student_Rating_Constraint;

    //Contraints a teacher is qualified for subject of assigned course
    public boolean self_Rating_Constraint;

    //Contraints a teacher is able to teach assigned slots
    public boolean slot_Rating_Constraint;

    public ArrayList<Integer> sum = new ArrayList<>();

    public int[][] getChromosome() {
        return chromosome;
    }

    public void setChromosome(int[][] chromosome) {
        this.chromosome = chromosome;
    }

    public Solution(Data data) {
        all_Courses_Contraints = true;
        single_Slot_Contraints = true;
        single_Teacher_Courses_Contraints = true;
        inRange_Slot_Contraints = true;
        student_Rating_Constraint = true;
        self_Rating_Constraint = true;
        slot_Rating_Constraint = true;
        chromosome = new int[data.M][data.N];
        this.data = data;

        //Create empty schedule
        for (int i = 0; i < data.M; i++) {
            for (int j = 0; j < data.N; j++) {
                chromosome[i][j] = 0;
            }
        }
    }

    //Calculate quality of P0
    public double cal_Quality_P0(Data data) {
        double quality = 0;
        for (int i = 0; i < data.M; i++) {
            for (int j = 0; j < data.N; j++) {
                quality += chromosome[i][j] * data.Rating[j][data.courses[i].getSubject()];
            }
        }
        return quality;
    }

    //Calculate total salary 
    public double cal_Salary_P0(Data data) {
        double salary = 0;
        for (int i = 0; i < data.M; i++) {
            for (int j = 0; j < data.N; j++) {
                salary += chromosome[i][j] * data.teachers[j].getSalaryLevel();
            }
        }
        return salary;
    }

    //Calculate payoff of P0 
    public double cal_Payoff_P0(Data data) {
        double payoff;
        payoff = data.w1 * cal_Quality_P0(data);
        return payoff;
    }

    //Calculate Interest (for the subject) for the assigned courses of Pj
    public double cal_Favourite_Subs_PJ(Data data, int teacher) {
        double favourite_Subs = 0;
        for (int i = 0; i < data.M; i++) {
            favourite_Subs += chromosome[i][teacher] * data.FSub[teacher][data.courses[i].getSubject()];
        }
        return favourite_Subs;
    }

    
    //Calculate Interest (in terms of time frame) for the Courses classified of Pj
    public double cal_Favourite_Slots_PJ(Data data, int teacher) {
        double favourite_Slots = 0;
        for (int i = 0; i < data.M; i++) {
            favourite_Slots += chromosome[i][teacher] * data.FSlot[teacher][data.courses[i].getSlot()];
        }
        return favourite_Slots;
    }

    //Calculate The difference between the desired number of layers and the number of classes to be classified of Pj
    public double cal_Err_Courses_PJ(Data data, int teacher) {
        double Err_Courses = 0;
        for (int i = 0; i < data.M; i++) {
            Err_Courses += chromosome[i][teacher];
        }
        Err_Courses = data.teachers[teacher].getIdealClass() - Err_Courses;
        return 10- Math.abs(Err_Courses);
    }

    //Calculate Number of time segments of the day of Pj
    public double cal_Periods_PJ(Data data, int teacher) {

        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < data.M; i++) {
            if (chromosome[i][teacher] == 1) {
                list.add(data.courses[i].getSlot());
            }
        }
        Collections.sort(list);
        int t = 1;
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) - 1 != list.get(i - 1)) {
                t++;
            }
        }

        return t;

    }

    //Calculate Payoff of Pj
    public double cal_Payoff_PJ(Data data, int teacher) {
        double payoff = (data.w3 * cal_Favourite_Subs_PJ(data, teacher) + data.w4 * cal_Favourite_Slots_PJ(data, teacher)+ data.w5 * cal_Err_Courses_PJ(data, teacher));
        return payoff;
    }
    
    //Calculate Payoff of all Pj from 1 to N
    public double cal_Payoff_All_PJ(Data data) {
        double payoff = 0;
        for (int i = 0; i < data.N; i++) {
            payoff += data.w[i + 1] * cal_Payoff_PJ(data, i);
        }
        return payoff;
    }

    //Calculate Fitness
    public double cal_Fitness(Data data) {
        
        //Phat solution neu no vi pham contraints
        if (check_Solution(data) == false) {
            return ((data.c1 * cal_Payoff_P0(data) + data.c2 * cal_Payoff_All_PJ(data))) / 10000;
        }
        return (data.c1 * cal_Payoff_P0(data) + data.c2 * cal_Payoff_All_PJ(data));
    }

    //Check Contraitns all course have been assigned
    public boolean checkAllCourseConstraint(Data data) {
        int sumSlot = 0;
        for (int i = 0; i < data.M; i++) {
            for (int j = 0; j < data.N; j++) {
                sumSlot += chromosome[i][j];
            }
        }

        if (sumSlot == data.M) {
            //System.out.println("Not all courses assigned");
            all_Courses_Contraints = true;
            return true;
        }
        return false;
    }

     //Check Contraints a course has only one teacher
    public boolean checkSingleTeacherCourseConstraint(Data data) {
        for (int i = 0; i < data.M; i++) {
            int sum2 = 0;
            for (int j = 0; j < data.N; j++) {
                sum2 += chromosome[i][j];
            }
            if (sum2 != 1) {
                //System.out.println("courses have 2 teacher");
                single_Teacher_Courses_Contraints = false;
                return false;
            }
            sum2 = 0;
        }
        return true;
    }

     //Check Contrants a teacher has only one course at same slot
    public boolean checkSingleSlotConstraint(Data data) {
        for (int i = 0; i < data.N; i++) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int j = 0; j < data.M; j++) {
                if (chromosome[j][i] == 1) {
                    list.add(data.courses[j].getSlot());
                }
            }
            Collections.sort(list);

            for (int k = 1; k < list.size(); k++) {
                if (list.get(k) == list.get(k - 1)) {
                    //System.out.println("teacher have 2 slot at same time");
                    single_Slot_Contraints = false;
                    return false;
                }
            }
            list.clear();
        }
        return true;
    }

     //Check Contraints a teacher has number of slots in range
    public boolean checkInRangeSlotConstraint(Data data) {
        for (int i = 0; i < data.N; i++) {
            int sum3 = 0;
            for (int j = 0; j < data.M; j++) {
                if (chromosome[j][i] == 1) {
                    sum3 += chromosome[j][i];
                }
            }
            sum.add(sum3);
            if (sum3 > data.teachers[i].getMaxClass() || sum3 < data.teachers[i].getMinClass()) {
                //System.out.println("Number of slot not in range");
                inRange_Slot_Contraints = false;
                return false;
            }
            sum3 = 0;
        }
        return true;
    }

     //Check Contraints a teacher has enough rating from students with subject at course assigned
    public boolean checkStudentRatingConstraint(Data data) {
        for (int i = 0; i < data.M; i++) {
            for (int j = 0; j < data.N; j++) {
                if (chromosome[i][j] == 1 && data.Rating[j][data.courses[i].getSubject()] < 1) {
                    student_Rating_Constraint = false;
                    return false;
                }
            }
        }
        return true;
    }

     //Check Contraints a teacher is qualified for subject of assigned course
    public boolean checkSelfRatingConstaint(Data data) {
        for (int i = 0; i < data.M; i++) {
            for (int j = 0; j < data.N; j++) {
                if (chromosome[i][j] == 1 && data.FSub[j][data.courses[i].getSubject()] < 1) {
                    self_Rating_Constraint = false;
                    return false;
                }
            }
        }
        return true;
    }

     //Check Contraints a teacher is able to teach assigned slots
    public boolean checkSlotRatingConstraint(Data data) {
        for (int i = 0; i < data.M; i++) {
            for (int j = 0; j < data.N; j++) {
                if (chromosome[i][j] == 1 && data.FSlot[j][data.courses[i].getSlot()] < 1) {
                    slot_Rating_Constraint = false;
                    return false;
                }
            }
        }
        return true;
    }

    //Check if valid solution
    public boolean check_Solution(Data data) {
        checkAllCourseConstraint(data);
        checkInRangeSlotConstraint(data);
        checkSelfRatingConstaint(data);
        checkSingleSlotConstraint(data);
        checkSingleTeacherCourseConstraint(data);
        checkSlotRatingConstraint(data);
        checkStudentRatingConstraint(data);
        return checkAllCourseConstraint(data) && checkInRangeSlotConstraint(data)
                && checkSingleSlotConstraint(data) && checkSingleTeacherCourseConstraint(data)
                && checkStudentRatingConstraint(data) && checkSelfRatingConstaint(data)
                && checkSlotRatingConstraint(data);
    }

    //Calculate sum of PJ Favour_Sub
    public double cal_Favourite_Subs_All_PJ(Data data) {
        double favourite_Subs = 0;
        for (int i = 0; i < data.N; i++) {
            favourite_Subs += cal_Favourite_Subs_PJ(data, i);
        }
        return favourite_Subs;
    }

    //Calculate sum of PJ Favour_Slot
    public double cal_Favourite_Slots_All_PJ(Data data) {
        double favoriteSlots = 0;
        for (int i = 0; i < data.N; i++) {
            favoriteSlots += cal_Favourite_Slots_PJ(data, i);
        }
        return favoriteSlots;
    }

    //Calculate sum of PJ Error_Course
    public double cal_Err_Courses_All_PJ(Data data) {
        double errCourses = 0;
        for (int i = 0; i < data.N; i++) {
            errCourses += cal_Err_Courses_PJ(data, i);
        }
        return errCourses;
    }

    //Calculate sum of PJ PeriodOfDay
    public double cal_Periods_All_PJ(Data data) {
        double periods = 0;
        for (int i = 0; i < data.N; i++) {
            periods += cal_Periods_PJ(data, i);
        }
        return periods;
    }

}
