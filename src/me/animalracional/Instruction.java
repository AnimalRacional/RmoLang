package me.animalracional;


import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import static me.animalracional.Main.arrContains;


public class Instruction {
    static char[] comparators = "<>=#".toCharArray();
    String instruction;
    String[] parameters;
    public Instruction(String instru, String par1, String par2){
        instruction = instru.toUpperCase();
        parameters = new String[]{par1, par2};
    }

    public boolean isValid(){
        switch(instruction){
            case "APOINT":
            case "INVAL":
            case "IN":
                parameters[1] = null;
                return isMemRef(parameters[0]);
            case "INLINE":
                return isMemRef(parameters[0]) && isPosNum(parameters[1]);

            case "LBL":
                parameters[1] = null;
                return isNum(parameters[0]);
            case "POINT":
            case "GOTO":
            case "OUT":
            case "AOUT":
                parameters[1] = null;
                return isIntRef(parameters[0]);
            case "CACOUT":
            case "ACOUT":
            case "COUT":
                return isMemRef(parameters[0]) && isPosNum(parameters[1], false);

            case "SET":
                return isMemRef(parameters[0]) && isIntRef(parameters[1]);

            case "ADD":
            case "REM":
            case "SUB":
            case "MUL":
            case "DIV":
            case "MOD":
                return isIntRef(parameters[0]) && isMemRef(parameters[1]);

            case "GOTOIF":
                return isCondition(parameters[0]) && isIntRef(parameters[1]);
            case "END":
                return true;
            case "SOUT":
                return parameters[0] != null;
            default:
                return false;
        }
    }

    public InstructionResult execute(int[] memory, int line, HashMap<Integer, Integer> labels, int pointer){
        boolean change = false;
        int targLine = 0;
        boolean endsIt = false;
        int newPointer = pointer;
        switch(instruction){
            case "IN":
                Scanner input = new Scanner(System.in);
                int memAdd = getNumFromMem(parameters[0], pointer);
                char toAddIn = input.next().charAt(0);
                memory[memAdd] = toAddIn;
                break;
            case "INVAL":
                input = new Scanner(System.in);
                memAdd = getNumFromMem(parameters[0], pointer);
                String inpt = "";
                while(!isNum(inpt)){
                    inpt = input.nextLine();
                }
                memory[memAdd] = Integer.parseInt(inpt);
                break;
            case "INLINE":
                input = new Scanner(System.in);
                String inp = input.nextLine();
                int length = Integer.parseInt(parameters[1]);
                length = Math.min(length, inp.length());
                inp = inp.substring(0, length);
                int curMem = getNumFromMem(parameters[0], pointer);
                for(int i = 0; i < length; i++){
                    memory[curMem+i] = inp.charAt(i);
                }
                break;
            case "OUT":
                System.out.print(getNumFromIntRef(parameters[0], memory, pointer));
                break;
            case "SOUT":
                System.out.print(parameters[0]);
                break;
            case "AOUT":
                System.out.print((char)(getNumFromIntRef(parameters[0], memory, pointer)));
                break;
            case "COUT":
                length = Integer.parseInt(parameters[1]);
                curMem = getNumFromMem(parameters[0], pointer);
                for(int i = 0; i < length; i++){
                    System.out.println(memory[curMem+i]);
                }
                break;
            case "ACOUT":
                length = Integer.parseInt(parameters[1]);
                curMem = getNumFromMem(parameters[0], pointer);
                for(int i = 0; i < length; i++){
                    if(memory[curMem+i] == 0){ break; }
                    System.out.print((char)memory[curMem+i]);
                }
                break;
            case "CACOUT":
                length = Integer.parseInt(parameters[1]);
                curMem = getNumFromMem(parameters[0], pointer);
                for(int i = 0; i < length; i++){
                    System.out.print((char)memory[curMem+i]);
                }
                break;
            case "APOINT":
                newPointer = getNumFromMem(parameters[0], pointer);
                break;
            case "POINT":
                int num = getNumFromIntRef(parameters[0], memory, pointer);
                newPointer += num;
                break;
            case "SET":
                int memLoc = getNumFromMem(parameters[0], pointer);
                memory[memLoc] = getNumFromIntRef(parameters[1], memory, pointer);
                break;
            case "ADD":
                int toAddOper;
                toAddOper = getNumFromIntRef(parameters[0], memory, pointer);
                memLoc = getNumFromMem(parameters[1], pointer);
                memory[memLoc] = memory[memLoc] + toAddOper;
                break;
            case "REM":
            case "SUB":
                toAddOper = getNumFromIntRef(parameters[0], memory, pointer);
                memLoc = getNumFromMem(parameters[1], pointer);
                memory[memLoc] = memory[memLoc] - toAddOper;
                break;
            case "MUL":
                toAddOper = getNumFromIntRef(parameters[0], memory, pointer);
                memLoc = getNumFromMem(parameters[1], pointer);
                memory[memLoc] = memory[memLoc] * toAddOper;
                break;
            case "DIV":
                toAddOper = getNumFromIntRef(parameters[0], memory, pointer);
                memLoc = getNumFromMem(parameters[1], pointer);
                memory[memLoc] = memory[memLoc] / toAddOper;
                break;
            case "MOD":
                toAddOper = getNumFromIntRef(parameters[0], memory, pointer);
                memLoc = getNumFromMem(parameters[1], pointer);
                memory[memLoc] = memory[memLoc] % toAddOper;
                break;
            case "GOTO":
                change = true;
                targLine = getNumFromIntRef(parameters[0], memory, pointer);
                break;
            case "GOTOIF":
                if(assertCondition(parameters[0], memory, pointer)){
                    change = true;
                    targLine = getNumFromIntRef(parameters[1], memory, pointer);
                }
                break;
            case "END":
                endsIt = true;
                break;
        }
        /*

SET (mem location) (integer reference) V
ADD (integer reference) (mem location) V
SUB (integer reference) (mem location) V
MUL (integer reference) (mem location) V
DIV (integer reference) (mem location) V
OUT (integer reference) V
AOUT (integer reference) - Outputs ASCII character V
IN (mem location) V
LBL (integer)
GOTO (integer reference)
GOTOIF (condition) (integer reference)


 */
        return new InstructionResult(change ? labels.get(targLine) : line, memory, endsIt, newPointer);
    }

    public static int getNumFromIntRef(String intRef, int[] mem, int pointer){
        if(isMemRef(intRef)){ return mem[isGetPointer(intRef) ? pointer : getNumFromMem(intRef, pointer)]; }
        else{
            if(intRef.equalsIgnoreCase("rand")){
                return new Random().nextInt(100);
            }
            else if(isGetPointer(intRef)){
                return mem[pointer];
            }
            else{
                return Integer.parseInt(intRef);
            }
        }
    }

    public static boolean isGetPointer(String ref){
        return ref.startsWith("$") && ref.substring(1, ref.length()).equalsIgnoreCase("p");
    }
    public static int getNumFromMem(String mem, int ptr)
    {
        String tmp = mem.substring(1);
        return tmp.equalsIgnoreCase("p") ? ptr : Integer.parseInt(tmp);
    }
    public static boolean isMemRef(String ref){
        return ref.startsWith("$") && (isPosNum(ref.substring(1), true) || isGetPointer(ref));
    }
    public static boolean isIntRef(String ref){
        return isMemRef(ref) || isNum(ref) || ref.equalsIgnoreCase("rand") || isGetPointer(ref);
    }
    public static boolean isIntRef(String ref, boolean positive){
        if(positive) {
            return isMemRef(ref) || isPosNum(ref) || ref.equalsIgnoreCase("rand");
        }
        else{
            return isIntRef(ref);
        }
    }
    public static boolean isIntRef(String ref, boolean positive, boolean zero){
        if(positive){
            return isMemRef(ref) || isPosNum(ref, zero) || ref.equalsIgnoreCase("rand");
        }
        else{
            return isIntRef(ref);
        }
    }
    public static boolean isPosNum(String num, boolean zero){
        if(isNum(num)) {
            int val = Integer.parseInt(num);
            return val > 0 || (zero && val == 0);
        }
        return false;
    }
    public static boolean isPosNum(String num){

        return isNum(num) && Integer.parseInt(num) > 0;

    }
    public static boolean isCondition(String con){

        String[] split = { "a", "a" };
        char comparator = 0;
        int compInd = -1;
        for(int i = 0; i < con.length(); i++){
            if(arrContains(con.charAt(i), comparators)){
                comparator = con.charAt(i);
                compInd = i;
                break;
            }
        }
        if(compInd > -1){ split = con.split(comparator + ""); }
        return split.length == 2 && isIntRef(split[0]) && isIntRef(split[1]);
    }
    public static boolean assertCondition(String con, int[] memory, int ptr){
        char comparator = 0;
        for(int i = 0; i < con.length(); i++){
            if(arrContains(con.charAt(i), comparators)){
                comparator = con.charAt(i);
                break;
            }
        }
        String[] split = con.split(""+comparator);
        int num1 = getNumFromIntRef(split[0], memory, ptr);
        int num2 = getNumFromIntRef(split[1], memory, ptr);
        switch(comparator){
            case '<':
                return num1 < num2;
            case '>':
                return num1 > num2;
            case '=':
                return num1 == num2;
            case '#':
                return num1 != num2;
        }

        return false;
    }

    public static boolean isNum(String val){
        try{ Integer.parseInt(val); return true; }
        catch(NumberFormatException e){
            return val.equalsIgnoreCase("RAND");
        }

    }
}
