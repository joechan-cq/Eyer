package com.joe.eyer.adapter;

import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.joe.eyer.R;
import com.joe.eyer.model.LanguageData;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * Created by chenqiao on 2016/10/14.
 */

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private List<LanguageData> data;
    private CancelClickListener cancelClickListener;
    private DownloadOrDeleteClickListener downloadOrDeleteClickListener;
    private EnableClickListener enableClickListener;

    public LanguageAdapter() {
        data = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (position >= 0 && position < data.size()) {
            LanguageData item = data.get(position);
            holder.setName(item.getName());
            holder.isDownloaded(item.isDownloaded());
            holder.isDownloading(item.isDownloading());
            holder.isEnable(item.isEnable());
            holder.setProgress(item.getDownloadProgress());
            holder.setCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cancelClickListener != null) {
                        cancelClickListener.onClick(holder.getAdapterPosition());
                    }
                }
            });
            holder.setDownloadClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloadOrDeleteClickListener != null) {
                        downloadOrDeleteClickListener.onClick(holder.getAdapterPosition());
                    }
                }
            });
            holder.setEnableListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (enableClickListener != null) {
                        enableClickListener.onClick(holder.getAdapterPosition());
                    }
                }
            });
        } else {
            holder.setCancelListener(null);
            holder.setEnableListener(null);
            holder.setDownloadClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<LanguageData> data) {
        this.data = data;
    }

    public void setOnCancelClickListener(CancelClickListener cancelClickListener) {
        this.cancelClickListener = cancelClickListener;
    }

    public void setDownloadOrDeleteClickListener(DownloadOrDeleteClickListener downloadOrDeleteClickListener) {
        this.downloadOrDeleteClickListener = downloadOrDeleteClickListener;
    }

    public void setEnableClickListener(EnableClickListener enableClickListener) {
        this.enableClickListener = enableClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView nameTv, enableTv, downloadOrDeleteTv, progressTv, cancelTv;

        private LinearLayout downloadingLayout, actionLayout;

        private ProgressBar pb;

        ViewHolder(View itemView) {
            super(itemView);
            nameTv = (AppCompatTextView) itemView.findViewById(R.id.tv_item_name);
            downloadOrDeleteTv = (AppCompatTextView) itemView.findViewById(R.id.tv_item_download_delete);
            enableTv = (AppCompatTextView) itemView.findViewById(R.id.tv_item_enable);
            progressTv = (AppCompatTextView) itemView.findViewById(R.id.tv_pb_downloading);
            cancelTv = (AppCompatTextView) itemView.findViewById(R.id.tv_item_cancel);
            pb = (ProgressBar) itemView.findViewById(R.id.pb_downloading);
            downloadingLayout = (LinearLayout) itemView.findViewById(R.id.layout_downloading);
            actionLayout = (LinearLayout) itemView.findViewById(R.id.layout_actions);
        }

        public void setName(String name) {
            nameTv.setText(name);
        }

        void isDownloading(boolean isDownloading) {
            if (isDownloading) {
                downloadingLayout.setVisibility(View.VISIBLE);
                actionLayout.setVisibility(View.GONE);
            } else {
                downloadingLayout.setVisibility(View.GONE);
                actionLayout.setVisibility(View.VISIBLE);
            }
        }

        void isDownloaded(boolean isDownloaded) {
            if (isDownloaded) {
                downloadOrDeleteTv.setText("删除");
                downloadOrDeleteTv.setCompoundDrawablesWithIntrinsicBounds(downloadOrDeleteTv.getContext().getResources().getDrawable(R.drawable.ic_delete_forever_black_24dp), null, null, null);
            } else {
                downloadOrDeleteTv.setText("下载");
                downloadOrDeleteTv.setCompoundDrawablesWithIntrinsicBounds(downloadOrDeleteTv.getContext().getResources().getDrawable(R.drawable.ic_file_download_black_24dp), null, null, null);
            }
        }

        void isEnable(boolean isEnable) {
            if (!isEnable) {
                itemView.setBackgroundColor(Color.WHITE);
                enableTv.setText("启用");
                enableTv.setCompoundDrawablesWithIntrinsicBounds(enableTv.getContext().getResources().getDrawable(R.drawable.ic_done_black_24dp), null, null, null);
            } else {
                itemView.setBackgroundResource(R.color.colorPrimary);
                enableTv.setText("禁用");
                enableTv.setCompoundDrawablesWithIntrinsicBounds(enableTv.getContext().getResources().getDrawable(R.drawable.ic_close_black_24dp), null, null, null);
            }
        }

        void setProgress(int num) {
            pb.setProgress(num);
            progressTv.setText(String.format("%d%%", num));
        }

        void setCancelListener(View.OnClickListener listener) {
            cancelTv.setOnClickListener(listener);
        }

        void setEnableListener(View.OnClickListener listener) {
            enableTv.setOnClickListener(listener);
        }

        void setDownloadClickListener(View.OnClickListener listener) {
            downloadOrDeleteTv.setOnClickListener(listener);
        }
    }

    public interface CancelClickListener {
        void onClick(int position);
    }

    public interface DownloadOrDeleteClickListener {
        void onClick(int position);
    }

    public interface EnableClickListener {
        void onClick(int position);
    }
}