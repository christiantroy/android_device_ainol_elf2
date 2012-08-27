/*
 * Copyright (C) 2007-2009 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.fbreader.formats.txt;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.formats.FormatPlugin;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.zlibrary.core.filesystem.ZLFile;
import org.geometerplus.zlibrary.core.image.ZLImage;

public class TxtPlugin extends FormatPlugin {
	//Override
	public boolean acceptsFile(ZLFile file) {
		return "txt".equalsIgnoreCase(file.getExtension());
	}
	
	public boolean readDescription(BookModel model) {
	    int enc = new SinoDetect().detectEncoding(new File(model.Book.File.getPath()));
//        System.out.println("encoding is ======"+enc);
	    InputStream stream = null;
	    try {
            stream = model.Book.File.getInputStream();
            if (stream.available() <= 0) {
                return false;
            }
            //Here, we are supposed to set language and encoder
            model.Book.setEncoding(SinoDetect.nicename[enc]);
            detectEncodingAndLanguage(model.Book, stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // ignore
            }
        }
        /*
	    if (description.getEncoding() == null || description.getEncoding().equals("")) {
	        return false;
	    }
        */
	    return true;
	}
	
	//Override
	public boolean readModel(BookModel model) {
		File mfile = new File(model.Book.File.getPath());
		if(mfile.length() == 0)
			return false;
		readDescription(model);
		return new TxtReader(model).readBook();
	}

	//Override
	public ZLImage readCover(Book book) {
		// TODO Auto-generated method stub
		return null;
	}

	//Override
	public boolean readMetaInfo(Book book) {
		// TODO Auto-generated method stub
		return true;
	}
}
