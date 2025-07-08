package com.gameboy.cpu;

import com.gameboy.memory.MemoryBus;
import com.gameboy.memory.Memory;

/**
 * This is the CPU functionality and operations
 * @author Mitchell Schaeffer 
 * @version 1.0
 */
public class Cpu {
    

    //reference to registers / variable we fill using constructor 
    private Registers registers;
    //Assigning the variable to this so we can determine when to use this
    //Program counter 
    public int pc;
    //Memory bus class
    MemoryBus memory = new MemoryBus();
    //Memory functions
    Memory mem = new Memory();

    //

    /**
     * This is the constructor for CPU
     * @param registers
     */
    public Cpu(Registers registers, MemoryBus bus) {
        this.registers = registers;
        this.memory = bus;
        this.pc = 0x0000;
    }
    /**
     * Takes in a register target and returns the value of 
     * t
     * hat target as an int since they are stored as ints in 
     * register anyways
     * @param reg
     * @return Arithmetic target as an int
     */
    public int getRegisterValue(ArithmeticTarget reg) {

        return switch (reg) {
            case A -> registers.a;
            case B -> registers.b;
            case C -> registers.c;
            case D -> registers.d;
            case E -> registers.e;
            case H -> registers.h;
            case L -> registers.l;
            default -> throw new IllegalArgumentException("This can not work");
        };
    }

    public void set_register_value(ArithmeticTarget target, int value) {
        //overwrite value so that it is stored as 8 bits
        value = value & 0xFF;
        switch (target) {
            case A -> registers.a = value;
            case B -> registers.b = value;
            case C -> registers.c = value;
            case D -> registers.d = value;
            case E -> registers.e = value;
            case H -> registers.h = value;
            case L -> registers.l = value;
            default -> throw new IllegalArgumentException("There was an error setting the" +
                "value of register" + target);
        }
    }
    
    // public void step() {    
    //     //read current memory at program counter pos
    //     int instruction = mem.read_byte(pc);
         
    //     //convert the byte into some instruction 
    //     Instruction instruction = new Instruction();
    // }
    
    public void execute(Instruction instruction) {
        //get type of thing we doing (e.g ADD, SUB)
        InstructionType type = instruction.getType();
        //Get register we doing type to (e.g A register)
        ArithmeticTarget target = instruction.getTarget();
        int value = getRegisterValue(target);  
        
        //Might fuck around and make this a switch later 
        if (type == InstructionType.ADD) {
            //function only adds one register to another as of right now
            add(value);
        } else if (type == InstructionType.SUB) {
            sub(value);
            
        } else if (type == InstructionType.OR) {
            or(value);
        } else if (type == InstructionType.AND) {
            and(value);
        } else if (type == InstructionType.XOR) {
            xor(value);
        } else if(type == InstructionType.LD) {
            ld(target, value); 
        }else if(type == InstructionType.JP){
            return;
            //write the jp function.
        }
         else if (type == InstructionType.NOP) {
            return;
        }

    }                 
    
    /* read current value from target register  **check** 
     * add value from target to reg a **check** 
     * add the value to the value in targeted register and make sure to handle overflow  **check**
     * update flag register  **check** 
     * write updated value to register itself  **check**
    */
    /**
     * Takes a register value then adds them to a reg
     * then sets flags for f reg 
     * @param regValue
     * @return A register added 
     */
    public int add(int regValue) {
        int a = registers.a;
        int result = a + regValue;        
        //mask to stay in 8 bits. (GB only has 8 bit reg) 
        //set value to registers 
        registers.set_zero_flag((result & 0xFF) == 0); 
        registers.set_sub_flag((false));
        registers.set_carry_flag((result > 0xFF));
        registers.set_half_flag((a & 0x0F) + (regValue & 0x0F) > 0x0F);

        // set register a value
        registers.a = (result & 0xFF);

        return registers.a; 
    }
    
    /**
     * This takes the value we are trying to subtract and subtracts it from A 
     * since that is the register that does the adding and subtracting
     * @param regValue
     * @return
     */
    public int sub(int regValue) {
        int a = registers.a;
        int result = a - regValue;

        //Register check
        registers.set_zero_flag((result & 0xFF) == 0); 
        registers.set_sub_flag((true));
        /*if the number we are subtracting is bigger than 
        the number we want to subtract then obviously we are going 
        to have to carry or do something since we can not represent negatives*/
        registers.set_carry_flag((regValue > a));
        /* if the first 4 bits of a are less than regValue 
        it's going to need to round from front 4 bits*/ 
        registers.set_half_flag((a & 0x0F) < (regValue & 0x0F));
        
        registers.a = (result & 0xFF);
        
        return registers.a;
    }
    

    /**
     * This is an or operation for one register against another one 
     * I don't fully understand the use of this totally yet 
     * but it is apart of the logic and I need to do some more research on it.
     * @param regValue
     * @return register a or'd with register passed to it.
     */
    public int or(int regValue) {
        int a = registers.a;
        int result = a | regValue;
        
        //The zero flag is the only one we need to check the other 
        //flags do not really make sense to check since there is no carrying beyond 8 bits.
        registers.set_zero_flag((result) == 0x00);
        registers.set_sub_flag(false);
        registers.set_carry_flag(false);
        registers.set_half_flag(false);
        
        
        registers.a = (result & 0xFF);
        return registers.a;
    }
    
    /**
     * This is the and operation it ands the register passed in 
     * and the a register 
     * @param regValue
     * @return result of anding a and reg passed in 
     */
    public int and(int regValue){ 
        int a = registers.a;
        int result = a & regValue;

        //The zero flag is the only one we need to check the other 
        //flags do not really make sense to check since there is no carrying beyond 8 bits.
        registers.set_zero_flag((result) == 0x00);
        registers.set_sub_flag(false);
        registers.set_carry_flag(false);
        registers.set_half_flag(false);
        

        registers.a = (result & 0xFF);
        return registers.a;
        
    }
    

    /**
     * this is an XOR so if the regValue has a bit in pos 2 and so does 
     * the a register then it sets it to 0 but if regValue has a 1 in bit 3 and 
     * a does not then it will set it to 1  (if both bits in same spot are 1 it is 0)
     * then it treats 0 like normal if both are 0 it is 0 
     * @param regValue
     * @return XOR of regValue and A register 
     */
    public int xor(int regValue) {
        int a = registers.a;
        int result = a ^ regValue;

        registers.set_zero_flag((result) == 0x00);
        registers.set_sub_flag(false);
        registers.set_carry_flag(false);
        registers.set_half_flag(false);
        

        registers.a = (result & 0xFF);
        return registers.a;

    }
    
    /**
     * This function takes the register it wants to target then adds 1 to it
     * @param reg
     */
    public void inc(ArithmeticTarget reg) {
       int value = getRegisterValue(reg); 
       int result = ((value & 0xFF) + 1);
       set_register_value(reg, result);
       
        registers.set_zero_flag((result) == 0x00);
        registers.set_sub_flag(false);
        registers.set_carry_flag((value & 0xFF) + 1 > 0xFF);
        registers.set_half_flag((value & 0x0F) + 1 > 0x0F);
    }

    /**
     * This takes in a register and decrements it by 1
     * @param reg
     */
    public void dec(ArithmeticTarget reg) {
       int value = getRegisterValue(reg); 
       int result = ((value & 0xFF) - 1);
       set_register_value(reg, result);
       
        registers.set_zero_flag((result) == 0x00);
        registers.set_sub_flag(true);
        // registers.set_carry_flag((value & 0xFF) - 1 > 0xFF);
        //check if lower 4 bits are 0 causing the need to carry from bit 4
        registers.set_half_flag((value & 0xFF) == 0x00);
    }
    
    /**
     * This functions shifts the targeted register to the left by 1
     * and will put the bit that is in the 7th spot into the carry flag as well.
     * @param reg
     */
    public void rlca(ArithmeticTarget reg) {
        int value = getRegisterValue(reg); 
        //this moves the seventh bit the the start and then it extrapolates it.
        int carry = (value >> 7) & 0x01;
        System.out.println("The 7th bit of a is: " + carry);
        //Moves everything left by 1. adds carry to it and sets it to 8 bits.
        int result = ((value << 1) | carry & 0xFF );

        set_register_value(reg, result);
        //The only flags this function touches is the carry 
        registers.set_carry_flag(carry == 1);
        registers.set_half_flag(false);
        registers.set_sub_flag(false);
    }
    
    /**
     * This functions shifts the targeted register to the left by 1
     * and will put the carry flag into bit 0  .
     * @param reg
     */
    public void rla(ArithmeticTarget reg) {
        int currentCarry = registers.carry_flag();
        int value = getRegisterValue(reg); 
        int valueCarry = ((value >> 7) & 0x01 );
        //Moves everything left by 1. adds carry to it and sets it to 8 bits.
        //this should be named get carry flag but I am dumb
        int result = (((value << 1) | currentCarry) & 0xFF );

        set_register_value(reg, result);
        //The only flags this function touches is the carry 
        registers.set_zero_flag(false);        
        registers.set_carry_flag(valueCarry == 1);
        registers.set_half_flag(false);
        registers.set_sub_flag(false);
    }
    
    /**
     * This functions shifts the targeted register to the right by 1
     * and will put the bit that is in the 0th spot into the carry flag and the 7th bit.
     * @param reg
     */
    public void rrca(ArithmeticTarget reg) {
        int value = getRegisterValue(reg); 
        //This takes the 0 bit and stores it?
        int carry = (value) & 0x01;
        System.out.println("The 7th bit of a is: " + carry);
        //Moves everything left by 1. adds carry to it and sets it to 8 bits.
        int result = ((value >> 1) | (carry << 7) & 0xFF);

        set_register_value(reg, result);
        //This function  
        registers.set_carry_flag(carry == 1);
        registers.set_half_flag(false);
        registers.set_sub_flag(false);
    }

    public void rra(ArithmeticTarget reg) {
        int value = getRegisterValue(reg);
        int carry = (value & 0x01);
        System.out.println("the carry value is: " + carry);
        int result = (((value >> 1) | registers.carry_flag() << 7) & 0xFF);   
        registers.set_carry_flag(carry == 1);
        System.out.println("the RRA is " + result);
        
        set_register_value(reg, result);
        
        //The only flags this function touches is the carry 
        registers.set_zero_flag(false);        
        registers.set_half_flag(false);
        registers.set_sub_flag(false);
    }
    
    /**
     * Takes in a register and then compares them to see if they are equal by
     * subtracting them from each other and if it is a 0 we set the z flag to 0
     * @param reg
     */
    public void cp(ArithmeticTarget reg) {
        int value = getRegisterValue(reg);
        int a = registers.a;
        int result = a - value;
        
        System.out.println("we inside the compare function");
        registers.set_zero_flag(result == 0);
        registers.set_sub_flag(true);
        /*if the lower 4 bits of a are less than the lower 
        4 of value then you need to half carry*/
        registers.set_half_flag((a & 0x0f) < (value & 0x0f));
        /*if the value of a is less than value then it will be a full carry */
        registers.set_carry_flag((a & 0xFF) < (value & 0xFF));

    } 

    /**
     * This is the add with carry it takes the add and also adds the carry 
     * @param regValue
     * @return a register with value and carry added 
     */
    public int adc(int regValue) {
        int a = registers.a;
        int carry = registers.carry_flag(); 
        int result = a + carry + regValue;        
        //mask to stay in 8 bits. (GB only has 8 bit reg)
        //set value to registers
        registers.set_zero_flag((result & 0xFF) == 0);
        registers.set_sub_flag((false));
        registers.set_carry_flag((result > 0xFF));
        registers.set_half_flag(((a & 0x0F) + (regValue & 0x0F) + carry) > 0x0F);

        // set register a value
        registers.a = (result & 0xFF);

        return registers.a;
    }

    /**
     * subtract with carry checks the carry flag and also subs that 
     * @param regValue
     * @return a register with the value and carry subtracted
     */
    public int subc(int regValue) {
        int a = registers.a;
        int carry = registers.carry_flag(); 
        int result = a - carry -regValue;        
        //mask to stay in 8 bits. (GB only has 8 bit reg)
        //set value to registers
        registers.set_zero_flag((result & 0xFF) == 0);
        registers.set_sub_flag((true));
        registers.set_carry_flag((regValue + carry > a));
        registers.set_half_flag(((a & 0x0F) - (regValue & 0x0F) - carry) < 0x0F);

        // set register a value
        registers.a = (result & 0xFF);

        return registers.a;
    }
    

    /**
     * This adds a number to the HL register 
     * @param value
     * 
     */
    public void add_hl(int value) {
        int hl = registers.get_hl();
        int result = hl + value;
        registers.set_hl(result & 0xFFFF);

        registers.set_sub_flag((false));
        registers.set_carry_flag((result > 0xFFFF));
        registers.set_half_flag((hl & 0x0FFF) + (value & 0x0FFF) > 0x0FFF);

    }
    

    /**
     * This function increments the HL register by 1
     */
    public void inc_hl() {
        int hl = registers.get_hl();
        hl++;
        registers.set_hl(hl & 0xFFFF);
    }
    
    /**
     * This function decrements hl
     */
    public void dec_hl() {
        int hl = registers.get_hl();
        hl--;
        registers.set_hl(hl);
    }
    

    /**
     * This function when called checks the a register and the flags 
     * to determine if it needs to change it so it is a valid BCD since 
     * letters are not represented as numbers in BCD  so we offset by 6 
     * to adjust this 
     */
    public void daa() {
        //This is bad and I should use a getter and setter but TOO late might do later.
        int a = registers.a;
        int n = registers.subtract_flag();
        int z = registers.zero_flag();
        int h = registers.half_flag();
        int c = registers.carry_flag();

        if (n == 0) {
            if (c == 1 || a > 0x99) {
               a += 0x60; 
               registers.set_carry_flag(true);
            } 
            if (h == 1 || (a & 0x0f) > 0x09) {
                a += 0x06;
            }
        } else {
            if (c == 1 || a > 0x99) {
               a -= 0x60; 
            } 
            if (h == 1 || (a & 0x0f) > 0x09) {
                a -= 0x06;
            }

        }
        
        registers.set_zero_flag(a == 0);
        registers.set_half_flag(false);;
        registers.a = a;
        
    }
    

    /**
     * this function allows for making the value of one register to another register.
     * / overwriting a register with another register
     * @param reg
     * @param reg2
     */
    public void ld(ArithmeticTarget reg, ArithmeticTarget reg2) {
        int register_2 = getRegisterValue(reg2);
        set_register_value(reg, register_2); 
    }
    
    /**
     * this function allows for giving a value to a targeted register.
     * @param reg
     * @param value
     */
    public void ld(ArithmeticTarget reg, int value) {
        set_register_value(reg, value);
    }
    

    /**
     * This function takes in a register then it shifts it one to the left 
     * sets the carry flag to what bit 7 was then resets bit 0 to 0
     * making it so that bit 0 is always 0 
     * @param reg
     */
    public void sla(ArithmeticTarget reg) {
        int value = getRegisterValue(reg);
        int carry = (value >> 7 & 0x01);
        int result = ((value << 1 | 0) & 0xFF);       
        
        set_register_value(reg, result);
        

        registers.set_zero_flag(false);
        registers.set_carry_flag(carry == 1);
        registers.set_half_flag(false);
        registers.set_sub_flag(false);

    }
    

    /**
     * This function takes the targeted register then bit shifts to teh right by 1 
     * then it will take the 7th bit and set it to the carry flag and reset bit 7 
     * aka set bit 7 to 0
     * @param reg
     */
    public void sra(ArithmeticTarget reg) {
        int value = getRegisterValue(reg);
        int carry = (value & 0x01);
        int bit7 = ((value >> 7) & 0xFF);
        int result = ((value >> 1 | bit7 << 7) & 0xFF);
        
        set_register_value(reg, result);
        
        registers.set_zero_flag(false);
        registers.set_carry_flag(carry == 1);
        registers.set_half_flag(false);
        registers.set_sub_flag(false);
    }
    

    /**
     * This function takes in a targeted register and then it bit shifts it 
     * to the right and then sets the 7th bit to the carry as well as 
     * resetting the 7th bit to 0
     * @param reg
     */
    public void srl(ArithmeticTarget reg) {
        int value = getRegisterValue(reg);
        int carry = (value & 0x01);
        //bit 7 is 0 always 
        int result = (((value >> 1) | 0 << 7) & 0xFF);
        
        set_register_value(reg, result);
        
        registers.set_carry_flag(carry == 1);
        registers.set_sub_flag(false);
        registers.set_zero_flag(false);
        registers.set_half_flag(false);

    }
    


    /**
     * This function sets the carry flag to 1 and 
     * sets the half carry and teh sub flag to 0
     */
    public void scf() {
        registers.set_carry_flag(true);
        registers.set_half_flag(false);
        registers.set_sub_flag(false);
    }
    


    /**
     * This function toggles the carry flag so if it is 1 then it 
     * switches it to 0 and if it is 0 then it switches it to 1
     */
    public void ccf() {
        int carry_flag = registers.carry_flag();
        switch(carry_flag) {
            case 0 -> registers.set_carry_flag(true);
            case 1 -> registers.set_carry_flag(false);
        }
    }
    
    /**
     * This function is used to tell the cpu where there is free space 
     * in memory and then allow you to store information there
     * @param reg
     */
    public void push(RegisterPair reg) {
        
        switch(reg) {
            case AF: 
                break;
        }
    }
    
    /**
     * This function takes in a register and the bit location we 
     * want to check and set the zero flag to 1 if the bit is not set
     * @param reg register we are targetting
     * @param bit bit in byte we are checking
     * @return true if bit is set
     */
    public void bit(ArithmeticTarget reg, int bit) {
        int value = getRegisterValue(reg);
        int bitMask = ((value >> bit) & 0x01);
        registers.set_zero_flag(bitMask != 1);
        registers.set_sub_flag(false);
        registers.set_half_flag(true);
    }
    
    
    /**
     * This function gets a specific bit then resets that bit to 0
     * using a mask and & it with the value
     * 
     * @param reg
     * @param bit
     */
    public void reset(ArithmeticTarget reg, int bit) {
        int value = getRegisterValue(reg);
        //creats an 8 bit mask where all bits are one
        // except the bit we specified 
        int bitMask = (~(1 << bit) & 0xFF);
        int result = ((value & bitMask) & 0xFF);
        set_register_value(reg, result);
    }

    /**
     * this functions only works for add since every instruction 
     * needs something a little different so they can not work 
     * the same in every single one like add and sub :( I wasted
     * my time sadge.
     * @param result
     * @param a
     * @param regValue
     */
    public void check_flags(int result, int a, int regValue) {
        registers.set_zero_flag((result & 0xFF) == 0); 
        registers.set_sub_flag((result > a));
        registers.set_carry_flag((result > 0xFF));
        registers.set_half_flag((a & 0x0F) + (regValue & 0x0F) > 0x0F);
        
    }
    

    public static void main(String[] args) {
        Registers regs = new Registers();
        MemoryBus memory = new MemoryBus();
        Cpu cpu = new Cpu(regs, memory);
        
        
        regs.a = 0x02;
        regs.b = 0x00;


        System.out.println(cpu.getRegisterValue(ArithmeticTarget.B));
        System.out.println(cpu.getRegisterValue(ArithmeticTarget.A));
        // cpu.execute(new Instruction(InstructionType.ADD, ArithmeticTarget.B));
        System.out.println(cpu.getRegisterValue(ArithmeticTarget.B));

        // cpu.rlca(ArithmeticTarget.A);
        
        // System.out.println("The carry flag is currently: " + cpu.registers.carry_flag());       
        cpu.rra(ArithmeticTarget.A);

        // System.out.println("A Var is: " + regs.a);
        // if (regs.zero_flag() == 0) {
        //     System.out.println("Zero flag is False or NOT Set ");
        // } else {
        //     System.out.println("Zero flag is True or Set");
        // }


    }

}
    




