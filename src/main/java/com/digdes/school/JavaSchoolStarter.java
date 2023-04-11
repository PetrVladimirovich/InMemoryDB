package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaSchoolStarter {

    private final List<Map<String, Object>> db;
    private final List<Integer> validateElem = new ArrayList<>();

    public JavaSchoolStarter() {

        this.db = new ArrayList<>();

    }

    public List<Map<String, Object>> execute(String request) throws Exception {

        Map<String, Object> row = new HashMap<>();
        String lowCase = request.toLowerCase();
        List<String> arrBlocks = new ArrayList<>();

        if (lowCase.contains("where")) {
            System.out.println("WHERE case");
             int indexEndWhere = lowCase.indexOf("where") + 5;
             String newRequest = request.substring(indexEndWhere);
             newRequest = newRequest.replaceAll("\\s", "");

             for (int i = 0; i < newRequest.length(); i++) {
                 if (newRequest.charAt(i) == '(' || newRequest.charAt(i) == ')') {
                     arrBlocks.add(String.valueOf(newRequest.charAt(i)));
                     continue;
                 }

                 if (newRequest.charAt(i) == '\'') {
                     int indexEnd = getEndIndexBlock(newRequest, i);
                     String substring = newRequest.substring(i + 1, newRequest.indexOf("'", i + 1));
                     String s = newRequest.substring(newRequest.indexOf("'", i + 1) + 1);

                     if (indexEnd == 10000) {
                         arrBlocks.add(substring);
                         String operator = getOperation(s);
                         arrBlocks.add(operator);
                         arrBlocks.add(newRequest.substring(newRequest.indexOf("'", i + 1)
                                                        + 1 + operator.length()).replace("'", ""));
                         break;
                     }

                     arrBlocks.add(substring);
                     String operator = getOperation(s);
                     arrBlocks.add(operator);
                     arrBlocks.add(newRequest.substring(newRequest.indexOf("'", i + 1)
                                                + 1 + operator.length(), indexEnd).replace("'", ""));
                     i = indexEnd - 1;
                 }

                 if (newRequest.substring(i).toLowerCase().startsWith("or")) {
                     arrBlocks.add("or");
                 }else if (newRequest.substring(i).toLowerCase().startsWith("and")) {
                     arrBlocks.add("and");
                 }
             }

            request = request.substring(0, lowCase.indexOf("where"));
            lowCase = request.toLowerCase();
        }
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (!arrBlocks.isEmpty()) {
            for (int i = 0; i < db.size(); i++) {
                boolean validate = false;
                boolean and = false;
                int fullBracket = 0;
                int idxOpen;
                int idxClose;

                for (int j = 0; j < arrBlocks.size(); j++) {
                    if (arrBlocks.get(j).equals("(")) {
                        fullBracket++;
                        idxOpen = j;
                    }
                    if (arrBlocks.get(i).equals(")")) {

                    }

                    if (db.get(i).containsKey(arrBlocks.get(j))) {

                    }
                }

                if (validate) {
                    validateElem.add(i);
                }
            }
        }

        System.out.println("WHERE:");
        for (String str : arrBlocks) {

            System.out.println(str);

        }
        System.out.println("END WHERE!!!");



        if (lowCase.startsWith("insert values")) {

            System.out.println("INSERT:");
            String temp = lowCase.replaceAll("\\s", "")
                    .replace("insertvalues", "")
                    .replace("'", "");
            String[] arr = temp.split(",");

            for (String str : arr) {
                String[] equalSplit = str.split("=");
                if (equalSplit[0].equals("lastname")) {
                    row.put("lastName", parseLastName(request));
                } else {
                    row.put(equalSplit[0], mapperToType(equalSplit[0], equalSplit[1]));
                }
            }

        } else if (lowCase.startsWith("update values")) {

            System.out.println("UPDATE:");


        } else if (lowCase.startsWith("delete")) {

            System.out.println("DELETE:");

        } else if (lowCase.startsWith("select")) {

            System.out.println("SELECT:");

        } else {

            throw new Exception(String.format("Такой операции не существует: %s...", request.substring(0, 14)));

        }

        db.add(row);

        return db;

    }

    private boolean validationOperationForField(String field, String operation) throws Exception{
        switch (field) {
            case "lastName" -> {
                if (operation.equals("!=") ||
                    operation.equals("=") ||
                    operation.equals("ilike") ||
                        operation.equals("like")) {
                        return true;
                }else {
                    throw new Exception(String.format("Данная операция не доступна: '%s', с данным полем: '%s'", operation, field));
                }
            }
            case "id", "age", "cost" -> {
                if (operation.equals("=") ||
                        operation.equals("!=") ||
                        operation.equals(">=") ||
                        operation.equals("<=") ||
                        operation.equals(">") ||
                        operation.equals("<")) {
                    return true;
                }else {
                    throw new Exception(String.format("Данная операция не доступна: '%s', с данным полем: '%s'", operation, field));
                }
            }
            case "active" -> {
                if (operation.equals("!=") ||
                        operation.equals("=")) {
                    return true;
                }else {
                    throw new Exception(String.format("Данная операция не доступна: '%s', с данным полем: '%s'", operation, field));
                }
            }
            default -> {
                throw new Exception(String.format("Такого поля нет в таблице: '%s'", field));
            }
        }
    }

    private int getEndIndexBlock(String newRequest, int i) {
        newRequest = newRequest.toLowerCase();
        int[] index = {newRequest.indexOf("and", i), newRequest.indexOf("or", i), newRequest.indexOf(")", i),};
        int indexMin = 10000;
        for (int in : index) {
            if (in != -1 && indexMin > in) {
                indexMin = in;
            }
        }
        return indexMin;
    }

    private String getOperation(String param) {
        if (param.startsWith(">=")) {
            return ">=";
        }else if (param.startsWith("<=")) {
            return "<=";
        }else if (param.startsWith("!=")) {
            return "!=";
        }else if (param.startsWith("=")) {
            return "=";
        }else if (param.startsWith(">")) {
            return ">";
        }else if (param.startsWith("<")) {
            return "<";
        }else if (param.startsWith("ilike")) {
            return "ilike";
        }else if (param.startsWith("like")) {
            return "like";
        }else {
            return "NONE";
        }
    }

    private String parseLastName(String request) {

        String lowCase = request.toLowerCase();
        int lastNameIndex = lowCase.indexOf("lastname") + 9;
        lastNameIndex = request.indexOf("'", lastNameIndex);
        int secIndex = request.indexOf("'", lastNameIndex + 1);
        return request.substring(lastNameIndex + 1, secIndex);
    }

    private Object mapperToType(String field, String value) throws Exception {

        switch (field) {
            case "id", "age" -> {
                return Long.parseLong(value);
            }
            case "cost" -> {
                return Double.parseDouble(value);
            }
            case "active" -> {
                return Boolean.valueOf(value);
            }
            default -> {
                throw new Exception(String.format("Такого поля нет в таблице: '%s'", field));
            }
        }

    }

    public void printDB() {

        for (Map<String, Object> row : db) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                System.out.print(entry.getValue() + " ");
            }
            System.out.println();
        }
    }
}