package me.animalracional;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        boolean[] argums = new boolean[5];
        int perLine = 10;

        int maxMem = 30;
        String filename = "";
        // Get the arguments
        for(int i = 0; i < args.length; i++){
            String cur = args[i];
            if(cur.equals("-d")){ argums[0] = true; }
            else if(cur.equals("-m")){
                boolean memNum;
                argums[2] = true;
                try{ memNum = Integer.parseInt(args[i+1]) > 0; }
                catch(Exception e){ memNum = false; }
                perLine = memNum ? Integer.parseInt(args[i+1]) : perLine;
            }

            else if(cur.equals("-mem") && i+1 < args.length){
                boolean memNum;
                try{ memNum = Integer.parseInt(args[i+1]) > 0; }
                catch(NumberFormatException e){ memNum = false; }
                argums[3] = memNum;
                maxMem = memNum ? Integer.parseInt(args[i+1]) : 30;
            }
            else if(cur.endsWith(".rmo")){ filename = cur; }
        }
        // Finish getting the arguments

        // Open the file for reading, get all instructions and store labels
        HashMap<Integer, Instruction> instructions = new HashMap<>();
        HashMap<Integer, Integer> labels = new HashMap<>();

        File reading = new File(filename);
        try {
            if (reading.createNewFile()) {
                System.out.println("Created the file!");
            }
        }
        catch(IOException e){
            System.out.println("Couldn't access the file!");
            return;
        }

        Scanner reader;

        try{ reader = new Scanner(reading); }
        catch(Exception e){
            System.out.println("Couldn't read the file!");
            e.printStackTrace();
            return;
        }

        int curLine = 0;
        String line = "";
        try {

            while (reader.hasNextLine()) {
                line = reader.nextLine();
                line = line.trim();
                if (!line.startsWith("#") && !line.isEmpty()) {
                    String[] splitLine = line.split(" ");
                    splitLine[0] = splitLine[0].toUpperCase();

                    Instruction toAdd;
                    String[] parameters = Arrays.copyOfRange(splitLine, 1, splitLine.length);
                    switch (splitLine[0].toUpperCase()) {
                        case "LBL":
                            Instruction lblIn = new Instruction(splitLine[0], splitLine[1], null);
                            if (lblIn.isValid()) {
                                labels.put(Integer.parseInt(splitLine[1]), curLine);
                            } else {
                                System.out.println("Invalid label in line " + curLine+1 + ": " + line);
                                return;
                            }
                            continue;
                        case "POINT":
                        case "APOINT":
                        case "GOTO":
                        case "INVAL":
                        case "IN":
                        case "OUT":
                        case "AOUT":
                            toAdd = new Instruction(splitLine[0], splitLine[1], null);
                            break;
                        case "END":
                            toAdd = new Instruction(splitLine[0], null, null);
                            break;
                        case "SOUT":
                            String param;
                            try {
                                param = line.substring(line.indexOf(' ') + 1);
                                param = param.replace("\\r", "\r");
                                param = param.replace("\\n", "\n");
                                param = param.replace("\\t", "\t");
                                toAdd = new Instruction(splitLine[0], param, null);
                            }
                            catch(Exception e){ toAdd = new Instruction(splitLine[0], null, null); }
                            break;
                        default:
                            toAdd = new Instruction(splitLine[0], splitLine[1], splitLine[2]);
                            break;
                    }
                    if (toAdd.isValid()) {
                        instructions.put(curLine++, toAdd);
                    } else {
                        System.out.println("Invalid instruction in line " + curLine+1 + ": " + line);
                        return;
                    }
                }
            }
        }
        catch(Exception e){
            System.out.println("Something went wrong while in line " + curLine + ": " + line);
            e.printStackTrace();
            return;
        }
        reader.close();

        int[] memory = new int[maxMem];
        int pointer = 0;
        curLine = 0;
        while(curLine < instructions.size()){
            if(argums[0]){ System.out.println("Executing instruction " + instructions.get(curLine).instruction + " " + instructions.get(curLine).parameters[0] + " " + (instructions.get(curLine).parameters[1] != null ? instructions.get(curLine).parameters[1] : "")); }

            InstructionResult res = instructions.get(curLine++).execute(memory, curLine, labels, pointer);
            curLine = res.line;
            pointer = res.pointer;
            if(res.end){ break; }
        }

        if(argums[2]){
            System.out.println("\r\nMemory:");
            printMem(memory, perLine, pointer);
        }

    }

    public static void printMem(int[] mem, int perLine, int pointer){
        for(int i = 0; i < mem.length; i++){
            System.out.print(mem[i] + (i == pointer ? "P " : " "));
            if((i+1) % perLine == 0){ System.out.print("\r\n"); }
        }
    }

    public static boolean isNum(String val){
        try{ Integer.parseInt(val); return true; }
        catch(NumberFormatException e){ return false; }
    }

    public static boolean arrContains(char check, char[] arr){
        for(char cur : arr){
            if(cur == check){ return true; }
        }
        return false;
    }

}

