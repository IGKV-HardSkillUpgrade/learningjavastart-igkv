package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SearchAdultUsers {
    public static void main(String[] args) {
        List<User> listUsers = scanUsers();
        System.out.println(listUsers.stream().filter(p -> p.getAge() >= 18).map(User::getName).collect(Collectors.joining(", ")));
    }

    public static List<User> scanUsers(){
        List<User> copyUsers = new ArrayList<>();
        try (Scanner scanner = new Scanner(System.in)){
                int size = readInt(scanner);
                while (size > 0) {
                    String name = scanner.nextLine();
                    int age = readInt(scanner);
                    if (age <= 0){
                        System.out.println("Incorrect input. Age <= 0");
                    } else {
                        copyUsers.add(new User(name, age));
                        size--;
                    }
                }
        }
        return copyUsers;
    }

    private static int readInt(Scanner scanner) {
        while (true){
            String line = scanner.nextLine();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Could not parse a number. Please, try again");
            }
        }
    }
}

