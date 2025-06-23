package com.gameboy.cpu;

/**
 * This is the instruction file to tell cpu what to do with information 
 * and what register to do it on.
 */
public class Instruction { 
    
    private final InstructionType type;
    private final ArithmeticTarget target;
    
    public Instruction(InstructionType type, ArithmeticTarget target) {
        //Sets this.type (top var to what is passed to constructor)
        this.type = type;
        this.target = target;
    }
    
    /**
     * @return type of instruction (e.g ADD, SUB, OR, XOR)
     */
    public InstructionType getType() {
        return this.type;
    }
    
    /**
     * 
     * @return register instruction is being done on
     */
    public ArithmeticTarget getTarget() {
        return this.target;
    }
    


    
    
}
