import java.util.ArrayList;

public class CityBranchHashTable {
    private int tableSize = 100000001;
    private HashTable[] array;
    private int currentSize ;
    public ArrayList<Integer> positions = new ArrayList<>();
    CityBranchHashTable(){
        createArray(tableSize+1);
        currentSize = 0;
    }
    private void createArray(int size) {
        array = new HashTable[newListSize(size)];
    }
    public HashTable insertAndGet(String cityBranch){
        int pos = findAvailablePos(cityBranch);
        if (array[pos] != null)
            return array[pos];
        array[pos] = new HashTable(cityBranch);
        positions.add(pos);
        currentSize ++;
        if (currentSize > array.length/2)
            rehash();
        return array[pos];
    }
    private int findAvailablePos(String cityBranch){
        int currentPos = hashFunction(cityBranch);
        while (array[currentPos] != null && !array[currentPos].cityName.equals(cityBranch)){
            currentPos +=1;
            if (currentPos>= array.length)
                currentPos -= array.length;
        }
        return currentPos;
    }
    private int hashFunction(String cityBranch){
        int hashValue =cityBranch.hashCode();
        hashValue %= array.length;
        if (hashValue<0)
            hashValue += array.length;
        return hashValue;
    }
    public HashTable getElement(int pos){
        return array[pos];
    }
    private void rehash(){
        HashTable[] oldArray = array;
        createArray(array.length *2);
        currentSize = 0;
        positions.clear();
        for (HashTable element: oldArray){
            if (element!=null){
                insertAndGet(element.cityName);
            }
        }
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

