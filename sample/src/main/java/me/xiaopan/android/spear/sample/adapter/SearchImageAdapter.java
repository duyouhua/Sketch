package me.xiaopan.android.spear.sample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.SpearImageView;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.net.request.StarImageRequest;
import me.xiaopan.android.spear.sample.util.Settings;

/**
 * 搜索图片适配器
 */
public class SearchImageAdapter extends BaseAdapter {
    private static final int ITEM_TYPE_ITEM = 0;
    private static final int ITEM_TYPE_LOAD_MORE_FOOTER = 1;
    private static final int ITEM_TYPE_HEADER = 2;
    private int imageSize = -1;
    private int headWidth;
    private int column = 3;
    private int marginBorder;
    private int margin;
    private Context context;
    private List<StarImageRequest.Image> imageList;
    private OnLoadMoreListener onLoadMoreListener;
    private String headImageUrl;
    private View.OnClickListener itemClickListener;
    private List<String> imageUrlList;
    private LoadMoreFooterViewHolder loadMoreFooterViewHolder;
    private Settings settings;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public SearchImageAdapter(Context context, String headImageUrl, List<StarImageRequest.Image> imageList, final OnItemClickListener onItemClickListener){
        this.context = context;
        this.settings = Settings.with(context);
        this.headImageUrl = headImageUrl;
        this.imageList = imageList;
        this.imageUrlList = new ArrayList<>(imageList.size());
        for(StarImageRequest.Image image : imageList){
            imageUrlList.add(image.getSourceUrl());
        }

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        margin = (int) context.getResources().getDimension(R.dimen.home_category_margin);
        marginBorder = (int) context.getResources().getDimension(R.dimen.home_category_margin_border);
        headWidth = screenWidth - (marginBorder*2);
        int maxSize = screenWidth - (marginBorder * 4);
        imageSize = maxSize/column;

        itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    int position = (Integer) v.getTag();
                    if(position < SearchImageAdapter.this.imageList.size()){
                        StarImageRequest.Image image = SearchImageAdapter.this.imageList.get(position);
                        onItemClickListener.onItemClick(position, image);
                    }
                }
            }
        };
    }

    @Override
    public int getCount() {
        if(imageList == null){
            return 0;
        }
        return (imageList.size()%column!=0?(imageList.size()/column)+1:(imageList.size()/column))   // ITEM行数
                +(headImageUrl !=null?1:0) // 加上头
                +(onLoadMoreListener!=null?1:0); // 加上尾巴
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if(headImageUrl != null && position == 0){
            return ITEM_TYPE_HEADER;
        }else if(onLoadMoreListener != null && position == getCount()-1){
            return ITEM_TYPE_LOAD_MORE_FOOTER;
        }else{
            return ITEM_TYPE_ITEM;
        }
    }

    public int getDataSize(){
        return imageList.size();
    }

    public void append(List<StarImageRequest.Image> imageList) {
        this.imageList.addAll(imageList);
        for(StarImageRequest.Image image : imageList){
            imageUrlList.add(image.getSourceUrl());
        }
    }

    public List<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = onCreateViewHolder(parent, getItemViewType(position));
        }

        onBindViewHolder(convertView.getTag(), position);
        return convertView;
    }

    public View onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch(viewType){
            case ITEM_TYPE_HEADER :
                View headItemView = LayoutInflater.from(context).inflate(R.layout.list_item_star_image_header, viewGroup, false);
                HeadViewHolder headViewHolder = new HeadViewHolder(headItemView);
                headViewHolder.spearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_3_2);
                headViewHolder.spearImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ViewGroup.LayoutParams headParams = headViewHolder.spearImageView.getLayoutParams();
                headParams.width = headWidth;
                headParams.height = (int) (headWidth / 3.2f);
                headViewHolder.spearImageView.setLayoutParams(headParams);
                headViewHolder.spearImageView.setShowClickRipple(true);
                headItemView.setTag(headViewHolder);
                return headItemView;
            case ITEM_TYPE_ITEM :
                View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_star_image, viewGroup, false);
                ItemViewHolder itemViewHolder = new ItemViewHolder(itemView);
                ViewGroup.LayoutParams itemParams = itemViewHolder.oneSpearImageView.getLayoutParams();
                itemParams.width = imageSize;
                itemParams.height = imageSize;
                itemViewHolder.oneSpearImageView.setLayoutParams(itemParams);

                itemParams = itemViewHolder.twoSpearImageView.getLayoutParams();
                itemParams.width = imageSize;
                itemParams.height = imageSize;
                itemViewHolder.twoSpearImageView.setLayoutParams(itemParams);

                itemParams = itemViewHolder.threeSpearImageView.getLayoutParams();
                itemParams.width = imageSize;
                itemParams.height = imageSize;
                itemViewHolder.threeSpearImageView.setLayoutParams(itemParams);

                itemViewHolder.oneSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_1);
                itemViewHolder.twoSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_1);
                itemViewHolder.threeSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_1);

                itemViewHolder.oneSpearImageView.setShowClickRipple(true);
                itemViewHolder.twoSpearImageView.setShowClickRipple(true);
                itemViewHolder.threeSpearImageView.setShowClickRipple(true);

                itemViewHolder.oneSpearImageView.setOnClickListener(itemClickListener);
                itemViewHolder.twoSpearImageView.setOnClickListener(itemClickListener);
                itemViewHolder.threeSpearImageView.setOnClickListener(itemClickListener);
                itemView.setTag(itemViewHolder);
                return itemView;
            case ITEM_TYPE_LOAD_MORE_FOOTER:
                View footerItemView = LayoutInflater.from(context).inflate(R.layout.list_footer_load_more, viewGroup, false);
                loadMoreFooterViewHolder = new LoadMoreFooterViewHolder(footerItemView);
                footerItemView.setTag(loadMoreFooterViewHolder);
                return footerItemView;
            default:
                return null;
        }
    }

    public void onBindViewHolder(Object viewHolder, int position) {
        if(viewHolder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
            if(headImageUrl != null){
                position -= 1;
            }

            int topMargin;
            int bottomMargin;
            if(position == 0){
                topMargin = headImageUrl !=null?margin:marginBorder;
                bottomMargin = margin;
            }else if(position == getCount()-1){
                topMargin = margin;
                bottomMargin = marginBorder;
            }else{
                topMargin = margin;
                bottomMargin = margin;
            }

            itemViewHolder.oneSpearImageView.setShowDownloadProgress(settings.isShowImageDownloadProgress());
            itemViewHolder.twoSpearImageView.setShowDownloadProgress(settings.isShowImageDownloadProgress());
            itemViewHolder.threeSpearImageView.setShowDownloadProgress(settings.isShowImageDownloadProgress());

            itemViewHolder.oneSpearImageView.setShowFromFlag(settings.isShowImageFromFlag());
            itemViewHolder.twoSpearImageView.setShowFromFlag(settings.isShowImageFromFlag());
            itemViewHolder.threeSpearImageView.setShowFromFlag(settings.isShowImageFromFlag());

            itemViewHolder.oneSpearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());
            itemViewHolder.twoSpearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());
            itemViewHolder.threeSpearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());

            itemViewHolder.oneSpearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());
            itemViewHolder.twoSpearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());
            itemViewHolder.threeSpearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemViewHolder.oneSpearImageView.getLayoutParams();
            params.topMargin = topMargin;
            params.bottomMargin = bottomMargin;
            itemViewHolder.oneSpearImageView.setLayoutParams(params);

            params = (ViewGroup.MarginLayoutParams) itemViewHolder.twoSpearImageView.getLayoutParams();
            params.topMargin = topMargin;
            params.bottomMargin = bottomMargin;
            itemViewHolder.twoSpearImageView.setLayoutParams(params);

            params = (ViewGroup.MarginLayoutParams) itemViewHolder.threeSpearImageView.getLayoutParams();
            params.topMargin = topMargin;
            params.bottomMargin = bottomMargin;
            itemViewHolder.threeSpearImageView.setLayoutParams(params);

            int oneReadPosition = (position*column);
            bind(itemViewHolder.oneSpearImageView, oneReadPosition<imageList.size()?imageList.get(oneReadPosition):null, oneReadPosition);

            int twoReadPosition = (position*column)+1;
            bind(itemViewHolder.twoSpearImageView, twoReadPosition<imageList.size()?imageList.get(twoReadPosition):null, twoReadPosition);

            int threeReadPosition = (position*column)+2;
            bind(itemViewHolder.threeSpearImageView, threeReadPosition<imageList.size()?imageList.get(threeReadPosition):null, threeReadPosition);
        }else if(viewHolder instanceof LoadMoreFooterViewHolder){
            LoadMoreFooterViewHolder footerViewHolder = (LoadMoreFooterViewHolder) viewHolder;
            if(!onLoadMoreListener.isEnd()){
                footerViewHolder.progressBar.setVisibility(View.VISIBLE);
                footerViewHolder.contextTextView.setText("别着急，您的包裹马上就来！");
                onLoadMoreListener.onLoadMore();
            }else{
                footerViewHolder.progressBar.setVisibility(View.GONE);
                footerViewHolder.contextTextView.setText("没有您的包裹了！");
            }
        }else if(viewHolder instanceof HeadViewHolder){
            HeadViewHolder headViewHolder = (HeadViewHolder) viewHolder;
            headViewHolder.spearImageView.setShowFromFlag(settings.isShowImageFromFlag());
            headViewHolder.spearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());
            headViewHolder.spearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());
            headViewHolder.spearImageView.displayImage(headImageUrl);
        }
    }

    private void bind(SpearImageView spearImageView, StarImageRequest.Image image, int position){
        if(image != null){
            spearImageView.setTag(position);
            spearImageView.displayImage(image.getSourceUrl());
            spearImageView.setVisibility(View.VISIBLE);
        }else{
            spearImageView.setVisibility(View.INVISIBLE);
        }
    }

    public void loadMoreFail(){
        if(loadMoreFooterViewHolder != null){
            loadMoreFooterViewHolder.contextTextView.setText("Sorry！您的包裹运送失败，您再试一次吧！");
            loadMoreFooterViewHolder.progressBar.setVisibility(View.GONE);
        }
    }

    private static class ItemViewHolder{
        private SpearImageView oneSpearImageView;
        private SpearImageView twoSpearImageView;
        private SpearImageView threeSpearImageView;

        @SuppressWarnings("deprecation")
        public ItemViewHolder(View itemView) {
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_two);
            threeSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_three);
        }
    }

    private static class HeadViewHolder {
        private SpearImageView spearImageView;

        public HeadViewHolder(View itemView) {
            spearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageHeaderItem);
        }
    }

    private static class LoadMoreFooterViewHolder{
        private ProgressBar progressBar;
        private TextView contextTextView;

        public LoadMoreFooterViewHolder(View itemView) {
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_loadMoreFooter);
            contextTextView = (TextView) itemView.findViewById(R.id.text_loadMoreFooter_content);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position, StarImageRequest.Image image);
    }

    public interface OnLoadMoreListener{
        boolean isEnd();
        void onLoadMore();
    }
}
