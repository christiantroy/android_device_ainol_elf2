package com.amlogic.pmt.music;

import java.io.IOException;
import java.io.RandomAccessFile;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class mp3Id3V2Info 
{
	byte[] pictureBuf =null;
	
	private int toUnsignedInt(byte[] b) {
        return (((b[0]&0xff) << 24) + ((b[1]&0xff) << 16) + ((b[2]&0xff) << 8) + (b[3]&0xff));
    }
	
	private int totalTagSize(byte[] b)
	{
		int total_size;
		total_size = (b[0]&0x7F)*0x200000+(b[1]&0x7F)*0x4000+(b[2]&0x7F)*0x80+(b[3]&0x7F);//byte is signed default,so & process it
		return total_size;
	}
	
    private byte[] read(RandomAccessFile rdf, int pos, int length) throws IOException {
        rdf.seek(pos);
        byte result[] = new byte[length];
        rdf.read(result,0,length);
        return result;
    }

    private int getAPICOffset(RandomAccessFile rdf,int position) throws IOException
    {
    	int i = 0;
    	byte[] tmp = read(rdf,position+10,64);
    	if(tmp[i] == 0)
    		i++;
    	while(tmp[i] != 0)
    		i++;
    	i++;
    	while(tmp[i] != 0)
    		i++;
    	i++;
    	return i;
    }
    
    public mp3Id3V2Info(RandomAccessFile rdf) throws IOException {

        String id3 = new String(read(rdf,0,3));
        if(id3.equals("ID3") == false)
        	return;
        int tagSize = totalTagSize(read(rdf,6,4));
        int position = 10;
    	while (position < (tagSize + 10))
    	{
    		String tag = new String(read(rdf,position,4));
    		int size = toUnsignedInt(read(rdf,position+4,4));
    		if(tag.equals("APIC"))
    		{
    			int offset = getAPICOffset(rdf,position);
    			this.pictureBuf = read(rdf,position+10+offset,size-offset);
    			break;
    		}
    		position = position+10+size;
    	}

        rdf.close();
    }
    public Bitmap getPicture()
    {
    	if(this.pictureBuf != null && this.pictureBuf.length != 0)
    	{
    		Bitmap bm = BitmapFactory.decodeByteArray(this.pictureBuf, 0, this.pictureBuf.length);
    		return bm;
    	}
    	else
    		return null;

    }
    
/*    
    public void writeFile()
    {
    	File file = new File("/mnt/sdcard/aaa.jpg");  
    	try {  
    		OutputStream out = new FileOutputStream(file);  
    		out.write(this.pictureBuf);  
    		out.close(); 
    	} catch (Exception e) {}
    }
*/    
   
}