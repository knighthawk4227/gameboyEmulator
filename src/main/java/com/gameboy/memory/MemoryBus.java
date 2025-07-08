package com.gameboy.memory;

public class MemoryBus {
    //Needs to make it 1 more than 0xffff since we can not make it unsigned in java.
    public byte[] memory = new byte[0x10000];    
}
