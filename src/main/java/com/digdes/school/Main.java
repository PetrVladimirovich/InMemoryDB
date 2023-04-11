package com.digdes.school;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        JavaSchoolStarter starter = new JavaSchoolStarter();

        try {
            //List<Map<String,Object>> result1 = starter.execute("INSERT VALUES 'lastName' = 'Федоров' , 'id'=3, 'age'=40, 'active'=false");
            List<Map<String,Object>> result2 = starter.execute("INSERT VALUES 'lastName' = 'Федоров' , 'id'=3, " +
                    "'age'=40, 'active'=false WHERE 'lastName'  >= 'Петро' ANd ('age' >= 30 or 'cost' !=10.0 ) and 'active' like     false");
            //List<Map<String,Object>> result3 = starter.execute("UPDATE VALUES 'active'=false, 'cost'=10.1 where 'id'=3");
            //List<Map<String,Object>> result4 = starter.execute("SELECT Where");

            starter.printDB();

        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
