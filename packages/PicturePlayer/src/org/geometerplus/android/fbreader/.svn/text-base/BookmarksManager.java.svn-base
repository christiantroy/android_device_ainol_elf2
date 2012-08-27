package org.geometerplus.android.fbreader;

import java.util.LinkedList;
import java.util.List;

import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.library.Book;
import org.geometerplus.fbreader.library.Bookmark;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.text.view.ZLTextElement;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextWord;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;

public class BookmarksManager {
	private static final int OPEN_ITEM_ID = 0;
	private static final int EDIT_ITEM_ID = 1;
	private static final int DELETE_ITEM_ID = 2;

	List<Bookmark> AllBooksBookmarks;
	private final List<Bookmark> myThisBookBookmarks = new LinkedList<Bookmark>();
    private Bookmark myBookmark = null;


	
	
    public BookmarksManager()
	{
		AllBooksBookmarks = Bookmark.bookmarks();
		final FBReaderApp fbreader = (FBReaderApp)ZLApplication.Instance();

		if (fbreader.Model != null) {
			final long bookId = fbreader.Model.Book.getId();
			for (Bookmark bookmark : AllBooksBookmarks) {
				if (bookmark.getBookId() == bookId) {
					myThisBookBookmarks.add(bookmark);
				}
			}
		}
		if(myThisBookBookmarks.size() >0)
			myBookmark = myThisBookBookmarks.get(0);	
	}
    
    public Bookmark GetBookmark()
    {
    	return myBookmark;
    }
    public void gotoBookmark() {
		myBookmark.onOpen();
		final FBReaderApp fbreader = (FBReaderApp)ZLApplication.Instance();
		final long bookId = myBookmark.getBookId();
		if ((fbreader.Model == null) || (fbreader.Model.Book.getId() != bookId)) {
			final Book book = Book.getById(bookId);
			if (book != null) {
				fbreader.openBook(book, myBookmark);
			} 
		} else {
			fbreader.gotoBookmark(myBookmark);
		}
	}
	
	
    public void addBookmark() {
    	for(Bookmark bmark : myThisBookBookmarks)
    		bmark.delete();
		final FBReaderApp fbreader = (FBReaderApp)ZLApplication.Instance();
		final ZLTextView textView = fbreader.getTextView();
		final ZLTextWordCursor cursor = textView.getStartCursor();

		if (cursor.isNull()) {
			// TODO: implement
			return;
		}

		// TODO: text edit dialog
		final Bookmark bookmark = new Bookmark(
			fbreader.Model.Book,
			createBookmarkText(cursor),
			textView.getModel().getId(),
			cursor
		);
			bookmark.save();
	}
	
	private String createBookmarkText(ZLTextWordCursor cursor) {
		cursor = new ZLTextWordCursor(cursor);

		final StringBuilder builder = new StringBuilder();
		final StringBuilder sentenceBuilder = new StringBuilder();
		final StringBuilder phraseBuilder = new StringBuilder();

		int wordCounter = 0;
		int sentenceCounter = 0;
		int storedWordCounter = 0;
		boolean lineIsNonEmpty = false;
		boolean appendLineBreak = false;
mainLoop:
		while ((wordCounter < 20) && (sentenceCounter < 3)) {
			while (cursor.isEndOfParagraph()) {
				if (!cursor.nextParagraph()) {
					break mainLoop;
				}
				if ((builder.length() > 0) && cursor.getParagraphCursor().isEndOfSection()) {
					break mainLoop;
				}
				if (phraseBuilder.length() > 0) {
					sentenceBuilder.append(phraseBuilder);
					phraseBuilder.delete(0, phraseBuilder.length());
				}
				if (sentenceBuilder.length() > 0) {
					if (appendLineBreak) {
						builder.append("\n");
					}
					builder.append(sentenceBuilder);
					sentenceBuilder.delete(0, sentenceBuilder.length());
					++sentenceCounter;
					storedWordCounter = wordCounter;
				}
				lineIsNonEmpty = false;
				if (builder.length() > 0) {
					appendLineBreak = true;
				}
			}
			final ZLTextElement element = cursor.getElement();
			if (element instanceof ZLTextWord) {
				final ZLTextWord word = (ZLTextWord)element;
				if (lineIsNonEmpty) {
					phraseBuilder.append(" ");
				}
				phraseBuilder.append(word.Data, word.Offset, word.Length);
				++wordCounter;
				lineIsNonEmpty = true;
				switch (word.Data[word.Offset + word.Length - 1]) {
					case ',':
					case ':':
					case ';':
					case ')':
						sentenceBuilder.append(phraseBuilder);
						phraseBuilder.delete(0, phraseBuilder.length());
						break;
					case '.':
					case '!':
					case '?':
						++sentenceCounter;
						if (appendLineBreak) {
							builder.append("\n");
							appendLineBreak = false;
						}
						sentenceBuilder.append(phraseBuilder);
						phraseBuilder.delete(0, phraseBuilder.length());
						builder.append(sentenceBuilder);
						sentenceBuilder.delete(0, sentenceBuilder.length());
						storedWordCounter = wordCounter;
						break;
				}
			}
			cursor.nextWord();
		}
		if (storedWordCounter < 4) {
			if (sentenceBuilder.length() == 0) {
				sentenceBuilder.append(phraseBuilder);
			}
			if (appendLineBreak) {
				builder.append("\n");
			}
			builder.append(sentenceBuilder);
		}
		return builder.toString();
	}
}




