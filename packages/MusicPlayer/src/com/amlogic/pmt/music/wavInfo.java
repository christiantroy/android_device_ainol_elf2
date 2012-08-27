package com.amlogic.pmt.music;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class wavInfo 
{
	private long samples = 0;
	private long bitrate = 0;
	
	public int toInt(byte[] b) {
        return ((b[3] << 24) + (b[2] << 16) + (b[1] << 8) + (b[0] << 0));
    }
    public short toShort(byte[] b) {
        return (short)((b[1] << 8) + (b[0] << 0));
    }
    
    public byte[] read(RandomAccessFile rdf, int pos, int length) throws IOException {
        rdf.seek(pos);
        byte result[] = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = rdf.readByte();
        }
        return result;
    }

    public wavInfo(String path) throws IOException {

        File f = new File(path);
        RandomAccessFile rdf = null;
        rdf = new RandomAccessFile(f,"r");
        
        this.samples = toInt(read(rdf, 24, 4))/1000;
        this.bitrate = toInt(read(rdf, 28, 4))*8/1000;
        
        //System.out.println("audio size: " + toInt(read(rdf, 4, 4)));
        //System.out.println("audio format: " + toShort(read(rdf, 20, 2)));
        //System.out.println("num channels: " + toShort(read(rdf, 22, 2)));
        //System.out.println("sample rate: " + toInt(read(rdf, 24, 4)));
        //System.out.println("byte rate: " + toInt(read(rdf, 28, 4)));
        //System.out.println("frame size: " + toShort(read(rdf, 32, 2)));
        //System.out.println("bits per sample: " + toShort(read(rdf, 34, 2)));
        rdf.close();
    }
    
    public long getSamples()
    {
    	return this.samples;
    }
    public long getBitrate()
    {
    	return this.bitrate;
    }

}
