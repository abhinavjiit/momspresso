package com.mycity4kids.models.parentingstop;

import java.io.Serializable;

public class ParentingArticleListModel implements Serializable {
	private static final long serialVersionUID = -1985293655490675746L; // Implemented Serializable Interface to share the model across the activities using Intent.
	private int articleId;
	private String articleTitle;
	private String articleBody;
	private String articleCreatedDate;
	private int authorId;
	private String authorFirstName;
	private String authorLastName;
	private String aboutAuthor;
	private String authorProfileImg;
	public int getArticleId() {
		return articleId;
	}
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	public String getArticleTitle() {
		return articleTitle;
	}
	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}
	public String getBody() {
		return articleBody;
	}
	public void setBody(String body) {
		this.articleBody = body;
	}
	public String getArticleCreatedDate() {
		return articleCreatedDate;
	}
	public void setArticleCreatedDate(String articleCreatedDate) {
		this.articleCreatedDate = articleCreatedDate;
	}
	public int getAuthorId() {
		return authorId;
	}
	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}
	public String getAuthorFirstName() {
		return authorFirstName;
	}
	public void setAuthorFirstName(String authorFirstName) {
		this.authorFirstName = authorFirstName;
	}
	public String getAuthorLastName() {
		return authorLastName;
	}
	public void setAuthorLastName(String authorLastName) {
		this.authorLastName = authorLastName;
	}
	public String getAboutAuthor() {
		return aboutAuthor;
	}
	public void setAboutAuthor(String aboutAuthor) {
		this.aboutAuthor = aboutAuthor;
	}
	public String getAuthorProfileImg() {
		return authorProfileImg;
	}
	public void setAuthorProfileImg(String authorProfileImg) {
		this.authorProfileImg = authorProfileImg;
	}

}
