/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package teacherclasses;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author saplab
 */
public class GeneticAlgorithmImplementer {

    //Chua moi dau vao va cac trong so cua mo hinh
    Data data;
    
    //Dung de do thoi gian chay cua thuat toan
    long time;

    public GeneticAlgorithmImplementer(Data data) {

        this.data = data;
    }

    //Generate a new Solution
    public Solution generateSolution() {
        
        /*Qua trinh generate theo luong nhu sau:
        1. Assign giao vien cho tung course
            Voi moi course, lap qua danh sach giao vien:
            1.1. Check xem so course giao vien nay da day da vuot qua max cua giao vien chua
            1.2. Check xem do ua thich cua giao vien voi course co > 0
            1.3. Check xem do yeu thich cua giao vien voi slot co >0
            1.4. Check xem rating cua hoc sinh doi voi giao vien khi day course nay > 0
            1.5. Neu thoa man tu 1.1 => 1.4 thi add giao vien nay vao 1 list thoa man doi voi course
            1.6. Chon random 1 giao vien tu list va assign cho course nay
           Lap tu 1.1 => 1.6 cho den khi tat ca course dc assign => Day la solution
        
        */
        
        //Number of slot assigned for each Teacher, start from 0
        int[] currentSlots = new int[data.N];

        int maximum = data.N;
        int minimum = 0;
        int range = maximum - minimum;
        int randomNum;
        Solution s = new Solution(data);
        //With each course randomly choose a teacher to assign
        for (int i = 0; i < data.M; i++) {
            DistributedRandomNumberGenerator rnd = new DistributedRandomNumberGenerator();
            
            for (int j = 0; j < data.N; j++) {
                
                int sumSlot = 0;

                //Check if the teacher is able to teach at this slots or this subject
                if (data.Rating[j][data.courses[i].getSubject()] > 0 && data.FSlot[j][data.courses[i].getSlot()] > 0 && data.FSub[j][data.courses[i].getSubject()] > 0) {
                    ArrayList<Integer> slot = new ArrayList<>();
                    
                    //Calculate number of slot the teacher has been assigned
                    for (int k = 0; k <= i; k++) {
                        if (s.chromosome[k][j] == 1) {
                            sumSlot++;
                            slot.add(data.courses[k].getSlot());
                        }
                    }
                    //Check if the teacher still can teach more slot; if true add teacher to pool with distibution 0.1
                    if (slot.contains(data.courses[i].getSlot()) == false && sumSlot + 1 <= data.teachers[j].getMaxClass()) {
                        rnd.addNumber(j, 0.1);
                    }

                }

            }
            
            //Randomly choose teacher from pool; every teacher has same chance.
            randomNum = rnd.getDistributedRandomNumber();
            s.chromosome[i][randomNum] = 1;
        }
        return s;
    }

    //Crossover parents to a new Solution
    public Solution Crossover(Solution Mom, Solution Dad) {
        Solution child = new Solution(data);
        
        //Cut off Mom and Dad Solutions at middle veritcally, combine Mom 1st half and Dad 2nd half to new Chromosome
        for (int i = 0; i < data.M / 2; i++) {
            for (int j = 0; j < data.N; j++) {
                child.chromosome[i][j] = Dad.chromosome[i][j];
                child.chromosome[data.M - i - 1][j] = Mom.chromosome[data.M - i - 1][j];
            }
        }
        child.chromosome[data.M / 2] = Mom.chromosome[data.M / 2];

        return child;
    }

    //Muate a Solution to a new one
    //Dot bien bang cach chon random 2 course va doi giao vien duoc assign cho nhau
    public Solution Mutate(Solution s) {
        int maximum = data.N;
        int minimum = 0;
        int range = maximum - minimum;
        int randomNum;

       
        Random rn = new Random();        
         //Random an index in range
        randomNum = rn.nextInt(range) + minimum;
        int randomNum2;
        do {
            //Random another index in range not duplicate the first one
            randomNum2 = rn.nextInt(range) + minimum;
        } while (randomNum2 == randomNum);
        
        //Swap 2 rows of chromosome based on 2 random index
        int tmpRow[] = s.chromosome[randomNum];
        s.chromosome[randomNum] = s.chromosome[randomNum2];
        s.chromosome[randomNum2] = tmpRow;
        
        return s;
    }

    
    //Thuc thi GA
    public ArrayList<Solution> implementGA() {
        
        
        long startTime = System.currentTimeMillis();
        //Tap chu ket qua khoi tao rong, tap nay chua solution tot nhat cua moi vong lap
        ArrayList<Solution> result = new ArrayList<>();
        ArrayList<Solution> current_generation = new ArrayList<>();

        int maximum = 300;
        int minimum = 0;
        int mutation_minimum = 25;
        int range = maximum - minimum;
        int randomNum;
        Random rn = new Random();

        int randomNum2;
        //Generate 1 tap gom maximum solution voi maximum =300
        for (int i = 0; i < maximum; i++) {
            current_generation.add(generateSolution());
        }

        //sort tap theo thu tu giam dan fitness
        Collections.sort(current_generation, new Comparator<Solution>() {
            @Override
            public int compare(Solution o1, Solution o2) {

                return -Double.compare(o1.cal_Fitness(data), o2.cal_Fitness(data));
            }
        });

        
        //Them solution tot nhat vao tap ket qua
        result.add(current_generation.get(0));
        
        //Produce new generation
        for (int j = 0; j < 300; j++) {
            //Selection keep top 30 best solutions to new generation
            ArrayList<Solution> next_generation = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                next_generation.add(current_generation.get(i));
            }
            //Crossover; Create 270 new solutions by crossover random parents to new generation
            for (int i = 0; i < 270; i++) {
                randomNum = rn.nextInt(range) + minimum;
                do {
                    randomNum2 = rn.nextInt(range) + minimum;
                } while (randomNum2 == randomNum);
                next_generation.add(Crossover(current_generation.get(randomNum), current_generation.get(randomNum2)));
            }
            
            //Sort new generation giam dan theo fitness
            Collections.sort(next_generation, new Comparator<Solution>() {
                @Override
                public int compare(Solution o1, Solution o2) {

                    return -Double.compare(o1.cal_Fitness(data), o2.cal_Fitness(data));
                }

            });
            
            
            //mutation new generation
            for (int i = 0; i < 40; i++) {
                //dot bien 40 ca the ko nam trong 25 ca the tot nhat.
                //Randomly choose a solutions to mutate(Excepts top 25 best solutions)
                randomNum = rn.nextInt(range - 25) + mutation_minimum;
                next_generation.set(randomNum, Mutate(next_generation.get(randomNum)));
            }
            
             //Sort new generation
            Collections.sort(next_generation, new Comparator<Solution>() {
                @Override
                public int compare(Solution o1, Solution o2) {

                    return -Double.compare(o1.cal_Fitness(data), o2.cal_Fitness(data));
                }

            });
            
            //Them solution tot nhat vao tap ket qua
            if (result.get(result.size() - 1).cal_Fitness(data) <= next_generation.get(0).cal_Fitness(data)) {
                System.out.println(j + " - "+next_generation.get(0).cal_Fitness(data));
                result.add(next_generation.get(0));
            }
            
            else{
                System.out.println(j + " - "+result.get(result.size() - 1).cal_Fitness(data));
                result.add(result.get(result.size() - 1));
            }
            current_generation.clear();
            current_generation = new ArrayList<>(next_generation);
        }
        long endTime = System.currentTimeMillis();
        
        //Set time execution
        time = endTime - startTime;
        //result chua 300 ket qua. Moi ket qua la ket qua tot nhat cua 1 trong 300 vong lap
//        for(int i=0;i<result.size();i++){
//            System.out.println(i+" - " + result.get(i).cal_Fitness(data));
//        }
        return result;
    }

    //Tìm tập Pareto bằng cách lấy kết quả tốt nhất của mỗi lần chạy thuật toán GA và random các tham số 
        public ArrayList<Solution> findPareto(int numberOfLoop) {
         long startTime = System.currentTimeMillis();
        //Tập kết quả bao gồm kết quả tốt nhất của mỗi vòng lặp
        ArrayList<Solution> results = new ArrayList<>();
        
        for(int i=0;i<numberOfLoop;i ++){
            System.out.println(i);
            Random rand = new Random();
            for(int j=0;j<data.N+1;j++){
                data.w[j] =(double) rand.nextInt(10) + 1;
            }
            
           
            ArrayList<Solution> result = implementGA();
            results.add(result.get(result.size()-1));
        }
         long endTime = System.currentTimeMillis();
        
        //Set time execution
        time = endTime - startTime;
            return  results;
    }
    
    
    //Write fitness and all objective of set of solutions
    public static void writeSolution(ArrayList<Solution> solutions, Data data) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet1");

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Fitness");

        cell = row.createCell(1);
        cell.setCellValue("Quality P0");

        cell = row.createCell(2);
        cell.setCellValue("Salary P0");

        cell = row.createCell(3);
        cell.setCellValue("Favorite subs");

        cell = row.createCell(4);
        cell.setCellValue("Favorite slots");

        cell = row.createCell(5);
        cell.setCellValue("Periods");

        cell = row.createCell(6);
        cell.setCellValue("Err courses");

        int rowCount = 0;

        for (Solution s : solutions) {
            row = sheet.createRow(++rowCount);
            cell = row.createCell(0);
            cell.setCellValue(s.cal_Fitness(data));

            cell = row.createCell(1);
            cell.setCellValue(s.cal_Quality_P0(data));

            cell = row.createCell(2);
            cell.setCellValue(s.cal_Salary_P0(data));

            cell = row.createCell(3);
            cell.setCellValue(s.cal_Favourite_Subs_All_PJ(data));

            cell = row.createCell(4);
            cell.setCellValue(s.cal_Favourite_Slots_All_PJ(data));

            cell = row.createCell(5);
            cell.setCellValue(s.cal_Periods_All_PJ(data));

            cell = row.createCell(6);
            cell.setCellValue(s.cal_Err_Courses_All_PJ(data));

        }
        try (FileOutputStream outputStream = new FileOutputStream("Fitness.xlsx")) {
            workbook.write(outputStream);
            outputStream.close();
        }
    }

    public static void addResult(Solution s, Data data) throws IOException {
        FileInputStream myxls = new FileInputStream("Results_NASH.xlsx");

        XSSFWorkbook workbook = new XSSFWorkbook(myxls);
        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRow = sheet.getLastRowNum();
        Row row = sheet.createRow(++lastRow);

        Cell cell;

        cell = row.createCell(0);
        cell.setCellValue(s.cal_Fitness(data));

        cell = row.createCell(1);
        cell.setCellValue(s.cal_Quality_P0(data));

        cell = row.createCell(2);
        cell.setCellValue(s.cal_Salary_P0(data));

        cell = row.createCell(3);
        cell.setCellValue(s.cal_Favourite_Subs_All_PJ(data));

        cell = row.createCell(4);
        cell.setCellValue(s.cal_Favourite_Slots_All_PJ(data));

        cell = row.createCell(5);
        cell.setCellValue(s.cal_Periods_All_PJ(data));

        cell = row.createCell(6);
        cell.setCellValue(s.cal_Err_Courses_All_PJ(data));

        try (FileOutputStream outputStream = new FileOutputStream("Results_NASH.xlsx")) {
            workbook.write(outputStream);
            outputStream.close();
        }
    }

    
    //Write all payoff of set of solutions to excel file
    //Ghi lai payoff cua tung nguoi choi va fitness ung voi moi solution
    public static void writeSolutions(ArrayList<Solution> solutions, Data data, long time,String sheetName) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName);

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("PayOff P0");
        for (int i = 1; i <= data.N; i++) {
            cell = row.createCell(i);
            cell.setCellValue("PayOff P" + i);
        }
        cell = row.createCell(data.N + 2);
        cell.setCellValue("Time");
        cell = row.createCell(data.N + 3);
        cell.setCellValue("Fitness");
        int rowCount = 0;

        for (Solution s : solutions) {
            row = sheet.createRow(++rowCount);
            cell = row.createCell(0);
            cell.setCellValue(s.cal_Payoff_P0(data));
            for (int i = 1; i <= data.N; i++) {
                cell = row.createCell(i);
                cell.setCellValue(s.cal_Payoff_PJ(data, i - 1));
            }
            cell = row.createCell(data.N + 2);
            cell.setCellValue(time);
            cell = row.createCell(data.N + 3);
            cell.setCellValue(s.cal_Fitness(data));
        }
        try (FileOutputStream outputStream = new FileOutputStream(sheetName+".xlsx")) {
            workbook.write(outputStream);
            outputStream.close();
        }
    }

    
    //Write  The difference between the desired number of layers and the number of classes to be classified of all teachers to excel
    public static void writeErrCourseToExcel(Solution solution, Data data) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sheet2");
        for (int i = 0; i < data.N; i++) {
            Row row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(i);
            cell = row.createCell(1);
            cell.setCellValue(solution.cal_Err_Courses_PJ(data, i));
        }
        try (FileOutputStream outputStream = new FileOutputStream("Expectation.xlsx")) {
            workbook.write(outputStream);
            outputStream.close();
        }
    }

    //Write a solution timetable to excel
    public static void writeSolutionAsTimetable(Solution solution, Data data,String sheetName) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        for (int i = 0; i < data.N; i++) {

            XSSFSheet sheet = workbook.createSheet(i + "");
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(1);
            cell.setCellValue("Monday");

            cell = row.createCell(2);
            cell.setCellValue("Tuesday");

            cell = row.createCell(3);
            cell.setCellValue("Wednesday");

            cell = row.createCell(4);
            cell.setCellValue("Thurday");

            cell = row.createCell(5);
            cell.setCellValue("Friday");

            for (int k = 1; k < 7; k++) {
                row = sheet.createRow(k);
                row.setHeight((short) 800);
                cell = row.createCell(0);
                cell.setCellValue(k);
            }

            for (int j = 0; j < data.M; j++) {
                if (solution.chromosome[j][i] == 1) {
                    String cellContent = data.courses[j].getSubjectName() + "\n" + data.courses[j].getClasses() + "\n" + data.courses[j].getRoom();
                    if (data.courses[j].getSlot() == 0) {
                        cell = sheet.getRow(1).createCell(1);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(1).createCell(3);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(1).createCell(5);
                        cell.setCellValue(cellContent);
                    }
                    if (data.courses[j].getSlot() == 1) {
                        cell = sheet.getRow(2).createCell(1);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(2).createCell(3);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(2).createCell(5);
                        cell.setCellValue(cellContent);
                    }
                    if (data.courses[j].getSlot() == 2) {
                        cell = sheet.getRow(3).createCell(1);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(3).createCell(3);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(3).createCell(5);
                        cell.setCellValue(cellContent);
                    }
                    if (data.courses[j].getSlot() == 3) {
                        cell = sheet.getRow(4).createCell(1);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(4).createCell(3);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(4).createCell(5);
                        cell.setCellValue(cellContent);
                    }
                    if (data.courses[j].getSlot() == 4) {
                        cell = sheet.getRow(5).createCell(1);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(5).createCell(3);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(5).createCell(5);
                        cell.setCellValue(cellContent);
                    }
                    if (data.courses[j].getSlot() == 5) {
                        cell = sheet.getRow(6).createCell(1);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(6).createCell(3);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(6).createCell(5);
                        cell.setCellValue(cellContent);
                    }
                    if (data.courses[j].getSlot() == 6) {
                        cell = sheet.getRow(1).createCell(2);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(2).createCell(2);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(1).createCell(4);
                        cell.setCellValue(cellContent);
                    }
                    if (data.courses[j].getSlot() == 7) {
                        cell = sheet.getRow(3).createCell(2);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(2).createCell(4);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(3).createCell(4);
                        cell.setCellValue(cellContent);
                    }
                    if (data.courses[j].getSlot() == 8) {
                        cell = sheet.getRow(4).createCell(2);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(5).createCell(2);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(4).createCell(4);
                        cell.setCellValue(cellContent);
                    }
                    if (data.courses[j].getSlot() == 9) {
                        cell = sheet.getRow(6).createCell(2);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(5).createCell(4);
                        cell.setCellValue(cellContent);
                        cell = sheet.getRow(6).createCell(4);
                        cell.setCellValue(cellContent);
                    }
                }
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream(sheetName+"Schedule.xlsx")) {
            workbook.write(outputStream);
            outputStream.close();
        }

    }
}
