package com.gameboy.memory;

//These are essentially the memory functions
public class Memory {
    private final byte[] memory;

    public Memory() {
        //THis has to be 10000 since we can not do unsigned bytes
        this.memory = new byte[0x10000];
    }
    
    public int read_byte(int address) {
        //Check only 16 bits
        address = address & 0xFFFF;
        //Takes the address then it converts to unsigned
        return memory[address] & 0xFF; 
    }
    
    public void write_byte(int address, int value) {
        //mask it to make sure it has not overflowed
        address = address & 0xFFFF;
        memory[address] = (byte) value;
    }
    
}
