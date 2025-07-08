package com.gameboy.cpu;

/**
 * This is the instruction file to tell cpu what to do with information 
 * and what register to do it on.
 */
public class Instruction { 
    
    private final InstructionType type;
    private final ArithmeticTarget target;
    private final int immediate;
    
    public Instruction(InstructionType type, ArithmeticTarget target, int immediate) {
        //Sets this.type (top var to what is passed to constructor)
        this.type = type;
        this.target = target;
        this.immediate = immediate;
    }
    

    /**
     * This is to check immediate value since sometimes we need 
     * to use a value that is being given or returned to us 
     * and in this case we sob and we use this function
     * @return if immediate value has been initialized
     */
    public boolean hasImmediate() {
        return immediate != -1;
    }
    

    /**
     * This is a getter for immediate value
     * @param immediate
     * @return immediate value variable.
     */
    public int getImmediateValue(int immediate) {
        return immediate;
    }
    

    /**
     * This function takes in a byte/ memory location 
     * then it tells the gameboy what to do based on that memory location.
     * @param instructionByte
     * @return Instruction of what to do
     */
    public static Instruction fromByte(int instructionByte, int immediate) {
        return switch (instructionByte) {
            case 0x87 -> new Instruction(InstructionType.ADD, ArithmeticTarget.A, immediate);
            case 0x3E -> new Instruction(InstructionType.LD, ArithmeticTarget.A, immediate);
            default -> throw new IllegalArgumentException("Unknown instruction byte: " + instructionByte);
        };
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
