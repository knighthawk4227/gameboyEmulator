package com.gameboy.cpu;

/**sets registers for the cpu and combines 2 8 bit into 16 bit.
 * @author Mitchell Schaeffer
 * @version 1.0
 * 
 */

public class Registers {
    
    /*ints representing the registers using int since gameboy bits were unsigned */
    int a, b, c, d, e, h, l;

    /* This is the flag register  its last 4 bits are always 0
     * bit 7 "zero",
     * bit 6 "subtraction",
     * bit 5 "half carry",
     * bit 4 "carry"
    */
    int f;
    
    int sp;

    int pc;

    //gets the 7th bit posiiton bit mask 
    private static final int ZERO_FLAG_BYTE_POSITION = 1 << 7;
    // gets 6th bit position bit mask
    private static final int SUBTRACT_FLAG_BYTE_POSITION = 1 << 6;
    //gets the 5th bit position bit mask 
    private static final int HALF_CARRY_FLAG_BYTE_POSITION = 1 << 5;
        //gets the 4th bit position bit mask
        private static final int CARRY_FLAG_BYTE_POSITION = 1 << 4;

    /**returns af as 16 bit 
     * @return a & f/ combined as 16 bit
     */
    public int get_af() {
        /*Pushes the first bit into high part left 0's (0000 0000 0000 0000)
        and puts f into lower 0's (0000 0000 0000 0000) this turns it into 16 bit first 8 are a and second 8 are f*/
        return (a << 8) | f;
    }
    
    /** Sets the value of AF register */
    public void set_af(int value) {
        a = ((value & 0xFF00) >> 8);
        //specifically the F bit the gameboy cpu did not use the last 4 bits
        f = (value & 0xF0);
    }
    
    /**
     * returns the 16 bit value of bc combined
     * @return 16 bit value bc
     */
    public int get_bc() {
        return (b << 8) | c;
    }
    
    /**
     * sets value of bc
     * @param value
     */
    public void set_bc(int value) {
        b = ((value & 0xFF00) >> 8);        
        c = (value & 0xFF);
    }
    
    /**
     * returns de as 16 bit
     * @return de merged to 16 bit
     */
    public int get_de() {
        return (d << 8) | e;
    }
    
    /**
     * sets value of d & e with a 16 bit
     * @param value
     *
     */
    public void set_de(int value) {
        d = ((value & 0xFF00) >> 8);
        e = (value & 0xFF);
    }

    /**
     * returns 16 bit of hl merged
     * @return hl merged into 16 bit 
     */
    public int get_hl() {
        return (h << 8) | l;
    }
    
    /**
     * sets value of h & l since the gameboy uses these to set 
     * memory and other things in 16bit ways even though they are 8 bit registers
     * @param value
     */
    public void set_hl(int value) {
        h = ((value & 0xFF00) >> 8);
        l = (value & 0xFF);
    }
    
    /**
     * return 1 if zero flag in  bit position 7
     * @return zero flag 
     */
    public int zero_flag() {
        return (f & ZERO_FLAG_BYTE_POSITION) !=0 ? 1 : 0;
    }
    
    /**
     * Sets the flag to 1 if the value is true otherwise it is 0 
     * @param value
     */
    public void set_zero_flag(boolean value) {
        if (value) {
            f |= ZERO_FLAG_BYTE_POSITION; 
        } else {
            //~ makes it so all the bits flip on the zero flag so it 
            //only changes the 7th bit and all the other bits rely on f register.
            f &= ~ZERO_FLAG_BYTE_POSITION;
        }
    }
    /**
     * return 1 if subtract flag in bit posiiton 6
     * @return subtract flag
     */
    public int subtract_flag() {
        return (f & SUBTRACT_FLAG_BYTE_POSITION) != 0 ? 1 : 0;
    }
    
    /**
     * if the value is true so we have done subtracting then we set it to 1 
     * else we set it to zero and this can reset it to zero as well. 
     * @param value
     */
    public void set_sub_flag(Boolean value) {
        if (value) {
            f |= SUBTRACT_FLAG_BYTE_POSITION;
        } else {
            f &= ~SUBTRACT_FLAG_BYTE_POSITION;
        }
    }

    /**
     * return 1 if half flag is "on" in bit position 5 
     * @return half flag
     */
    public int half_flag() {
        return (f & HALF_CARRY_FLAG_BYTE_POSITION) !=0 ? 1 : 0;
    }
    
    /**
     * Sets the half flag byte position to 1 if value is true
     * else it sets it to zero.
     * 
     * putting in the result as a check into this like (result && 0xFF)
     * will work for this since it is a boolean that is being checked. 
     * @param value
     */
    public void set_half_flag(boolean value) {
        if (value) {
            f |= HALF_CARRY_FLAG_BYTE_POSITION;
        } else {
            f &= ~HALF_CARRY_FLAG_BYTE_POSITION;
        } 
    }
    
    /**
     * return 1 if carry flag is 1 else 0 
     * @return carry flag 
     */
    public int carry_flag() {
        return (f & CARRY_FLAG_BYTE_POSITION) !=0 ? 1 : 0;
    }
    
    /**
     * This function checks if value is true and if it is then 
     * it will set the carry position to 1
     * @param value
     */
    public void set_carry_flag(boolean value) {
        if (value) {
            f |= CARRY_FLAG_BYTE_POSITION;
        } else {
            f &= ~CARRY_FLAG_BYTE_POSITION;
        }
    } 
    


}


