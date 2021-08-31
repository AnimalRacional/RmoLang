package me.animalracional;

public class InstructionResult {
    int line;
    // TODO find a way not to have a memory in all instruction results
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
