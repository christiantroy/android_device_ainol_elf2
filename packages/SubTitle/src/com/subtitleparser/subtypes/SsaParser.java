package com.subtitleparser.subtypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.subtitleparser.MalformedSubException;
import com.subtitleparser.SubData;
import com.subtitleparser.Subtitle;
import com.subtitleparser.SubtitleFile;
import com.subtitleparser.SubtitleLine;
import com.subtitleparser.SubtitleParser;
import com.subtitleparser.SubtitleTime;
import com.subtitleparser.SubtitleApi;


class SsaApi extends SubtitleApi
{
	private SubtitleFile SubFile =null;
	 private SubtitleLine cur=null;
	 private String st=null;
	 public SsaApi(SubtitleFile sf){
		 SubFile=sf;
	 }
	 public void closeSubtitle( ){}
	 public Subtitle.SUBTYPE type()
	 {
		 return Subtitle.SUBTYPE.SUB_SSA;
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
			{
				st=st.replaceAll( "\\{(.*?)\\}","" );
				st=st.replaceAll( "\\\\N","\\\n" );
				return new SubData(st,cur.getBegin().getMilValue(),cur.getEnd().getMilValue());
			}
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
* a .SUB subtitle parser.
*
* @author jeff.yang
*/
public class SsaParser implements SubtitleParser{
	
	public SubtitleApi parse(String inputString,int index) throws MalformedSubException{
		try{
			String n="\\"+System.getProperty("line.separator");
			String tmpText="";
			SubtitleFile sf=new SubtitleFile();
			SubtitleLine sl=null;
			
			//SSA regexp
			Pattern p = Pattern.compile(
					"Dialogue:[^,]*,\\s*"+"(\\d):(\\d\\d):(\\d\\d).(\\d\\d)\\s*,\\s*"
					+"(\\d):(\\d\\d):(\\d\\d).(\\d\\d)"+
					"[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,[^,]*,"+
					"(.*?)"+n
			);

			Matcher m = p.matcher(inputString);

			int occ=0;
			while(m.find()){

				occ++;
//				String tmp=m.group(9).replaceAll("\\{.*?\\}", "");

				sl=new SubtitleLine(occ,
						new SubtitleTime(Integer.parseInt(m.group(1)), //start time
								Integer.parseInt(m.group(2)),
								Integer.parseInt(m.group(3)),
								Integer.parseInt(m.group(4))),
						new SubtitleTime(Integer.parseInt(m.group(5)), //end time
								Integer.parseInt(m.group(6)),
								Integer.parseInt(m.group(7)),
								Integer.parseInt(m.group(8))),
								m.group(9) //text
				);
				tmpText="";
				sf.add(sl);
			}
			Log.i("SsaParser", "find"+sf.size());
			return new SsaApi(sf);
		}catch(Exception e)
		{
			Log.i("SsaParser", "------------!!!!!!!parse file err!!!!!!!!");
		    throw new MalformedSubException(e.toString());
		}
	};
	public SubtitleApi parse(String inputString,String st2) throws MalformedSubException{
		return null;
	};
	
}