import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class project2 {
    public static void main(String[] args) throws IOException {
        CityBranchHashTable cityBranchHashTable = new CityBranchHashTable();
        File file = new File(args[0]);
        FileWriter writer = new FileWriter(args[2]);
        Scanner reading = new Scanner(file);
        while (reading.hasNextLine()){
            String[] inputList =reading.nextLine().split(", ");
            String[] nameAndSurnameList = inputList[2].split(" ");
            String name= nameAndSurnameList[0];
            String surname = nameAndSurnameList[1];
            String city=inputList[0];
            String district = inputList[1];
            String position = inputList[3];
            String cityDistrictCombination = city+district;
            Person person = new Person(name,surname,position,city,district);
            HashTable hashTable = cityBranchHashTable.insertAndGet(cityDistrictCombination);
            hashTable.insert(person,writer);
        }
        File input = new File(args[1]);
        Scanner readingUpdates = new Scanner(input);
        String currentMonth = null;
        while (readingUpdates.hasNextLine()){
            String[] lineList = readingUpdates.nextLine().split(", ");
            if (!lineList[0].isEmpty() && lineList.length == 1){
                currentMonth=lineList[0].split(":" )[0];
            }else if (lineList.length !=1){
                String[] initialList = lineList[0].split(": ");
                String operation = initialList[0];
                String city = initialList[1];
                String district = lineList[1];
                String cityAndDistrictCombination = city+district;
                HashTable hashTable = cityBranchHashTable.insertAndGet(cityAndDistrictCombination);
                if (operation.equals("LEAVE")){
                    String[] nameAndSurnameList = lineList[2].split(" ");
                    String name = nameAndSurnameList[0];
                    String surname = nameAndSurnameList[1];
                    String nameSurname = name+surname;
                    Person person = hashTable.search(nameSurname);
                    if (person != null){
                        hashTable.leave(person,writer);
                    }else{
                        writer.write("There is no such employee.");
                        writer.flush();
                        writer.write("\n");
                    }
                }else if(operation.equals("ADD")){
                    String[] nameAndSurnameList = lineList[2].split(" ");
                    Person newPerson = new Person(nameAndSurnameList[0],nameAndSurnameList[1],lineList[3],city,district);
                    hashTable.insert(newPerson,writer);
                }else if (operation.equals("PRINT_MANAGER")){
                    writer.write("Manager of the "+ hashTable.manager.district +" branch is "+ hashTable.manager.name+" "+ hashTable.manager.surname+".");
                    writer.flush();
                    writer.write("\n");
                }else if (operation.equals("PERFORMANCE_UPDATE")){
                    String[] nameAndSurnameList = lineList[2].split(" ");
                    String name = nameAndSurnameList[0];
                    String surname = nameAndSurnameList[1];
                    String nameSurname = name+surname;
                    int monthlyScore = Integer.parseInt(lineList[3]);
                    Person person = hashTable.search(nameSurname);
                    if (person != null){
                        hashTable.update(person,monthlyScore,writer);
                    }else{
                        writer.write("There is no such employee.");
                        writer.flush();
                        writer.write("\n");
                    }
                }else if (operation.equals("PRINT_MONTHLY_BONUSES")){
                    int bonus = hashTable.calculateMonthlyBonus();
                    writer.write("Total bonuses for the " + district + " branch this month are: " + bonus);
                    writer.flush();
                    writer.write("\n");
                }else if (operation.equals("PRINT_OVERALL_BONUSES")){
                    int bonus = hashTable.calculateOverallBonus();
                    writer.write("Total bonuses for the "+ district + " branch are: "+ bonus);
                    writer.flush();
                    writer.write("\n");
                }
            }else if(lineList[0].isEmpty()){
                for (int i :cityBranchHashTable.positions){
                    cityBranchHashTable.getElement(i).resetMonth();
                }
            }
        }
    }
}