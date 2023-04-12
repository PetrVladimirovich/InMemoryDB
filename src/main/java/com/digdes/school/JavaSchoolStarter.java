package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaSchoolStarter {

    private final List<Map<String, Object>> db;

    public JavaSchoolStarter() {

        this.db = new ArrayList<>();

    }

    public List<Map<String, Object>> execute(String request) throws Exception {

        Map<String, Object> row = new HashMap<>();
        String lowCase = request.toLowerCase();
        List<String> arrBlocks = new ArrayList<>();
        List<Integer> validateElem = new ArrayList<>();
        boolean isWhere = false;

        if (lowCase.contains("where")) {
            isWhere = true;
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

        if (!arrBlocks.isEmpty()) {
            for (int i = 0; i < db.size(); i++) {
                boolean validate = false;
                boolean leftBracket = false;
                boolean bracketValidate = false;
                int idxOpen = 0;
                int idxClose = 0;

                for (int j = 0; j < arrBlocks.size(); j++) {
                    if (arrBlocks.get(j).equals("(")) {
                        leftBracket = true;
                        idxOpen = j;
                        continue;
                    }

                    if (arrBlocks.get(j).equals(")") && leftBracket && j > idxOpen && idxOpen != 0) {
                        bracketValidate = true;
                        idxClose = j;
                        continue;
                    }

                    if (db.get(i).containsKey(arrBlocks.get(j))) {
                        boolean ok = validationOperationForField(arrBlocks.get(j), arrBlocks.get(j+1));
                        if (ok) {
                            validate = operatorEqual(arrBlocks.get(j),
                                    arrBlocks.get(j+1),
                                    db.get(i).get(arrBlocks.get(j)),
                                    arrBlocks.get(j+2));
                        }
                        if ((validate && j > idxOpen && arrBlocks.size()-1 > j+4 && arrBlocks.get(j+3).equals(")")
                                && !arrBlocks.get(j+4).equals("and"))
                                || (validate && j+3 < arrBlocks.size() && !arrBlocks.get(j+3).equals("and"))
                                || (validate && j+3 == arrBlocks.size()-1)) {
                            break;
                        }else if ((!validate && j > idxOpen && arrBlocks.size()-1 > j+4 && arrBlocks.get(j+3).equals(")")
                                    && arrBlocks.get(j+4).equals("and"))
                                    || (!validate && j+3 < arrBlocks.size() && arrBlocks.get(j+3).equals("and"))
                                    || (!validate && j+3 == arrBlocks.size()-1)) {
                            break;
                        }
                    }
                }

                if (validate) {
                    validateElem.add(i);
                }
            }
        }

        List<Map<String, Object>> answer = new ArrayList<>();

        if (lowCase.startsWith("insert values")) {
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
            db.add(row);
            answer.add(row);

        } else if (lowCase.startsWith("update values")) {
            String temp = lowCase.replaceAll("\\s", "")
                    .replace("updatevalues", "")
                    .replace("'", "");
            String[] arr = temp.split(",");

            if (!validateElem.isEmpty()) {
                for (Integer i : validateElem) {
                    for (String str : arr) {
                        String[] equalSplit = str.split("=");
                        if (equalSplit[0].equals("lastname")) {
                            db.get(i).put("lastName", parseLastName(request));
                        } else {
                            db.get(i).put(equalSplit[0], mapperToType(equalSplit[0], equalSplit[1]));
                        }
                    }
                    answer.add(db.get(i));
                }

            }else if (!db.isEmpty() && !isWhere) {
                for (Map<String, Object> map : db) {
                    for (String str : arr) {
                        String[] equalSplit = str.split("=");
                        if (equalSplit[0].equals("lastname")) {
                            map.put("lastName", parseLastName(request));
                        } else {
                            map.put(equalSplit[0], mapperToType(equalSplit[0], equalSplit[1]));
                        }
                    }
                    answer.add(map);
                }
            }

        } else if (lowCase.startsWith("delete")) {
            if (!validateElem.isEmpty()) {
                for (int i = validateElem.size()-1; i != 0; i--) {
                    answer.add(db.get(i));
                    db.remove(i);
                }
            }else if (!db.isEmpty() && !isWhere) {
                answer = List.copyOf(db);
                db.clear();
            }

        } else if (lowCase.startsWith("select")) {
            if (!validateElem.isEmpty()) {
                for (int i = validateElem.size()-1; i != 0; i--) {
                    answer.add(db.get(i));
                }
            }else if (!db.isEmpty() && !isWhere) {
                for (Map<String, Object> map : db) {
                    answer.add(map);
                }
            }
        } else {

            throw new Exception(String.format("Такой операции не существует: %s...", request.substring(0, 14)));

        }

        return answer;

    }

    private boolean operatorEqual(String field, String sign, Object inDb, String inInput) throws Exception{
        switch (field) {
            case "lastName" -> {
                if (sign.equals("!=")) {
                    return !inDb.toString().equals(inInput);

                }else if (sign.equals("=")) {
                    return inDb.toString().equals(inInput);

                }else if (sign.equals("like")) {
                    if (inInput.startsWith("%") && inInput.endsWith("%")) {
                         int indexFind = inDb.toString().indexOf(inInput.replace("%", ""));
                        return indexFind != 0 && inInput.length() - 2 + indexFind < inDb.toString().length();
                    }else if (inInput.startsWith("%")) {
                        return inDb.toString().endsWith(inInput.replace("%", ""));
                    }else if (inInput.endsWith("%")) {
                        return inDb.toString().startsWith(inInput.replace("%", ""));
                    }

                }else if (sign.equals("ilike")) {
                    if (inInput.startsWith("%") && inInput.endsWith("%")) {
                        int indexFind = inDb.toString().toLowerCase().indexOf(inInput.toLowerCase().replace("%", ""));
                        return indexFind != 0 && inInput.length() - 2 + indexFind < inDb.toString().length();
                    }else if (inInput.startsWith("%")) {
                        return inDb.toString().toLowerCase().endsWith(inInput.toLowerCase().replace("%", ""));
                    }else if (inInput.endsWith("%")) {
                        return inDb.toString().toLowerCase().startsWith(inInput.toLowerCase().replace("%", ""));
                    }
                }
            }

            case "id", "age" -> {
                if (sign.equals("=")) {
                    return (Long) inDb == Long.parseLong(inInput);
                }else if (sign.equals("!=")) {
                    return (Long) inDb != Long.parseLong(inInput);
                }else if (sign.equals(">=") || sign.equals(">")) {
                    return (Long) inDb > Long.parseLong(inInput) || (Long) inDb == Long.parseLong(inInput);
                }else if (sign.equals("<=") || sign.equals("<")) {
                    return (Long) inDb < Long.parseLong(inInput) || (Long) inDb == Long.parseLong(inInput);
                }
            }

            case "cost" -> {
                if (sign.equals("=")) {
                    return (Double) inDb == Double.parseDouble(inInput);
                }else if (sign.equals("!=")) {
                    return (Double) inDb != Double.parseDouble(inInput);
                }else if (sign.equals(">=") || sign.equals(">")) {
                    return (Double) inDb > Double.parseDouble(inInput) || (Double) inDb == Double.parseDouble(inInput);
                }else if (sign.equals("<=") || sign.equals("<")) {
                    return (Double) inDb < Double.parseDouble(inInput) || (Double) inDb == Double.parseDouble(inInput);
                }
            }

            case "active" -> {
                if (sign.equals("=")) {
                    return (Boolean) inDb == Boolean.parseBoolean(inInput);
                }else if (sign.equals("!=")) {
                    return (Boolean) inDb != Boolean.parseBoolean(inInput);
                }
            }

            default -> {
                throw new Exception(String.format("Такого поля нет в таблице: '%s'", field));
            }
        }
        return false;
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
        int[] index = {newRequest.indexOf("and", i), newRequest.indexOf("or", i), newRequest.indexOf(")", i)};
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