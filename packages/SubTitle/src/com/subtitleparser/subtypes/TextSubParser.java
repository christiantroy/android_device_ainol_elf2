package com.subtitleparser.subtypes;

import android.util.Log;

import com.subtitleparser.MalformedSubException;
import com.subtitleparser.SubData;
import com.subtitleparser.SubtitleApi;
import com.subtitleparser.SubtitleFile;
import com.subtitleparser.SubtitleLine;
import com.subtitleparser.SubtitleParser;
import com.subtitleparser.Subtitle;


class TextSubApi extends SubtitleApi
{
	 private SubtitleFile SubFile =null;
	 private SubtitleLine cur=null;
	 private String st=null;
	 public TextSubApi(SubtitleFile sf){
		 SubFile=sf;
	 }
	 public void closeSubtitle( )
	 {		 
	 }
	 public Subtitle.SUBTYPE type()
	 {
		 return Subtitle.SUBTYPE.SUB_COMMONTXT;
	 }
	 public SubData getdata(int millisec )
	 {
		 try {
			 cur = SubFile.curSubtitle();
			 if (millisec >= cur.getBegin().getMilValue()
						&& millisec <= cur.getEnd().getMilValue()) {
					st=SubFile.curSubtitle().getText();
			} else {
				SubFile.matchSubtitle(millisec);
				cur = SubFile.curSubtitle();
				if (millisec >= cur.getBegin().getMilValue()
						&& millisec <= cur.getEnd().getMilValue()) {
					st=SubFile.curSubtitle().getText();
				}else if(millisec<cur.getBegin().getMilValue())
				{
					st="";
				}
				else
				{
					SubFile.toNextSubtitle();
					st="";
				}
			}
			if(st.compareTo("")!=0)
				return new SubData(st,cur.getBegin().getMilValue(),cur.getEnd().getMilValue());
			else
				return new SubData(st,millisec,millisec+30);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	 }

}




/**
* a .SRT subtitle parser.
*
* @author
*/
public class TextSubParser implements SubtitleParser {

	public SubtitleApi parse(String filename,String encode) throws MalformedSubException{

		SubtitleFile file=Subtitle.parseSubtitleFileByJni(filename, encode);
		if(file==null)
		{
		    Log.i("TextSubParser", "------------err-----------" );

			throw new MalformedSubException("text sub parser return NULL!");
		}else
		{	
			return new TextSubApi(file);

		}
	};
	public SubtitleApi parse(String inputstring,int index) throws MalformedSubException{
		return null;
};

}
