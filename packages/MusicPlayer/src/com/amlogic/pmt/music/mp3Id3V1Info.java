package com.amlogic.pmt.music;

import java.io.UnsupportedEncodingException;

public class mp3Id3V1Info {

	private final String TAG = "TAG"; //1-3
	private String songName; //4-33
	private String artist; //34-63
	private String album; // 61-93
	private String year; //94-97
	private String comment; //98-125
	private byte r1, r2, r3; //126 127 128
	private boolean valid; //
	public transient String fileName;

	public mp3Id3V1Info(byte[] data) throws UnsupportedEncodingException {
		if (data.length != 128) {
		throw new RuntimeException("data length error:" + data.length);
		}
		String tag = new String(data, 0, 3);

		if (tag.equalsIgnoreCase("TAG")) {
		valid = true;
		songName = new String(data, 3, 30,"GBK").trim();
		artist = new String(data, 33, 30,"GBK").trim();
		album = new String(data, 63, 30,"GBK").trim();
		year = new String(data, 93, 4,"GBK").trim();
		comment = new String(data, 97, 28,"GBK").trim();
		r1 = data[125];
		r2 = data[126];
		r3 = data[127];
		} else {
		valid = false;
		}
	}

	public boolean isValid() {
	return valid;
	}

	public byte[] getBytes() {
		byte[] data = new byte[128];
		System.arraycopy(TAG.getBytes(), 0, data, 0, 3);
		byte[] temp = songName.getBytes();
		System.arraycopy(temp, 0, data, 3, temp.length > 30 ? 30 : temp.length);
		temp = artist.getBytes();
		System
		.arraycopy(temp, 0, data, 33, temp.length > 30 ? 30
		: temp.length);
		temp = album.getBytes();
		System
		.arraycopy(temp, 0, data, 63, temp.length > 30 ? 30
		: temp.length);
		temp = year.getBytes();
		System.arraycopy(temp, 0, data, 93, temp.length > 4 ? 4 : temp.length);
		temp = comment.getBytes();
		System
		.arraycopy(temp, 0, data, 97, temp.length > 28 ? 28
		: temp.length);
		data[125] = r1;
		data[126] = r2;
		data[127] = r3;
		return data;
	}

	public String getArtist() {
	return artist;
	}

	public void setArtist(String authorName) {
	this.artist = authorName;
	}

	public String getComment() {
	return comment;
	}

	public void setComment(String comment) {
	this.comment = comment;
	}

	public byte getR1() {
	return r1;
	}

	public void setR1(byte r1) {
	this.r1 = r1;
	}

	public byte getR2() {
	return r2;
	}

	public void setR2(byte r2) {
	this.r2 = r2;
	}

	public byte getR3() {
	return r3;
	}

	public void setR3(byte r3) {
	this.r3 = r3;
	}

	public String getSongName() {
	return songName;
	}

	public void setSongName(String songName) {
	if (songName == null) {
	throw new NullPointerException("song name null!");
	}
	valid = true;
	this.songName = songName;
	}

	public String getAlbum() {
	return album;
	}

	public void setAlbum(String specialName) {
	this.album = specialName;
	}

	public String getYear() {
	return year;
	}

	public void setYear(String year) {
	this.year = year;
	}

	}
