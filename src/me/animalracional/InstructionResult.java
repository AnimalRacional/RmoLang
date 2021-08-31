package me.animalracional;

public class InstructionResult {
    int line;
    boolean end;
    int pointer;
    public InstructionResult(int lines, boolean ends, int pointerRes) {
        line = lines;
        end = ends;
        pointer = pointerRes;
    }

}
