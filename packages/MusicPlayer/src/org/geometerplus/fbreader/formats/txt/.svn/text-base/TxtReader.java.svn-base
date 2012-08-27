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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import org.geometerplus.fbreader.bookmodel.*;
import org.geometerplus.zlibrary.core.xml.*;
import org.geometerplus.zlibrary.text.model.ZLTextParagraph;

public final class TxtReader extends BookReader implements ZLXMLReader {
    private int myBufferSize = 65536;

    public TxtReader(BookModel model) {
        super(model);
    }

    boolean readBook(/*ZLFile file*/) {
        InputStream inputstream = null;
        try {
            inputstream = Model.Book.File.getInputStream();
        } catch (IOException e) {
        }
        if (inputstream == null) {
            return false;
        }
        startDocumentHandler();
        beginParagraph(ZLTextParagraph.Kind.TEXT_PARAGRAPH);
        InputStreamReader streamReader = null;
        try {
            streamReader = new InputStreamReader(inputstream, Model.Book.getEncoding());
            char[] buffer = new char[myBufferSize];
            while (true) {
                int count = streamReader.read(buffer);
//                System.out.println(count);
                if (count <= 0) {
                    streamReader.close();
                    break;
                }
                int start = 0;
                for (int i = 0; i < count; i++) {
                    if (buffer[i] == '\n') {
                        if (start != i) {
                            characterDataHandler(buffer, start, i - start);
                            endParagraph();
                            beginParagraph(ZLTextParagraph.Kind.TEXT_PARAGRAPH);
                        }
                        start = i + 1;
                    } else if (buffer[i] == '\r') {
                        continue;
                    } else if (buffer[i] == ' ' || buffer[i] == '\t') {
                        buffer[i] = ' ';
                    } else {
                    }
                }
                if (start != count) {
                    characterDataHandlerFinal(buffer, start, count - start);
                }
            }
            endParagraph();
            endDocumentHandler();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            try {
                inputstream.close();
            } catch (IOException e) {
            }

        }
        return true;
    }

    public void startDocumentHandler() {
        setMainTextModel();
    }

    public void endDocumentHandler() {
        unsetCurrentTextModel();
    }

    public boolean dontCacheAttributeValues() {
        return true;
    }

    public void characterDataHandler(char[] ch, int start, int length) {
        if (length == 0) {
            return;
        }
        addData(ch, start, length, false);
    }

    public void characterDataHandlerFinal(char[] ch, int start, int length) {
        if (length == 0) {
            return;
        }
        addData(ch, start, length, true);
    }

    public boolean endElementHandler(String tagName) {
        return false;
    }

    public boolean startElementHandler(String tagName, ZLStringMap attributes) {
        return false;
    }

    private static ArrayList ourExternalDTDs = new ArrayList();

    public ArrayList externalDTDs() {
        if (ourExternalDTDs.isEmpty()) {
            ourExternalDTDs.add("data/formats/fb2/FBReaderVersion.ent");
        }
        return ourExternalDTDs;
    }

    ////Override
    //public void namespaceListChangedHandler(HashMap namespaces) {
    //	return ;
    //}

    //Override
    public boolean processNamespaces() {
        return false;
    }

	//Override
	public void addExternalEntities(HashMap<String, char[]> entityMap) {
		// TODO Auto-generated method stub
		
	}

	//Override
	public void namespaceMapChangedHandler(HashMap<String, String> namespaces) {
		// TODO Auto-generated method stub
		
	}
	
}
