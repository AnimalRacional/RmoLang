package me.animalracional;

public class InstructionResult {
    int line;
    int[] memory;
    boolean end;
    int pointer;
    public InstructionResult(int lines, int[] memorys, boolean ends, int pointerRes) {
        line = lines;
        memory = memorys;
        end = ends;
        pointer = pointerRes;
    }

}
