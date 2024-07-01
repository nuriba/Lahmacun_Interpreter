import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class HashTable{
    public String cityName;
    int initialTableSize = 1000001;
    private Person[] hashList;
    public int currentSize;
    public int numberOfCook = 0;
    public int numberOfCashier = 0;
    public int numberOfCourier = 0;
    public String currentMonth = null;
    public Person manager = null;
    public Person dismissCook = null;
    public Person dismissCashier = null;
    public Person dismissCourier = null;
    public ArrayList<Person> cookToManager = new ArrayList<>();
    public ArrayList<Person> cashierToCook = new ArrayList<>();
    ArrayList<Integer> currentMonthBonus = new ArrayList<>();
    ArrayList<Integer> overallBonus = new ArrayList<>();
    HashTable(String cityName) {
        this.cityName = cityName;
        hashList = new Person[newListSize(initialTableSize)];
        currentSize = 0;
    }
    public void insert(Person person, FileWriter writer) throws IOException {
        String nameSurname = person.name + person.surname;
        int position = findPos(nameSurname);
        if (contains(person)){
            writer.write("Existing employee cannot be added again.");
            writer.flush();
            writer.write("\n");
            return;
        }
        hashList[position] = person;
        updateEmployeeNumber(person,"insert");
        currentSize++;
        if (person.position.equals("MANAGER") && manager == null){
            manager = person;
        }
        if( person.position.equals("CASHIER"))
            checkPromote(person,writer);
        if (manager != null)
            if(manager.promotionScore<= -5)
                dismissManager(writer);
        if(dismissCashier != null || dismissCourier != null || dismissCook != null)
            checkDismiss(writer);
        if (currentSize > hashList.length/2)
            rehash(writer);
    }
    public boolean contains(Person person){
        String nameSurname = person.name + person.surname;
        int position = findPos(nameSurname);
        Person checkPerson = hashList[position];
        if (checkPerson == null)
            return false;
        return checkPerson.nameSurname.equals(nameSurname);
    }
    private void checkDismiss(FileWriter writer) throws IOException {
        if (numberOfCashier>1 &&  dismissCashier != null && contains(dismissCashier)) {
            if (dismissCashier.promotionScore>-5){
                dismissCashier = null;
                return;
            }else {
                delete(dismissCashier);
                writer.write(dismissCashier.name + " " + dismissCashier.surname + " is dismissed from branch: " + dismissCashier.district + ".");
                writer.flush();
                writer.write("\n");
                dismissCashier = null;
            }
        }
        if( numberOfCook>1 && dismissCook != null && contains(dismissCook)){
            if (dismissCook.promotionScore > -5){
                dismissCook = null;
                return;
            }else{
                delete(dismissCook);
                writer.write(dismissCook.name + " " + dismissCook.surname +" is dismissed from branch: "+ dismissCook.district+".");
                writer.flush();
                writer.write("\n");
                dismissCook= null;
            }

        }
        if(numberOfCourier>1 && dismissCourier != null && contains(dismissCourier)){
            if(dismissCourier.promotionScore > -5){
                dismissCourier = null;
                return;
            }else {
                delete(dismissCourier);
                writer.write(dismissCourier.name + " " + dismissCourier.surname + " is dismissed from branch: " + dismissCourier.district + ".");
                writer.flush();
                writer.write("\n");
                dismissCourier = null;
            }
        }
    }
    public void checkPromote(Person person, FileWriter writer) throws IOException {
        if (person.position.equals("CASHIER") && !cashierToCook.isEmpty() && numberOfCashier>1){
            Person willGetPromoted = cashierToCook.get(0);
            cashierToCook.remove(0);
            delete(willGetPromoted);
            willGetPromoted.promotionScore -=3;
            willGetPromoted.position="COOK";
            insert(willGetPromoted,writer);
            writer.write(willGetPromoted.name+" "+willGetPromoted.surname + " is promoted from Cashier to Cook.");
            writer.flush();
            writer.write("\n");
        }
    }
    public void update(Person person, int monthlyScore,FileWriter writer) throws IOException {
        person.updateMonthlyScore(monthlyScore);
        currentMonthBonus.add(person.bonus);
        int promotionScore = person.promotionScore;
        String position = person.position;
        if (!cashierToCook.isEmpty()){
            for (int i=0 ; i<cashierToCook.size() ; i++){
                if (cashierToCook.get(i).promotionScore<3)
                    cashierToCook.remove(i);
            }
        }
        if (! cookToManager.isEmpty()){
            for (int i=0 ; i<cookToManager.size() ; i++){
                if (cookToManager.get(i).promotionScore<10)
                    cookToManager.remove(i);
            }
        }
        if (promotionScore<= -5){
            if (position.equals("MANAGER"))
                dismissManager(writer);
            else
                dismiss(person,writer);
        }else if(promotionScore >=3 && position.equals("CASHIER")){
            if (numberOfCashier>1){
                delete(person);
                person.position = "COOK";
                person.promotionScore -= 3;
                insert(person,writer);
                writer.write(person.name+" "+person.surname + " is promoted from Cashier to Cook.");
                writer.flush();
                writer.write("\n");
            }else
                if (!cashierToCook.contains(person))
                    cashierToCook.add(person);
        }else if (promotionScore >= 10 && position.equals("COOK")){
            if (!cookToManager.contains(person))
                cookToManager.add(person);
            if(manager.promotionScore <= -5 && numberOfCook > 1){
                dismissManager(writer);
            }
        }
    }
    public void dismissManager(FileWriter writer) throws IOException {
        if (manager.promotionScore <= -5 && !cookToManager.isEmpty() && numberOfCook>1){
            Person newManager = cookToManager.get(0);
            cookToManager.remove(0);
            delete(manager);
            writer.write(manager.name + " " + manager.surname + " is dismissed from branch: " + manager.district + ".");
            writer.flush();
            writer.write("\n");
            delete(newManager);
            newManager.promotionScore -= 10;
            newManager.position = "MANAGER";
            manager = null;
            insert(newManager, writer);
            writer.write(newManager.name + " " + newManager.surname + " is promoted from Cook to Manager.");
            writer.flush();
            writer.write("\n");
        }
    }
    public void dismiss(Person person,FileWriter writer) throws IOException {
        if (getNumberOfEmployee(person.position)>1){
            delete(person);
            writer.write(person.name + " " + person.surname +" is dismissed from branch: "+ person.district+".");
            writer.flush();
            writer.write("\n");
        }else{
            switch (person.position) {
                case "CASHIER" -> dismissCashier = person;
                case "COOK" -> dismissCook = person;
                case "COURIER" -> dismissCourier = person;
            }
        }
    }
    public void leave(Person person,FileWriter writer) throws IOException   {
        if (person.position.equals("MANAGER")){
            if(cookToManager.isEmpty() || numberOfCook<=1) {
                if (manager.promotionScore > -5)
                    currentMonthBonus.add(200);
                return;
            }
            Person newManager = cookToManager.get(0);
            cookToManager.remove(0);
            delete(manager);
            writer.write(manager.name + " " + manager.surname + " is leaving from branch: " + manager.district + ".");
            writer.flush();
            writer.write("\n");
            delete(newManager);
            newManager.promotionScore -= 10;
            newManager.position = "MANAGER";
            manager = null;
            insert(newManager, writer);
            writer.write(newManager.name + " " + newManager.surname + " is promoted from Cook to Manager.");
            writer.flush();
            writer.write("\n");
        }else{
            if(getNumberOfEmployee(person.position)<=1){
                if(person.promotionScore>-5)
                    currentMonthBonus.add(200);
                return;
            }
            if(getNumberOfEmployee(person.position)>1) {
                delete(person);
                writer.write(person.name + " " + person.surname + " is leaving from branch: " + person.district + ".");
                writer.flush();
                writer.write("\n");
            }
        }
    }
    public void delete(Person person){
        String nameSurname = person.name + person.surname;
        currentSize--;
        updateEmployeeNumber(person,"delete");
        if (cookToManager.contains(person))
            cookToManager.remove(person);
        if (cashierToCook.contains(person))
            cashierToCook.remove(person);
        hashList[findPos(nameSurname)] = null;
    }
    public void updateEmployeeNumber(Person person, String method){
        if (method.equals("insert")) {
            switch (person.position) {
                case "COOK" -> numberOfCook +=1;
                case "CASHIER" -> numberOfCashier +=1;
                case "COURIER" -> numberOfCourier +=1;
            }
        }else if (method.equals("delete")) {
            switch (person.position) {
                case "COOK" -> numberOfCook-=1;
                case "CASHIER" -> numberOfCashier-=1;
                case "COURIER" -> numberOfCourier-=1;
            }
        }
    }
    public int getNumberOfEmployee(String position){
        return switch (position) {
            case "COURIER" -> numberOfCourier;
            case "CASHIER" -> numberOfCashier;
            case "COOK" -> numberOfCook;
            default -> 1;
        };
    }
    public void rehash(FileWriter writer) throws IOException {
        Person[] oldHash = hashList;
        hashList = new Person[newListSize(hashList.length * 2)];
        numberOfCook = 0;
        numberOfCourier = 0;
        numberOfCashier = 0;
        currentSize = 0;
        manager = null;
        for (Person item : oldHash) {
            if (item != null)
                insert(item, writer);
        }
    }
    public int calculateMonthlyBonus(){
        int total=0;
        for (int bonus: currentMonthBonus)
            total +=bonus;
        return total;
    }
    public int calculateOverallBonus(){
        int total =0;
        for (int bonus: overallBonus)
            total += bonus;
        total+=calculateMonthlyBonus();
        return total;
    }
    public void resetMonth(){
        overallBonus.add(calculateMonthlyBonus());
        currentMonthBonus.clear();
    }
    public Person search(String nameSurname){
        int currentPos = findPos(nameSurname);
        return hashList[currentPos];
    }
    public int findPos(String nameSurname) {
        int currentPos = hashFunction(nameSurname);
        while (hashList[currentPos] != null && !hashList[currentPos].nameSurname.equals(nameSurname)) {
            currentPos += 1;
            if (currentPos >= hashList.length) {
                currentPos -= hashList.length;
            }
        }
        return currentPos;
    }
    private int hashFunction(String value){
        int hashValue = value.hashCode();
        hashValue %= hashList.length;
        if (hashValue<0){
            hashValue += hashList.length;
        }
        return hashValue;
    }
    public int newListSize(int currentNum) {
        if(currentNum % 2 == 0) {
            currentNum++;
        }
        while(!isPrime(currentNum)) {
            currentNum += 2;
        }
        return currentNum;
    }
    public boolean isPrime(int num) {
        if(num == 2 || num == 3) {
            return true;
        }
        if (num == 1 || num % 2 == 0) {
            return false;
        }
        for(int i = 3; i * i <= num; i += 2) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;

    }
}