package com.joe.eyer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joe.eyer.R;
import com.joe.eyer.adapter.LanguageAdapter;
import com.joe.eyer.config.AppConfig;
import com.joe.eyer.model.LanguageData;
import com.joe.eyer.model.LanguageFactory;
import com.joe.eyer.task.DownloadTaskManager;
import com.joe.eyer.task.FileDownloadTask;
import com.joe.eyer.utils.FileUtils;
import com.joe.eyer.utils.ToastUtils;
import com.joe.eyer.utils.ZipUtils;

import java.io.File;
import java.util.List;

/**
 * Description
 * Created by chenqiao on 2016/10/14.
 */

public class LanguageManagerFragment extends Fragment {
    private RecyclerView recyclerView;
    private LanguageAdapter languageAdapter;
    private List<LanguageData> data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_language, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_language);
        languageAdapter = new LanguageAdapter();
        data = LanguageFactory.getLanguages();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.language_decode);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        languageAdapter.setData(data);
        recyclerView.setAdapter(languageAdapter);
        languageAdapter.setOnCancelClickListener(new LanguageAdapter.CancelClickListener() {
            @Override
            public void onClick(int position) {
                LanguageData item = data.get(position);
                item.setDownloading(false);
                AppConfig.getInstance().setDownloading(item.getName(), false);
                FileDownloadTask task = DownloadTaskManager.createOrGetTask(item.getName(), null, null);
                if (task != null) {
                    task.cancel();
                }
                DownloadTaskManager.removeTask(item.getName());
                refreshDownloading();
                languageAdapter.notifyItemChanged(position);
            }
        });
        languageAdapter.setEnableClickListener(new LanguageAdapter.EnableClickListener() {
            @Override
            public void onClick(int position) {
                LanguageData item = data.get(position);
                if (item.isDownloaded()) {
                    item.setEnable(!item.isEnable());
                    AppConfig.getInstance().setLanguageEnable(item.getName(), item.isEnable());
                    languageAdapter.notifyItemChanged(position);
                } else {
                    ToastUtils.show(getContext(), "请先下载");
                }
            }
        });
        languageAdapter.setDownloadOrDeleteClickListener(new LanguageAdapter.DownloadOrDeleteClickListener() {
            @Override
            public void onClick(int position) {
                LanguageData item = data.get(position);
                if (!item.isDownloaded()) {
                    String url = LanguageFactory.getLanguageDataUrl(item.getName());
                    File file = new File(getContext().getExternalCacheDir(), FileUtils.getFileName(url));
                    item.setDownloading(true);
                    FileDownloadTask task = DownloadTaskManager.createOrGetTask(item.getName(), url, file);
                    if (task != null) {
                        task.startDownload(true);
                    }
                    AppConfig.getInstance().setDownloading(item.getName(), true);
                } else {
                    AppConfig.getInstance().deleteLanguageData(item.getName());
                    item.setDownloading(false);
                    item.setDownloaded(false);
                    item.setEnable(false);
                }
                refreshDownloading();
                languageAdapter.notifyItemChanged(position);
            }
        });
        refreshDownloading();
    }

    private void refreshDownloading() {
        for (final LanguageData languageData : data) {
            if (languageData.isDownloading()) {
                FileDownloadTask task = DownloadTaskManager.createOrGetTask(languageData.getName(), null, null);
                if (task != null) {
                    task.setTag(languageData);
                    task.setDownloadListener(new FileDownloadTask.DownloadListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onDownloading(LanguageData tag, long now, long all) {
                            int percent;
                            if (all == 0) {
                                percent = 0;
                            } else {
                                percent = (int) (now * 100 / all);
                            }
                            tag.setDownloadProgress(percent);
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    languageAdapter.notifyDataSetChanged();
                                }
                            });
                            Log.d("LanguageManagerFragment", tag.getName() + " onDownloading: " + percent);
                        }

                        @Override
                        public void onDownloadFinished(LanguageData tag, File file) {
                            Log.d("LanguageManagerFragment", "onDownloadFinished: " + file.getName());
                            tag.setDownloaded(true);
                            tag.setDownloading(false);
                            AppConfig.getInstance().setDownloading(tag.getName(), false);
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    languageAdapter.notifyDataSetChanged();
                                    ToastUtils.show(getContext(), "正在解压数据");
                                }
                            });
                            try {
                                ZipUtils.unZipFolder(file.getAbsolutePath(), AppConfig.getInstance().dataDirectory.getAbsolutePath());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(LanguageData tag, Throwable throwable) {
                            tag.setDownloading(false);
                            DownloadTaskManager.removeTask(tag.getName());
                            AppConfig.getInstance().setDownloading(tag.getName(), false);
                            recyclerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.show(getContext(), "下载出错,请尝试重新下载");
                                    languageAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });
                }
            }
        }
    }
}