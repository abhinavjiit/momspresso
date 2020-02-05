package com.mycity4kids.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kelltontech.utils.StringUtils;
import com.mycity4kids.R;
import com.mycity4kids.models.response.ArticleTagsImagesResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * @author Hemant.Parmar
 */
public class ArticleTagsImagesGridAdapter extends BaseAdapter {

    private ArrayList<ArticleTagsImagesResponse.ArticleTagsImagesData.ArticleTagsImagesResult> tagsImageUrlList;
    private Context mContext;
    private LayoutInflater mInflator;
    private ITagImageSelect iTagImageSelect;

    public ArticleTagsImagesGridAdapter(Context pContext) {
        mContext = pContext;
        mInflator = (LayoutInflater) pContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        iTagImageSelect = (ITagImageSelect) pContext;
    }

    @Override
    public int getCount() {
        return tagsImageUrlList == null ? 0 : tagsImageUrlList.size();
    }

    @Override
    public Object getItem(int position) {
        return tagsImageUrlList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflator.inflate(R.layout.article_tags_image_grid_item, null);
            holder.tagsImageView = (ImageView) view.findViewById(R.id.tagImageView);
            holder.selectedLayerLayout = (LinearLayout) view.findViewById(R.id.selectedLayerLayout);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (!StringUtils.isNullOrEmpty(tagsImageUrlList.get(position).getImageUrl().getClientApp())) {
            Picasso.with(mContext).load(tagsImageUrlList.get(position).getImageUrl().getClientApp()).placeholder(R.drawable.default_article).error(R.drawable.default_article)
                    .fit().into(holder.tagsImageView);
        } else {
            holder.tagsImageView.setBackgroundResource(R.drawable.article_default);
        }

        if (tagsImageUrlList.get(position).isSelected()) {
            holder.selectedLayerLayout.setVisibility(View.VISIBLE);
        } else {
            holder.selectedLayerLayout.setVisibility(View.INVISIBLE);
        }

        holder.tagsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < tagsImageUrlList.size(); i++) {
                    tagsImageUrlList.get(i).setSelected(false);
                }
                tagsImageUrlList.get(position).setSelected(true);
                iTagImageSelect.onTagImageSelected(tagsImageUrlList.get(position).getImageUrl().getClientApp());
                notifyDataSetChanged();
            }
        });

        return view;
    }

    public void setDatalist(ArrayList<ArticleTagsImagesResponse.ArticleTagsImagesData.ArticleTagsImagesResult> datalist) {
        this.tagsImageUrlList = datalist;
    }

    class ViewHolder {
        ImageView tagsImageView;
        LinearLayout selectedLayerLayout;
    }

    public interface ITagImageSelect {
        void onTagImageSelected(String url);
    }

}
