package com.digdes.school;

public class Main {
    public static void main(String[] args) {
        JavaSchoolStarter starter = new JavaSchoolStarter();

        try {
            System.out.println(starter.execute("INSERT VALUES 'lastName' = 'Федоров' , 'id'=3, 'age'=40, 'active'=true"));
            starter.printDB();
            System.out.println(starter.execute("INSERT VALUES 'lastName' = 'Петро' , 'id'=3, 'age'=32, 'active'=true"));
            starter.printDB();
            System.out.println(starter.execute("INSERT VALUES 'lastName' = 'гринч' , 'id'=4, 'age'=100, 'active'=false"));
            starter.printDB();
            System.out.println(starter.execute("INSERT VALUES 'lastName' = 'гринч2' , 'id'=5, 'age'=100, 'cost' = 36.6,'active'=true"));
            starter.printDB();
            System.out.println(starter.execute("SELECT WHere 'lastname' like 'гринч' or 'id' >= 4"));
//            starter.printDB();
//            System.out.println(starter.execute("UPDATE VALUES 'active'=false, 'cost'=99.1 "));
//            starter.printDB();
//            System.out.println(starter.execute("SELECT"));
//            System.out.println(starter.execute("DELete Where 'age' = 100"));
//            System.out.println(starter.execute("UPDATE VALUES 'cost'=0.01 where 'id'=4 oR 'age' = 100"));
//            System.out.println();
//            System.out.println(starter.execute("SELECT"));
//            System.out.println(starter.execute("DELete") + " delete всех");
//            System.out.println(starter.execute("SELECT") + " select пустой");

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
