public class Person {
    String name;
    String surname;
    String nameSurname;
    String position;
    String city;
    String district;
    int monthlyScore = 0;
    int promotionScore = 0;
    int bonus = 0;
    Person(String name,String surname, String position, String city, String district){
        this.name = name;
        this.surname= surname;
        this.nameSurname = name+surname;
        this.position=position;
        this.city = city;
        this.district = district;
    }
    public int updateMonthlyScore(int monthlyScore){
        this.monthlyScore = monthlyScore;
        calculatingPromotionAndBonus();
        return bonus;
    }
    public void calculatingPromotionAndBonus(){
        if (monthlyScore< 0) {
            promotionScore += monthlyScore/200;
            bonus = 0;
        }else{
            promotionScore += monthlyScore/200;
            bonus = monthlyScore % 200;
        }
    }
}
