package com.mycity4kids.models.parentingfilter;

import java.util.ArrayList;

public class ArticleBlogFilterData {
	/**
	 * for article filter :- author,tags,topic 
	 * for blogs filter :- blogs,tags,bloggers.
	 * Bloggers & Filter author contain same data so i will use same classe 
	 * Deepanker Chaudhary
	 */
	private ArrayList<FilterAuthors> Authors;
	private ArrayList<FilterTags> Tags;
	private ArrayList<FilterTopics> Topics;
	private ArrayList<FilterBlogs> Blogs;
	private ArrayList<FilterAuthors> Bloggers;


	public ArrayList<FilterAuthors> getAuthors() {
		return Authors;
	}
	public void setAuthors(ArrayList<FilterAuthors> authors) {
		Authors = authors;
	}
	public ArrayList<FilterTags> getTags() {
		return Tags;
	}
	public void setTags(ArrayList<FilterTags> tags) {
		Tags = tags;
	}
	public ArrayList<FilterTopics> getTopics() {
		return Topics;
	}
	public void setTopics(ArrayList<FilterTopics> topics) {
		Topics = topics;
	}
	public ArrayList<FilterBlogs> getBlogs() {
		return Blogs;
	}
	public void setBlogs(ArrayList<FilterBlogs> blogs) {
		Blogs = blogs;
	}
	public ArrayList<FilterAuthors> getBloggers() {
		return Bloggers;
	}
	public void setBloggers(ArrayList<FilterAuthors> bloggers) {
		Bloggers = bloggers;
	}
}
