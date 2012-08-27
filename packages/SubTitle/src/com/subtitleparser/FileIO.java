package com.subtitleparser;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

/**
* General File I/O methods.
*
* @author
*/
public class FileIO {
	/**
	* Fetch the entire contents of a text file, and return it in a String.
	* This style of implementation does not throw Exceptions to the caller.
	*
	* @param aFile is a file which already exists and can be read.
	* @param enc text encode in aFile 
	*/
	static private String getContents(File aFile,String enc) throws IOException{
		//...checks on aFile are elided
		StringBuffer contents = new StringBuffer();
		
		//declared here only to make visible to finally clause
		BufferedReader input = null;
		try {
			//use buffering
			//this implementation reads one line at a time
			input = new BufferedReader( new InputStreamReader(new FileInputStream(aFile),enc));
			String line = null; //not declared within while loop
			while (( line = input.readLine()) != null){
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		}
		catch (FileNotFoundException ex) {
			throw ex;
		}
		catch (IOException ex){
			throw ex;
		}
		finally {
			try {
				if (input!= null) {
					//flush and close both "input" and its underlying FileReader
					input.close();
				}
			}
			catch (IOException ex) {
				throw ex;
			}
		}

		return contents.toString();
	}
	
	public static Subtitle.SUBTYPE dectFileType(String filePath,String encoding)
	{
		BufferedReader input = null;
		int testMaxLines =60;
		Pattern MICRODVD_Pattern = Pattern.compile("\\{\\d+\\}\\{\\d+\\}");
		Pattern MICRODVD_Pattern_2 = Pattern.compile("\\{\\d+\\}\\{\\}");
		Pattern SUB_MPL2_Pattern = Pattern.compile("\\[\\d+\\]\\[\\d+\\]");
		Pattern SUBRIP_Pattern = Pattern.compile("\\d+:\\d+:\\d+.\\d+,\\d+:\\d+:\\d+.\\d+");
		Pattern SUBVIEWER_Pattern = Pattern.compile("\\d+:\\d+:\\d+[\\,\\.:]\\d+ ?--> ?\\d+:\\d+:\\d+[\\,\\.:]\\d+");
		Pattern SUBVIEWER2_Pattern = Pattern.compile("\\{T \\d+:\\d+:\\d+:\\d+ ");
		Pattern SAMI_Pattern = Pattern.compile("<SAMI>");
		Pattern JACOSUB_Pattern = Pattern.compile("\\d+:\\d+:\\d+.\\d+ \\d+:\\d+:\\d+.\\d+");
		Pattern JACOSUB_Pattern_2 = Pattern.compile("@\\d+ @\\d+");
		Pattern VPLAYER_Pattern = Pattern.compile("\\d+:\\d+:\\d+[: ]");
		Pattern PJS_Pattern = Pattern.compile("\\d+\\d+,\"");
		Pattern MPSUB_Pattern = Pattern.compile("FORMAT=\\d+");
		Pattern MPSUB_Pattern_2 = Pattern.compile("FORMAT=TIME");
		Pattern AQTITLE_Pattern = Pattern.compile("-->>");
		Pattern SUBRIP9_Pattern = Pattern.compile("\\[\\d+:\\d+:\\d+\\]");

		Matcher matcher=null;
		try {
			//use buffering
			//this implementation reads one line at a time
//			input = new BufferedReader( new FileReader(filePath));
			input = new BufferedReader( new InputStreamReader(new FileInputStream(new File(filePath)),encoding),1024);
			String line = null; //not declared within while loop
			try {
				while (( line = input.readLine()) != null&&testMaxLines>0)
				{
					Log.v("dectFileType"," -----new line--------"+(60-testMaxLines)+"  "+line);
					if(line.length()>3000)
					{
						return Subtitle.SUBTYPE.SUB_INVALID;
					}

					testMaxLines--;
					if(line==null)
					{
						return Subtitle.SUBTYPE.SUB_INVALID;
					}
					if(line.startsWith("Dialogue: "))
					{
						return Subtitle.SUBTYPE.SUB_SSA;	
					}
					matcher=MICRODVD_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_MICRODVD;
					}

					matcher=MICRODVD_Pattern_2.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_MICRODVD ;
					}
					matcher=SUB_MPL2_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_MPL2;
					}
					matcher=SUBRIP_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_SUBRIP;
					}
					matcher=SUBVIEWER_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_SUBVIEWER;
					}
					matcher=SUBVIEWER2_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_SUBVIEWER2;
					}
					matcher=SAMI_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_SAMI;
					}
					matcher=JACOSUB_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_JACOSUB;
					}
					matcher=JACOSUB_Pattern_2.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_JACOSUB;
					}
					matcher=VPLAYER_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_VPLAYER;
					}
					if(line.startsWith("<"))
					{
						return Subtitle.SUBTYPE.SUB_RT;
					}
					matcher=PJS_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_PJS;					
					}
					matcher=MPSUB_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_MPSUB;					
					}
					matcher=MPSUB_Pattern_2.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_MPSUB;					
					}
					matcher=AQTITLE_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_AQTITLE;					
					}
					matcher=SUBRIP9_Pattern.matcher(line);
					if(matcher.find())
					{
						return Subtitle.SUBTYPE.SUB_SUBRIP09;					
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Subtitle.SUBTYPE.SUB_INVALID;
	}
	
	/**
	* Fetch the entire contents of a text file, and return it in a String.
	*
	* @param filePath is a file which already exists and can be read.
	* @param enc
	*/
	static public String file2string(String filePath, String enc) throws IOException{
	    Log.i("FileIO", "filename:"+filePath );
		File f=new File(filePath);
		if (!f.exists()) throw new FileNotFoundException(filePath+" doesn't exist.");
		if (f.isDirectory()) throw new FileNotFoundException(filePath+" is a directory.");

		return getContents(f,enc);
	}
	
	/**
	* Write a String into a text file.
	*
	* @param text the text to write in the file;
	* @param filePath is a file which already exists and can be read.
	* @param enc
	*/
	static public void string2file(String text, String filePath, String enc) throws IOException{
		String s="";
		File f=new File(filePath);
		f.createNewFile();
		int lineCount = 0;
		
		try {
			BufferedReader in = new BufferedReader(new StringReader(text));
			PrintWriter out = new PrintWriter(new BufferedWriter( new OutputStreamWriter(new FileOutputStream(filePath),enc)));
			
			while((s = in.readLine()) != null )
			{
				out.println(s);
				lineCount++;
			}
			out.close();
		} catch(IOException e) {
			throw new IOException("Problem of writing at line "+lineCount+": "+e);
		}				
	}
} 
