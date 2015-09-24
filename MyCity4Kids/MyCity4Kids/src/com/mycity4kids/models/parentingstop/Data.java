package com.mycity4kids.models.parentingstop;

import java.io.Serializable;

public class Data implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1724724275159075170L;
	private Articles Article;
	private Author Author;
	public Articles getArticle() {
		return Article;
	}
	public void setArticle(Articles article) {
		Article = article;
	}
	public Author getAuthor() {
		return Author;
	}
	public void setAuthor(Author author) {
		Author = author;
	}

}
