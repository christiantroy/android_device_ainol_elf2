package com.farcore.playerservice;

import android.os.Parcel;
import android.os.Parcelable;

public class DivxInfo{
  private String RegCode;
  private int CheckValue;

  public static final Parcelable.Creator<DivxInfo> CREATOR = new

      Parcelable.Creator<DivxInfo>() {
           public DivxInfo createFromParcel(Parcel in) {
              return new DivxInfo();
           }
           public DivxInfo[] newArray(int size) {
               return null;
           }

      };
      void writeToParcel(Parcel reply, int parcelableWriteReturnValue) {
        
            // TODO Auto-generated method stub
            //
            //      
            //
      }

  DivxInfo(){
    RegCode = "";
    CheckValue = 0;
  }

  public String GetRegistrationString(){
    return RegCode;
  }

  public int GetCheckValue(){
    return CheckValue;
  }
};

