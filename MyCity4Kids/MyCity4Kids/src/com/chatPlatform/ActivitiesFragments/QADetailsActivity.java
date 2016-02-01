package com.chatPlatform.ActivitiesFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kelltontech.network.Response;
import com.kelltontech.ui.BaseActivity;
import com.kelltontech.utils.DateTimeUtils;
import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.constants.AppConstants;
import com.mycity4kids.controller.ArticleBlogDetailsController;
import com.mycity4kids.models.parentingdetails.CommentsData;
import com.mycity4kids.models.parentingdetails.ParentingDetailResponse;
import com.mycity4kids.models.parentingdetails.ParentingDetailsData;

/**
 * Created by hemant on 18/1/16.
 */
public class QADetailsActivity extends BaseActivity {

    private Toolbar mToolbar;
    ParentingDetailsData detailData;
    int i = 1;
    private int leftMargin = 0;
    private int isComingAfterReply = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qa_details_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Q&A");
        ArticleBlogDetailsController _controller = new ArticleBlogDetailsController(this, this);
        _controller.getData(AppConstants.ARTICLES_DETAILS_REQUEST, "9221");
        LinearLayout commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));

//        addComment(null, null, commentLayout);
    }

    private class ViewHolder {
        private ImageView networkImg;
        private ImageView localImg;
        private TextView commentName;
        private TextView commentDescription;
        private TextView dateTxt;
        private RelativeLayout mRelativeContainer;
        private TextView replyTxt;
        private View dividerLine;
        private CardView innerCommentView;

    }

    public void setCommentData(ViewHolder holder, CommentsData commentList, LinearLayout commentLayout) {
        if (holder != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.custom_comment_cell, null);
            holder.networkImg = (ImageView) view.findViewById(R.id.network_img);
//            holder.localImg = (ImageView) view.findViewById(R.id.local_img);
            holder.commentName = (TextView) view.findViewById(R.id.txvCommentTitle);
            holder.commentDescription = (TextView) view.findViewById(R.id.txvCommentDescription);
            holder.mRelativeContainer = (RelativeLayout) view.findViewById(R.id.relativeMainContainer);
            holder.dateTxt = (TextView) view.findViewById(R.id.txvDate);
            holder.replyTxt = (TextView) view.findViewById(R.id.txvReply);
            holder.dividerLine = view.findViewById(R.id.dividerLine);
            holder.innerCommentView = (CardView) view.findViewById(R.id.inner_comment_view);
            holder.replyTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.replyTxt.setTag(commentList);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(leftMargin, 0, 0, 0);
            leftMargin = leftMargin + 30;
//            view.setLayoutParams(params);
            holder.mRelativeContainer.setLayoutParams(params);

            if (isComingAfterReply == 1) {
                --isComingAfterReply;
                View dividerView = new View(this);
                dividerView.setBackgroundResource(Color.TRANSPARENT);
//                dividerView.setBackgroundColor(Color.parseColor("#e3eaff"));
                commentLayout.addView(dividerView, LinearLayout.LayoutParams.MATCH_PARENT, 10);
            }

            commentLayout.addView(view);

            if (commentList.getReplies() != null && commentList.getReplies().size() > 0) {

                for (CommentsData replyList : commentList.getReplies()) {
                    /*	i++;
                    if(i==commentList.getReplies().size()){
						isInsertWithReply=true;
					}else{
						isInsertWithReply=false;
					}*/
                    setCommentData(holder, replyList, commentLayout);
                }
            }
        }


    }

    @Override
    protected void updateUi(Response response) {
        try {
            if (response == null) {
                removeProgressDialog();
                showToast("Something went wrong from server");
                return;
            }
            ParentingDetailResponse responseData = (ParentingDetailResponse) response.getResponseObject();
            if (responseData.getResponseCode() == 200) {
                ViewHolder holder = null;
                detailData = responseData.getResult().getData();
                if (detailData.getComments() != null) {
                    holder = new ViewHolder();
                    ((TextView) findViewById(R.id.txvComment)).setText("Comments " + "(" + detailData.getComments().size() + ")");
                    LinearLayout commentLayout = ((LinearLayout) findViewById(R.id.commnetLout));
                    if (commentLayout.getChildCount() > 0) {
                        commentLayout.removeAllViews();
                    }
                    //	LayoutInflater inflater=LayoutInflater.from(this);
                    for (CommentsData commentList : detailData.getComments()) {
                        leftMargin = 0;

                        ++isComingAfterReply;
                        //isInsertWithReply=false;


                        setCommentData(holder, commentList, commentLayout);
                    }
                }
            }
        } catch (Exception c) {

        }
    }
}
